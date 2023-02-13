package com.zerobase.wishmarket.domain.product.repository;

import com.zerobase.wishmarket.domain.product.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

}
