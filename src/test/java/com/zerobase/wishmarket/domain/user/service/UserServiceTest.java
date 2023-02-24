package com.zerobase.wishmarket.domain.user.service;


import com.zerobase.wishmarket.domain.user.model.dto.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.dto.UpdateForm;
import com.zerobase.wishmarket.domain.user.model.dto.UserDto;
import com.zerobase.wishmarket.domain.user.model.entity.DeliveryAddress;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void userDetail() {

        given(userRepository.findByUserId(1L))
                .willReturn(Optional.ofNullable(UserEntity.builder()
                        .userId(1L)
                        .name("starbucks")
                        .email("test@test.naver.com")
                        .nickName("coffee")
                        .phone("010-1234-5678")
                        .userRegistrationType(UserRegistrationType.EMAIL)
                        .userStatusType(UserStatusType.ACTIVE)
                        .build()));

        UserEntity originUser = userRepository.findByUserId(1L).get();

        DeliveryAddress updateAddress = DeliveryAddress.builder()
                .baseAddress("금천구 독산동 124")
                .detailAddress("204호")
                .build();

        UserDto detailUser = UserDto.from(UserEntity.builder()
                .userId(originUser.getUserId())
                .name(originUser.getName())
                .nickName(originUser.getNickName())
                .email(originUser.getEmail())
                .pointPrice(originUser.getPointPrice())
                .deliveryAddress(updateAddress)
                .phone(originUser.getPhone())
                .profileImage(originUser.getProfileImage())
                .userRegistrationType(originUser.getUserRegistrationType())
                .build());

        assertEquals(1L, detailUser.getId());
        assertEquals("coffee", detailUser.getNickName());
        assertEquals("010-1234-5678", detailUser.getPhone());
        assertEquals(UserRegistrationType.EMAIL, detailUser.getUserRegistrationType());
    }

    @Test
    void passwordChange() {

        // given
        String email = "test@test.com";
        String password = "tomato";
        String encodePassword = this.passwordEncoder.encode(password);
        UserRegistrationType userRegistrationType = UserRegistrationType.EMAIL;

        given(userRepository.save(any()))
                .willReturn(UserEntity.builder()
                        .userId(1L)
                        .email(email)
                        .password(encodePassword)
                        .userRegistrationType(userRegistrationType)
                        .build());

        given(userRepository.findByEmailAndUserRegistrationType("test@test.com", UserRegistrationType.EMAIL))
                .willReturn(Optional.ofNullable(UserEntity.builder()
                        .userId(1L)
                        .email(email)
                        .password(encodePassword)
                        .userRegistrationType(userRegistrationType)
                        .build()));

        // when
        ChangePwdForm form = ChangePwdForm.builder()
                .email("test@test.com")
                .password("potato")
                .build();
        userService.passwordChange(form);

        // then
        assertEquals(true, this.passwordEncoder.matches("potato",
                userRepository.findByEmailAndUserRegistrationType(form.getEmail(), UserRegistrationType.EMAIL).get().getPassword()));

    }

    @Test
    void updateUserInfo() {

        // given
        given(userRepository.findByUserId(3L))
                .willReturn(Optional.ofNullable(UserEntity.builder()
                        .userId(3L)
                        .name("tomato")
                        .nickName("defaultNickName")
                        .phone("010-1234-5678")
                        .build())
                );

        // when
        UserEntity originUser = userRepository.findByUserId(3L).get();

        UpdateForm form = UpdateForm.builder()
                .nickName("changeNickName")
                .phone("010-9876-5432")
                .baseAddress("서울특별시 영등포구 영중로 116")
                .detailAddress("래시오 오피스텔 1221호")
                .build();

        DeliveryAddress updateAddress = DeliveryAddress.builder()
                .baseAddress(form.getBaseAddress())
                .detailAddress(form.getDetailAddress())
                .build();

        UserDto updateUser = UserDto.from(UserEntity.builder()
                .userId(originUser.getUserId())
                .name(originUser.getName())
                .nickName(form.getNickName())
                .email(originUser.getEmail())
                .pointPrice(originUser.getPointPrice())
                .deliveryAddress(updateAddress)
                .phone(form.getPhone())
                .profileImage(form.getProfileImageUrl())
                .userRegistrationType(originUser.getUserRegistrationType())
                .build());

        // then
        assertEquals(updateUser.getId(), 3L);
        assertEquals(updateUser.getName(), "tomato");
        assertEquals(updateUser.getNickName(), "changeNickName");
        assertEquals(updateUser.getPhone(), "010-9876-5432");
        assertEquals(updateUser.getAddress(), "서울특별시 영등포구 영중로 116 래시오 오피스텔 1221호");
    }
}