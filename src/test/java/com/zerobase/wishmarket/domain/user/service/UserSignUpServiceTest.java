package com.zerobase.wishmarket.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthService userSignUpService;


    @Test
    void SignUp_Success() {
        //given
        SignUpForm form = SignUpForm.builder()
            .email("test@naver.com")
            .name("test")
            .nickName("근것")
            .password("won9975744!")
            .build();

        String encryptedPw = this.passwordEncoder.encode(form.getPassword());

        given(userAuthRepository.findByEmailAndUserRegistrationType(anyString(), any()))
            .willReturn(Optional.empty());

        given(userAuthRepository.existsByEmailAndUserRegistrationType(anyString(), any()))
            .willReturn(false);

        given(userAuthRepository.save(any()))
            .willReturn(UserEntity.builder()
                .userId(1L)
                .email("test@naver.com")
                .name("test")
                .password(encryptedPw)
                .build());

        // save에서 저장되는 실제 계좌는 captor 안으로 들어감.

        //when
        SignUpEmailResponse signUpEmailResponse = userSignUpService.signUp(form);

        //then
        assertEquals(1L, signUpEmailResponse.getId());
        assertEquals("test@naver.com", signUpEmailResponse.getEmail());
        assertEquals("test", signUpEmailResponse.getName());
    }

    @Test
    void EmailExist_Fail() {
        // given
        SignUpForm form = SignUpForm.builder()
            .email("test@naver.com")
            .name("test")
            .password("won9975744!")
            .build();

        given(userAuthRepository.existsByEmailAndUserRegistrationType(anyString(), any()))
            .willReturn(true);

        //when
        UserException userException = assertThrows(UserException.class,
            () -> userSignUpService.signUp(form));

        //then
        assertEquals(UserErrorCode.ALREADY_REGISTER_USER, userException.getUserErrorCode());
    }

    public UserEntity createWithdrawalUserEntity(String encryptedPw) {
        return UserEntity.builder()
            .userId(10L)
            .name("test")
            .email("test@naver.com")
            .nickName("근것")
            .password(encryptedPw)
            .userStatusType(UserStatusType.WITHDRAWAL)
            .build();
    }


}