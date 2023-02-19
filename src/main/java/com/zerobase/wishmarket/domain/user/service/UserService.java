package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.dto.UserDto;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserDto userDetail(Long userId) {
        Optional<UserEntity> userInfo = userRepository.findByUserId(userId);
        if (!userInfo.isPresent()) {
            throw new UserException(USER_NOT_FOUND);
        } else {
            return UserDto.from(userInfo.get());
        }
    }
}
