package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.zerobase.wishmarket.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity passwordChange(String email, String password) {

        String encodePassword = passwordEncoder.encode(password);
        Optional<UserEntity> user = userRepository.findByEmailAndUserRegistrationType(email, UserRegistrationType.EMAIL);

        if (user.isPresent()) {
            user.get().setPassword(encodePassword);
            userRepository.save(user.get());
        } else {
            throw new UserException(USER_NOT_FOUND);
        }

        return user.get();
    }
}
