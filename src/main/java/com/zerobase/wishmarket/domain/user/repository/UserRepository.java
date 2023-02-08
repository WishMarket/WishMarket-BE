package com.zerobase.wishmarket.domain.user.repository;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
