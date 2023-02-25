package com.zerobase.wishmarket.domain.funding.repository;

import com.zerobase.wishmarket.domain.funding.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
