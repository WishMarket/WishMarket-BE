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
        // ?????? ????????? ??????
        if (checkInvalidEmail(form.getEmail())) {
            throw new UserException(INVALID_EMAIL_FORMAT);
        }

        // ?????? ?????? ??????
        Optional<UserEntity> optionalUser = userAuthRepository.findByEmailAndUserRegistrationType(
            form.getEmail(),
            UserRegistrationType.EMAIL
        );

        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();

            // ?????? ?????? ?????? ?????? ?????? => ?????? ????????? ??????
            if (form.getType().equals("signUp") && userEntity.getUserStatusType() == UserStatusType.ACTIVE) {
                throw new UserException(ALREADY_REGISTER_USER);
            }else if(form.getType().equals("passwordChange")&& userEntity.getUserStatusType() == UserStatusType.ACTIVE){
                sendMail(form.getEmail());
                return AuthCodeResponse.builder()
                    .message(AUTH_MAIL_SEND_SUCCESS)
                    .build();
            }
        }else{ // ?????? ?????????
            // ?????? ???????????? ?????? ??????
            if(form.getType().equals("signUp")){
                sendMail(form.getEmail());

                return AuthCodeResponse.builder()
                    .message(AUTH_MAIL_SEND_SUCCESS)
                    .build();
            }else{ // ???????????? ???????????? ??????
                throw new UserException(USER_NOT_FOUND);
            }
        }

        return null;
    }

    private void sendMail(String email){
        String authCode = getRandomAuthCode();
        String key = KEY_PREFIX + email;

        // client?????? ?????? ?????? ?????? ?????? 3??? ?????? ???????????? ??????????????? ?????? ?????? ????????? ???
        // ?????? key ?????? ??? ?????? ??????
        if (redisClient.hasKey(key)) {
            redisClient.del(key);
        }

        // ?????? ?????? ?????? ??? redis??? ??? ??????
        redisClient.put(key, authCode, TimeUnit.MILLISECONDS, REDIS_AUTH_CODE_EXPIRE_TIME);

        mailComponents.sendAuthCodeMail(email, authCode);
    }

    public AuthCodeResponse authCodeVerify(AuthCodeVerifyForm form) {
        String key = KEY_PREFIX + form.getEmail();

        // key ?????? ??????
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
