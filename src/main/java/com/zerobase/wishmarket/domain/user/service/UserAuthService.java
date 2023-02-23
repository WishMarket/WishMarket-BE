package com.zerobase.wishmarket.domain.user.service;


import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_PREFIX;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.TOKEN_PREFIX;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.KEY_PREFIX;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.EMAIL_NOT_FOUND;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.INVALID_EMAIL_FORMAT;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.INVALID_PASSWORD_FORMAT;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.PASSWORD_DO_NOT_MATCH;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;
import static com.zerobase.wishmarket.exception.CommonErrorCode.NOT_VERIFICATION_AUTH_CODE;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.common.jwt.model.dto.TokenSetDto;
import com.zerobase.wishmarket.common.redis.RedisClient;
import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import com.zerobase.wishmarket.domain.follow.repository.FollowInfoRepository;
import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.EmailCheckForm;
import com.zerobase.wishmarket.domain.user.model.dto.EmailCheckResponse;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.SignInForm;
import com.zerobase.wishmarket.domain.user.model.dto.SignInResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import com.zerobase.wishmarket.exception.GlobalException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final FollowInfoRepository followInfoRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtProvider;
    private final RedisClient redisClient;
    private final RedisTemplate redisTemplate;

    private static final String EMAIL_USING_STATUS = "사용 가능한 이메일입니다.";

    @Transactional
    public SignUpEmailResponse signUp(SignUpForm form) {
        String key = KEY_PREFIX + form.getName() + form.getEmail();
        String value = redisClient.getAutoCode(key);

        if (value == null || !value.equals(form.getEmail())) {
            throw new GlobalException(NOT_VERIFICATION_AUTH_CODE);
        }

        if (checkInvalidPassword(form.getPassword())) {
            throw new UserException(INVALID_PASSWORD_FORMAT);
        }

        // 한번 탈퇴했던 회원이라면
        // UserStatus : withdrawal -> active
        Optional<UserEntity> optionalUser = userAuthRepository.findByEmailAndUserRegistrationType(form.getEmail(),
            UserRegistrationType.EMAIL);

        if (optionalUser.isPresent()) {

            UserEntity userEntity = optionalUser.get();

            if (userEntity.getUserStatusType() == UserStatusType.WITHDRAWAL) {
                userEntity.setUserStatusType(UserStatusType.ACTIVE);

                return SignUpEmailResponse.from(userAuthRepository.save(userEntity));
            }

        }

        form.setPassword(this.passwordEncoder.encode(form.getPassword()));

        FollowInfo empthFollowInfo = FollowInfo.builder()
            .followerCount(0L)
            .followCount(0L)
            .build();

        followInfoRepository.save(empthFollowInfo);

        redisClient.del(key);

        return SignUpEmailResponse.from(
            userAuthRepository.save(
                UserEntity.of(form, UserRegistrationType.EMAIL, UserStatusType.ACTIVE, empthFollowInfo))
        );
    }

    public EmailCheckResponse emailCheck(EmailCheckForm form) {
        if (checkInvalidEmail(form.getEmail())) {
            throw new UserException(INVALID_EMAIL_FORMAT);
        }

        Optional<UserEntity> optionalUser = userAuthRepository.findByEmailAndUserRegistrationType(
            form.getEmail(),
            UserRegistrationType.EMAIL
        );

        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();

            // 회원 정보 존재 -> 활동중
            if (userEntity.getUserStatusType() == UserStatusType.ACTIVE) {
                throw new UserException(ALREADY_REGISTER_USER);
            }
        }

        // 1. 정보가 없음. 가입 가능한 이메일입니다.
        // 2. 탈퇴 했던 회원 withdrawal => 사용 가능.
        return EmailCheckResponse.builder()
            .status(EMAIL_USING_STATUS)
            .build();
    }

    @Transactional
    public SignInResponse signInEmail(SignInForm form) {
        UserEntity user = userAuthRepository.findByEmailAndUserRegistrationType(
                form.getEmail(),
                UserRegistrationType.EMAIL
            )
            .orElseThrow(() -> new UserException(EMAIL_NOT_FOUND));

        validationPassword(form.getPassword(), user);

        TokenSetDto tokenSetDto = jwtProvider.generateTokenSet(user.getUserId());

        // 작성된 날짜에서 현재 날짜를 빼고 밀리초로 나누면 지나간 시간(초)이 계산
        long expirationSeconds = (tokenSetDto.getRefreshTokenExpiredAt().getTime() - new Date().getTime()) / 1000;

        // redis에 refresh토큰 저장
        redisClient.put(
            REFRESH_TOKEN_PREFIX + user.getUserId(),
            tokenSetDto.getRefreshToken(),
            TimeUnit.SECONDS,
            expirationSeconds);

        return SignInResponse.builder()
            .email(user.getEmail())
            .name(user.getName())
            .accessToken(TOKEN_PREFIX + tokenSetDto.getAccessToken())
            .accessTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getAccessToken())))
            .refreshToken(tokenSetDto.getRefreshToken())
            .refreshTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getRefreshToken())))
            .build();
    }

    @Transactional
    public SignInResponse signInGoogle(@LoginUserInfo OAuthUserInfo userInfo) {
        UserEntity user = userAuthRepository.findByEmailAndUserRegistrationType(
                        userInfo.getEmail(),
                        UserRegistrationType.GOOGLE
                )
                .orElseThrow(() -> new UserException(EMAIL_NOT_FOUND));

        TokenSetDto tokenSetDto = jwtProvider.generateTokenSet(user.getUserId());

        // 작성된 날짜에서 현재 날짜를 빼고 밀리초로 나누면 지나간 시간(초)이 계산
        long expirationSeconds = (tokenSetDto.getRefreshTokenExpiredAt().getTime() - new Date().getTime()) / 1000;

        // redis에 refresh토큰 저장
        redisClient.put(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                tokenSetDto.getRefreshToken(),
                TimeUnit.SECONDS,
                expirationSeconds);

        return SignInResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(TOKEN_PREFIX + tokenSetDto.getAccessToken())
                .accessTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getAccessToken())))
                .refreshToken(tokenSetDto.getRefreshToken())
                .refreshTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getRefreshToken())))
                .build();
    }

    @Transactional
    public SignInResponse signInNaver(@LoginUserInfo OAuthUserInfo userInfo) {
        UserEntity user = userAuthRepository.findByEmailAndUserRegistrationType(
                        userInfo.getEmail(),
                        UserRegistrationType.NAVER
                )
                .orElseThrow(() -> new UserException(EMAIL_NOT_FOUND));

        TokenSetDto tokenSetDto = jwtProvider.generateTokenSet(user.getUserId());

        // 작성된 날짜에서 현재 날짜를 빼고 밀리초로 나누면 지나간 시간(초)이 계산
        long expirationSeconds = (tokenSetDto.getRefreshTokenExpiredAt().getTime() - new Date().getTime()) / 1000;

        // redis에 refresh토큰 저장
        redisClient.put(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                tokenSetDto.getRefreshToken(),
                TimeUnit.SECONDS,
                expirationSeconds);

        return SignInResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(TOKEN_PREFIX + tokenSetDto.getAccessToken())
                .accessTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getAccessToken())))
                .refreshToken(tokenSetDto.getRefreshToken())
                .refreshTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getRefreshToken())))
                .build();
    }

    @Transactional
    public void logout(SignInResponse signInResponse) {
        // 로그아웃 하고 싶은 토큰이 유효한 지 먼저 검증하기
        if (!jwtProvider.isValidationToken(signInResponse.getAccessToken())) {
            throw new IllegalArgumentException("로그아웃 : 유효하지 않은 토큰입니다.");
        }

        // Access Token에서 User email을 가져온다
        Authentication authentication = jwtProvider.getAuthentication(signInResponse.getAccessToken());

        // Redis에서 해당 User email로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token을 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        Long expiration = jwtProvider.getExpiredDate(String.valueOf(signInResponse.getAccessTokenExpiredAt()))
            .getTime();
        redisTemplate.opsForValue().set(signInResponse.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

    }


    private void validationPassword(String password, UserEntity user) {
        if (!this.passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(PASSWORD_DO_NOT_MATCH);
        }
    }

    // 유저 Email + 가입 방식을 기반으로 중복 확인
    private boolean isEmailExist(String email) {
        return userAuthRepository.existsByEmailAndUserRegistrationType(
            email,
            UserRegistrationType.EMAIL);
    }

    private boolean checkInvalidEmail(String email) {
        return !Pattern.matches("^[a-zA-Z.].+[@][a-zA-Z].+[.][a-zA-Z]{2,4}$", email);
    }

    private boolean checkInvalidPassword(String password) {
        return !Pattern.matches("^.{8,}$", password);
    }


}
