package com.zerobase.wishmarket.domain.user.service;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.TOKEN_PREFIX;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.EMAIL_NOT_FOUND;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.INVALID_EMAIL_FORMAT;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.INVALID_PASSWORD_FORMAT;
import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.PASSWORD_DO_NOT_MATCH;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.common.jwt.model.dto.TokenSetDto;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.SignInForm;
import com.zerobase.wishmarket.domain.user.model.dto.SignInResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtProvider;

    @Transactional
    public SignUpEmailResponse signUp(SignUpForm form) {
        if (checkInvalidEmail(form.getEmail())) {
            throw new UserException(INVALID_EMAIL_FORMAT);
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

        if (isEmailExist(form.getEmail())) {
            throw new UserException(ALREADY_REGISTER_USER);
        }

        form.setPassword(this.passwordEncoder.encode(form.getPassword()));

        return SignUpEmailResponse.from(
            userAuthRepository.save(UserEntity.of(form, UserRegistrationType.EMAIL))
        );

    }

    @Transactional
    public SignInResponse signIn(SignInForm form) {
        UserEntity user = userAuthRepository.findByEmailAndUserRegistrationType(
                form.getEmail(),
                UserRegistrationType.EMAIL
            )
            .orElseThrow(() -> new UserException(EMAIL_NOT_FOUND));

        validationPassword(form.getPassword(), user);
        TokenSetDto tokenSetDto = jwtProvider.generateTokenSet(user.getUserId());

        return SignInResponse.builder()
            .email(user.getEmail())
            .name(user.getName())
            .accessToken(TOKEN_PREFIX + tokenSetDto.getAccessToken())
            .refreshToken(tokenSetDto.getRefreshToken())
            .build();
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
