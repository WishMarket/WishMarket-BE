package com.zerobase.wishmarket.domain.user.repository;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAuthRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndUserRegistrationType(String email, UserRegistrationType userRegistration);

    boolean existsByEmailAndUserRegistrationType(String email, UserRegistrationType userRegistration);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUserIdAndUserStatusType(Long userId, UserStatusType userStatusType);

    Page<UserEntity> findByNameContainsIgnoreCase(String userName, Pageable pageable);

    Page<UserEntity> findByEmailContainsIgnoreCase(String email, Pageable pageable);

    Page<UserEntity> findByNickNameContainsIgnoreCase(String nickName, Pageable pageable);

    @Query(value =
        "SELECT * "
        + "FROM user_entity user "
        + "where user.influence=true "
        + "order by RAND() limit 4", nativeQuery = true)
    List<UserEntity> findAllByInfluenceIsTrueRandom();

}
