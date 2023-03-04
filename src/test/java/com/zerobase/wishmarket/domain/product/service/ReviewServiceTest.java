package com.zerobase.wishmarket.domain.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.zerobase.wishmarket.domain.review.model.dto.ReviewDto;
import com.zerobase.wishmarket.domain.review.model.entity.Review;
import com.zerobase.wishmarket.domain.review.repository.ReviewRepository;
import com.zerobase.wishmarket.domain.review.service.ReviewService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ReviewService reviewService;


    @Test
    public void testReviews() {
        //given
        Long productId = 1L;
        Integer page = 1;
        Integer size = 12;
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(1L, 1L, "User1", "Comment1", true, productId));
        reviews.add(new Review(2L, 2L, "User2", "Comment2", false, productId));
        Page<Review> reviewPage = new PageImpl<>(reviews, pageRequest, reviews.size());

        given(reviewRepository.findAllByProductId(productId, pageRequest)).willReturn(reviewPage);

        //when
        Page<ReviewDto> reviewDtoPage = reviewService.reviews(productId, pageRequest);

        //then
        assertEquals(reviewPage.getTotalElements(), reviewDtoPage.getTotalElements());
        assertEquals(reviewPage.getSize(), reviewDtoPage.getSize());
        assertEquals(reviewPage.getTotalPages(), reviewDtoPage.getTotalPages());
    }
}