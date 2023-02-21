package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.model.dto.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserPasswordChangeReturnType;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordChangeReturnType passwordChange(ChangePwdForm form) {

        String encodePassword = passwordEncoder.encode(form.getPassword());
        Optional<UserEntity> user = userRepository.findByEmailAndUserRegistrationType(form.getEmail(), UserRegistrationType.EMAIL);

        if (!user.isPresent()) {
            return UserPasswordChangeReturnType.CHANGE_PASSWORD_FAIL;
        } else {
            UserEntity userInfo = user.get();
            userInfo.setPassword(encodePassword);
            userRepository.save(userInfo);
            return UserPasswordChangeReturnType.CHANGE_PASSWORD_SUCCESS;
        }

    }
}
