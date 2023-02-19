package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.model.dto.UserDto;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserDto userInfo;
    @Mock
    private Optional<UserEntity> userEntity;

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

        userEntity = userRepository.findByUserId(1L);
        userInfo = userService.userDetail(UserDto.from(userEntity.get()).getId());

        assertEquals(1L, userInfo.getId());
        assertEquals("coffee", userInfo.getNickName());
        assertEquals("010-1234-5678", userInfo.getPhone());
        assertEquals(UserRegistrationType.EMAIL, userInfo.getUserRegistrationType());
    }
}