package com.zerobase.wishmarket.domain.user.repository;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndUserRegistrationType(String email, UserRegistrationType userRegistration);

    boolean existsByEmailAndUserRegistrationType(String email, UserRegistrationType userRegistration);

    Optional<UserEntity> findByEmail(String email);

}
