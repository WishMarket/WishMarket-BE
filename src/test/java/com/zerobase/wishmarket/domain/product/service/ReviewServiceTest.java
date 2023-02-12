package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.model.entity.Review;
import com.zerobase.wishmarket.domain.product.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void reviews() {
        // Given
        Review review1 = Review.builder()
                .userId(1L)
                .userName("유저1")
                .comment("코멘트1")
                .isRecommend(true)
                .productId(1L)
                .build();
        Review review2 = Review.builder()
                .userId(2L)
                .userName("유저2")
                .comment("코멘트2")
                .isRecommend(true)
                .productId(1L)
                .build();
        Review review3 = Review.builder()
                .userId(3L)
                .userName("유저3")
                .comment("코멘트3")
                .isRecommend(false)
                .productId(1L)
                .build();

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        // When
        Page<Review> result = reviewService.reviews(1L, 1, 2);

        // Then
        assertEquals(2, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());

        List<Review> reviewList = result.getContent();
        assertEquals(1L, reviewList.get(0).getUserId().longValue());
        assertEquals("유저1", reviewList.get(0).getUserName());
        assertEquals("코멘트1", reviewList.get(0).getComment());
        assertTrue(reviewList.get(0).isRecommend());
        assertEquals(1L, reviewList.get(0).getProductId().longValue());
    }
}