package com.zerobase.wishmarket.domain.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.zerobase.wishmarket.domain.point.exception.PointErrorCode;
import com.zerobase.wishmarket.domain.point.exception.PointException;
import com.zerobase.wishmarket.domain.point.model.PointResponseDto;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    public void getMyPointTest() {
        // given
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
            .pointPrice(10000L)
            .build();

        given(userAuthRepository.findById(userId)).willReturn(Optional.of(userEntity));

        // when
        PointResponseDto pointResponseDto = pointService.getMyPoint(userId);

        // then
        assertEquals(userId, pointResponseDto.getId());
        assertEquals(userEntity.getPointPrice(), pointResponseDto.getPointPrice());
    }

    @Test
    void addPointTest(){
        //given
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
            .pointPrice(10000L)
            .build();

        given(userAuthRepository.findById(userId)).willReturn(Optional.of(userEntity));

        //when
        pointService.chargePoint(userId);
        PointResponseDto result = pointService.getMyPoint(userId);

        //then
        assertEquals(20000L, result.getPointPrice());

    }

    @Test
    void usePointTest(){
        //given
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
            .pointPrice(10000L)
            .build();
        Long inputPoint = 5000L;

        given(userAuthRepository.findById(userId)).willReturn(Optional.of(userEntity));

        //when
        pointService.usePoint(userId, inputPoint);

        PointResponseDto result = pointService.getMyPoint(userId);

        //then
        assertEquals(5000L,result.getPointPrice());

    }

    @Test
    void PointExceptionTest_NOT_ENOUGH_POINT(){
        //given
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
            .pointPrice(10000L)
            .build();
        Long inputPoint = 20000L;

        given(userAuthRepository.findById(userId)).willReturn(Optional.of(userEntity));

        //when
        PointException exception = assertThrows(PointException.class, () -> pointService.usePoint(userId,inputPoint));

        //then
        assertEquals(PointErrorCode.NOT_ENOUGH_POINT, exception.getErrorCode());

    }

    @Test
    void UserExceptionTest_USER_NOT_FOUND() {

        // given
        Long userId = 999L;

        given(userAuthRepository.findById(userId)).willReturn(Optional.empty());

        // when
        UserException exception = assertThrows(UserException.class, () -> pointService.getMyPoint(userId));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

}