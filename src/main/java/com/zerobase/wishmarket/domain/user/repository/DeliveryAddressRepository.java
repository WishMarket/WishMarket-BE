package com.zerobase.wishmarket.domain.user.repository;

import com.zerobase.wishmarket.domain.user.model.entity.DeliveryAddress;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    Optional<DeliveryAddress> findByUserEntity(UserEntity userEntity);

}
