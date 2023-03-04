package com.zerobase.wishmarket.domain.authcode.service;

import static com.zerobase.wishmarket.domain.authcode.exception.AuthErrorCode.INVALID_AUTH_CODE;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.AUTH_CODE_LENGTH;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.AUTH_CODE_VERIFICATION_SUCCESS;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.AUTH_MAIL_SEND_SUCCESS;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.KEY_PREFIX;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.REDIS_AUTH_CODE_EXPIRE_TIME;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.INVALID_EMAIL_FORMAT;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_KEY;

import com.zerobase.wishmarket.common.redis.RedisClient;
import com.zerobase.wishmarket.common.component.MailComponents;
import com.zerobase.wishmarket.domain.authcode.exception.AuthException;
import com.zerobase.wishmarket.domain.authcode.model.form.AuthCodeMailForm;
import com.zerobase.wishmarket.domain.authcode.model.dto.AuthCodeResponse;
import com.zerobase.wishmarket.domain.authcode.model.form.AuthCodeVerifyForm;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import com.zerobase.wishmarket.exception.GlobalException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCodeService {


    private final MailComponents mailComponents;
    private final RedisClient redisClient;

    private final UserAuthRepository userAuthRepository;

    @Transactional
    public AuthCodeResponse sendAuthCode(AuthCodeMailForm form) {
        // 메일 정규식 확인
        if (checkInvalidEmail(form.getEmail())) {
            throw new UserException(INVALID_EMAIL_FORMAT);
        }

        // 메일 중복 확인
        Optional<UserEntity> optionalUser = userAuthRepository.findByEmailAndUserRegistrationType(
            form.getEmail(),
            UserRegistrationType.EMAIL
        );

        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();

            // 회원 가입 회원 정보 존재 => 이미 가입된 회원
            if (form.getType().equals("signUp") && userEntity.getUserStatusType() == UserStatusType.ACTIVE) {
                throw new UserException(ALREADY_REGISTER_USER);
            }else if(form.getType().equals("passwordChange")&& userEntity.getUserStatusType() == UserStatusType.ACTIVE){
                sendMail(form.getEmail());
                return AuthCodeResponse.builder()
                    .message(AUTH_MAIL_SEND_SUCCESS)
                    .build();
            }
        }else{ // 회원 없으면
            // 회원 가입이면 메일 전송
            if(form.getType().equals("signUp")){
                sendMail(form.getEmail());

                return AuthCodeResponse.builder()
                    .message(AUTH_MAIL_SEND_SUCCESS)
                    .build();
            }else{ // 비밀번호 변경이면 에러
                throw new UserException(USER_NOT_FOUND);
            }
        }

        return null;
    }

    private void sendMail(String email){
        String authCode = getRandomAuthCode();
        String key = KEY_PREFIX + email;

        // client에서 인증 코드 만료 시간 3분 안에 페이지를 벗어났다가 인증 코드 재요청 시
        // 해당 key 삭제 후 메일 전송
        if (redisClient.hasKey(key)) {
            redisClient.del(key);
        }

        // 메일 전송 성공 시 redis에 키 저장
        redisClient.put(key, authCode, TimeUnit.MILLISECONDS, REDIS_AUTH_CODE_EXPIRE_TIME);

        mailComponents.sendAuthCodeMail(email, authCode);
    }

    public AuthCodeResponse authCodeVerify(AuthCodeVerifyForm form) {
        String key = KEY_PREFIX + form.getEmail();

        // key 시간 만료
        if (!redisClient.hasKey(key)) {
            throw new GlobalException(EXPIRED_KEY);
        }

        String verifiedAuthCode = redisClient.getAutoCode(key);

        if (!verifiedAuthCode.equals(form.getCode())) {
            throw new AuthException(INVALID_AUTH_CODE);
        }

        redisClient.put(key, form.getEmail());

        return AuthCodeResponse.builder()
            .message(AUTH_CODE_VERIFICATION_SUCCESS)
            .build();
    }

    private String getRandomAuthCode() {
        return RandomStringUtils.random(AUTH_CODE_LENGTH, true, true);
    }

    private boolean checkInvalidEmail(String email) {
        return !Pattern.matches("^[a-zA-Z.].+[@][a-zA-Z].+[.][a-zA-Z]{2,4}$", email);
    }
}
