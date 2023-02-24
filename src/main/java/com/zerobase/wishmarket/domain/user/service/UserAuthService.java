package com.zerobase.wishmarket.domain.user.service;


import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_REFRESH_TOKEN_REISSUE_TIME;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_BLACK_LIST_PREFIX;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_PREFIX;
import static com.zerobase.wishmarket.domain.authcode.exception.AuthErrorCode.INVALID_AUTH_CODE;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.KEY_PREFIX;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.EMAIL_NOT_FOUND;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.INVALID_PASSWORD_FORMAT;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.PASSWORD_DO_NOT_MATCH;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_KEY;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.INVALID_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.NOT_EXPIRED_ACCESS_TOKEN;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.common.jwt.model.dto.TokenSetDto;
import com.zerobase.wishmarket.common.redis.RedisClient;
import com.zerobase.wishmarket.domain.authcode.exception.AuthException;
import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import com.zerobase.wishmarket.domain.follow.repository.FollowInfoRepository;
import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.LogoutResponse;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.ReissueResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final FollowInfoRepository followInfoRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtProvider;
    private final RedisClient redisClient;

    private static final String EMAIL_USING_STATUS = "사용 가능한 이메일입니다.";
    private static final String LOGOUT_MESSAGE = "로그아웃 되셨습니다.";

    @Transactional
    public SignUpEmailResponse signUp(SignUpForm form) {
        String key = KEY_PREFIX + form.getEmail();
        authCodeVerification(key, form);

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
            .accessToken(ACCESS_TOKEN_PREFIX + tokenSetDto.getAccessToken())
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
                .accessToken(ACCESS_TOKEN_PREFIX + tokenSetDto.getAccessToken())
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
                .accessToken(ACCESS_TOKEN_PREFIX + tokenSetDto.getAccessToken())
                .accessTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getAccessToken())))
                .refreshToken(tokenSetDto.getRefreshToken())
                .refreshTokenExpiredAt(String.valueOf(jwtProvider.getExpiredDate(tokenSetDto.getRefreshToken())))
                .build();

    }


    @Transactional
    public LogoutResponse logout(Long userId, String accessToken) {

        if (!ObjectUtils.isEmpty(accessToken) && accessToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            accessToken = accessToken.substring(ACCESS_TOKEN_PREFIX.length());
        }

        // Redis에서 해당 User id로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        if (redisClient.getRefreshToken(REFRESH_TOKEN_PREFIX + userId) != null) {
            // Refresh Token을 삭제
            redisClient.del(REFRESH_TOKEN_PREFIX + userId);
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        long expirationSeconds = (jwtProvider.getExpiredDate(accessToken).getTime() - new Date().getTime()) / 1000;
        if (expirationSeconds > 0) {
            redisClient.put(ACCESS_TOKEN_BLACK_LIST_PREFIX + accessToken, String.valueOf(userId), TimeUnit.SECONDS,
                expirationSeconds);
        }

        return LogoutResponse.builder()
            .message(LOGOUT_MESSAGE)
            .build();
    }


    public ReissueResponse reissue(String accessToken, String refreshToken) {

        if (!ObjectUtils.isEmpty(accessToken) && accessToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            accessToken = accessToken.substring(ACCESS_TOKEN_PREFIX.length());
        }

        // 만료 되지 않은 Access Token이면 에러
        if (!jwtProvider.isExpiredToken(accessToken)) {
            throw new GlobalException(NOT_EXPIRED_ACCESS_TOKEN);
        }

        Long userId = Long.valueOf(jwtProvider.getUserId(accessToken));

        UserEntity user = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 1. Refresh Token 유효성 검사
        // 1-1. 유효하지 않거나 만료된 Refresh Token 일 시 Error Response
        if (jwtProvider.isExpiredToken(refreshToken)) {
            throw new GlobalException(EXPIRED_REFRESH_TOKEN); // 리프레쉬까지 만료되면 재 로그인 =>  로그아웃 처리
        }

        if (!redisClient.validationRefreshToken(REFRESH_TOKEN_PREFIX + user.getUserId(), refreshToken)) {
            throw new GlobalException(INVALID_TOKEN);
        }

        // 2. Access Token 재발급
        TokenSetDto tokenSetDto = jwtProvider.generateAccessToken(user.getUserId());

        ReissueResponse reissueResponse = ReissueResponse.builder()
            .email(user.getEmail())
            .name(user.getName())
            .accessToken(tokenSetDto.getAccessToken())
            .accessTokenExpiredAt(String.valueOf(tokenSetDto.getAccessTokenExpiredAt()))
            .build();

        // 3. 현재시간과 Refresh Token의 만료일을 통해 남은 만료기간 계산
        long now = new Date().getTime();

        long refreshExpireTime = jwtProvider.getExpiredDate(refreshToken).getTime();

        // 4. Refresh Token의 남은 만료기간이 15일 미만일 시 Refresh Token도 재발급
        if (refreshExpireTime - now < ACCESS_REFRESH_TOKEN_REISSUE_TIME) {
            tokenSetDto = jwtProvider.generateTokenSet(user.getUserId());
            reissueResponse = ReissueResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(tokenSetDto.getAccessToken())
                .accessTokenExpiredAt(String.valueOf(tokenSetDto.getAccessTokenExpiredAt()))
                .refreshToken(tokenSetDto.getRefreshToken())
                .refreshTokenExpiredAt(String.valueOf(tokenSetDto.getRefreshTokenExpiredAt()))
                .build();
        }

        return reissueResponse;
    }

    public void authCodeVerification(String key, SignUpForm form) {
        String value = redisClient.getAutoCode(key);

        // key 시간 만료
        if (!redisClient.hasKey(key)) {
            throw new GlobalException(EXPIRED_KEY);
        }

        String verifiedAuthCode = redisClient.getAutoCode(key);

        if (value == null || !verifiedAuthCode.equals(form.getCode())) {
            throw new AuthException(INVALID_AUTH_CODE);
        }

        redisClient.put(key, form.getEmail());

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


    private boolean checkInvalidPassword(String password) {
        return !Pattern.matches("^.{8,}$", password);
    }


}
