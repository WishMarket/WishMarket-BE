package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void userWithDrawalTest() {
        Long userId = 1L;
        UserStatusType statusType = UserStatusType.ACTIVE;
        String email = "stanley@test.com";
        String nickname = "starbucks";

        UserEntity user = UserEntity.builder()
                .userId(userId)
                .email(email)
                .nickName(nickname)
                .userStatusType(statusType)
                .build();

        userRepository.save(user);

        Optional<UserEntity> test1 = userRepository.findByEmail(email);
        System.out.println("=== before test ===");
        System.out.println(test1.get().getUserId());
        System.out.println(test1.get().getNickName());
        System.out.println(test1.get().getEmail());
        System.out.println(test1.get().getUserStatusType());

        UserEntity userEntity = test1.get().changeStatus(UserStatusType.WITHDRAWAL);
        userRepository.save(userEntity);
        Optional<UserEntity> test2 = userRepository.findByEmail(email);
        System.out.println("=== after test ===");
        System.out.println(test2.get().getUserId());
        System.out.println(test2.get().getNickName());
        System.out.println(test2.get().getEmail());
        System.out.println(test2.get().getUserStatusType());
    }
}