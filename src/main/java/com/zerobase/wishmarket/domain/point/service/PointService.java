package com.zerobase.wishmarket.domain.point.service;


import com.zerobase.wishmarket.domain.point.exception.PointErrorCode;
import com.zerobase.wishmarket.domain.point.exception.PointException;
import com.zerobase.wishmarket.domain.point.model.PointResponseDto;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointService {

    private final UserAuthRepository userAuthRepository;

    public PointResponseDto chargePoint(Long userId) {
        UserEntity user = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.increasePointPrice(10000L);
        userAuthRepository.save(user);

        return PointResponseDto.builder()
            .Id(userId)
            .pointPrice(user.getPointPrice())
            .build();
    }

    public void usePoint(Long userId, Long inputPoint) {
        UserEntity user = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (inputPoint > user.getPointPrice()) {
            throw new PointException(PointErrorCode.NOT_ENOUGH_POINT);
        }

        user.usePointPrice(inputPoint);
        userAuthRepository.save(user);
    }

    public void refundPoint(Long userId, Long refundingPoint) {
        UserEntity user = userAuthRepository.findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.increasePointPrice(refundingPoint);
        userAuthRepository.save(user);
    }

}
