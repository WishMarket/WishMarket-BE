package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public boolean userWithdrawal(OAuthUserInfo user, String email) {
        if(user.getEmail().equals(email)) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
            UserEntity userEntity = optionalUser.get().changeStatus(UserStatusType.WITHDRAWAL);
            userRepository.save(userEntity);
        } else {
            return false;
        }

        return true;
    }
}
