package com.zerobase.wishmarket.domain.product.repository;

import com.zerobase.wishmarket.domain.product.model.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByProductId(Long productId, Pageable pageable);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
}
