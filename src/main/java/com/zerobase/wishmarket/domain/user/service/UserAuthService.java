package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.common.jwt.model.dto.TokenSetDto;
import com.zerobase.wishmarket.common.redis.RedisClient;
import com.zerobase.wishmarket.domain.authcode.exception.AuthException;
import com.zerobase.wishmarket.domain.follow.model.entity.FollowInfo;
import com.zerobase.wishmarket.domain.follow.repository.FollowInfoRepository;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.*;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.model.type.UserWithdrawalReturnType;
import com.zerobase.wishmarket.domain.user.oauth.*;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import com.zerobase.wishmarket.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.*;
import static com.zerobase.wishmarket.domain.authcode.exception.AuthErrorCode.INVALID_AUTH_CODE;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.KEY_PREFIX;
import static com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType.ING;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.*;
import static com.zerobase.wishmarket.exception.CommonErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final InMemoryProviderCustom inMemoryProviderCustom;

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

    public SignInResponse signInSocial(String providerName, String code) {
        // 프론트에서 전달받은 provider 이름으로 inMemoryProviderCustom 에서 OauthProvider 가져오기
        OauthProvider provider = inMemoryProviderCustom.findByProviderName(providerName);

        // Authorization Code 를 통해 OAuth Access Token 가져오기
        OauthTokenResponse tokenResponse = getToken(code, provider);

        // Access Token 을 활용해 유저 정보 가져오기
        OAuthUserProfile oAuthUserProfile = getUserProfile(providerName, tokenResponse, provider);

        // 유저 정보 데이터베이스에 저장
        UserEntity user = saveOrUpdate(oAuthUserProfile);

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

    public UserWithdrawalReturnType withdrawal(Long userId, String accessToken) {

        Optional<UserEntity> userEntity = userAuthRepository.findById(userId);

        if (!userEntity.isPresent()) {
            return UserWithdrawalReturnType.WITHDRAWAL_FAIL;
        } else if (ObjectUtils.isEmpty(accessToken) || !(accessToken.startsWith(ACCESS_TOKEN_PREFIX))) {
            return UserWithdrawalReturnType.WITHDRAWAL_FAIL;
        } else {

            List<Funding> fundingList = userEntity.get().getFundingList();
            List<Funding> targetList = userEntity.get().getFundingTargetList();

            for (int i = 0; i < fundingList.size(); i++) {
                // 펀딩의 시작한 사람이 탈퇴하려는 유저인 경우
                // 펀딩이 진행중이건 종료되었건 관계 X
                if (fundingList.get(i).getUser().getUserId() == userEntity.get().getUserId()) {
                    // 시작한 사람을 Null 로 변경하고 연관관계 해제
                    fundingList.get(i).setStartUserWithdrawal();
                }
            }

            for (int i = 0; i < targetList.size(); i++) {
                // 탈퇴하려는 유저가 펀딩의 target 인 경우
                if (userEntity.get().getUserId() == targetList.get(i).getTargetUser().getUserId()) {
                    // 펀딩이 진행중이면 탈퇴 불가
                    if (ING.equals(targetList.get(i).getFundingStatusType())) {
                        throw new UserException(CANNOT_WITHDRAW_YOU_ARE_TARGET_TO_ONGOING_FUNDING);
                    }
                    // 탈퇴하려는 유저가 받은 선물 리스트 중 배송지를 입력하지 않은 상품이 있는 경우 탈퇴 불가
                    else if (!targetList.get(i).getFundingStatusType().equals(ING) &&
                            !targetList.get(i).getFundedStatusType().equals(FundedStatusType.COMPLETION)) {
                        throw new UserException(YOUR_GIFT_HAS_NOT_BEEN_PROCESSED);
                    }
                    // 종료된 펀딩이면서 배송지 입력까지 마쳤을 경우 탈퇴 가능
                    else if (!targetList.get(i).getFundingStatusType().equals(ING) &&
                            targetList.get(i).getFundedStatusType().equals(FundedStatusType.COMPLETION)) {
                        targetList.get(i).setTargetUserWithdrawal();
                    }
                }
            }
        }

        // 로그인한 유저의 userId 와 일치하는 Entity 삭제
        userAuthRepository.delete(userEntity.get());

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

        return UserWithdrawalReturnType.WITHDRAWAL_SUCCESS;
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
                .accessToken(ACCESS_TOKEN_PREFIX + tokenSetDto.getAccessToken())
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
                    .accessToken(ACCESS_TOKEN_PREFIX + tokenSetDto.getAccessToken())
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

    private UserEntity saveOrUpdate(OAuthUserProfile userProfile) {
        UserEntity userEntity = userAuthRepository.findByUserRegistrationType(userProfile.getUserRegistrationType())
                .map(entity -> entity.update(
                        userProfile.getEmail(), userProfile.getProfileImageUrl()))
                .orElseGet(userProfile::toEntity);
        return userAuthRepository.save(userEntity);
    }

    private OauthTokenResponse getToken(String code, OauthProvider provider) {
        return WebClient.create()
                .post()
                .uri(provider.getTokenUrl())
                .headers(header -> {
                    header.setBasicAuth(provider.getClientId(), provider.getClientSecret());
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code, provider))
                .retrieve()
                .bodyToMono(OauthTokenResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code, OauthProvider provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUrl());
        return formData;
    }

    private OAuthUserProfile getUserProfile(String providerName, OauthTokenResponse tokenResponse, OauthProvider provider) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);
        return OauthAttributes.extract(providerName, userAttributes);
    }

    // OAuth 서버에서 유저 정보 map으로 가져오기
    private Map<String, Object> getUserAttributes(OauthProvider provider, OauthTokenResponse tokenResponse) {
        return WebClient.create()
                .get()
                .uri(provider.getUserInfoUrl())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

}
