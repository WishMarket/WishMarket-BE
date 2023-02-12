package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.model.entity.Review;
import com.zerobase.wishmarket.domain.product.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Page<Review> reviews(Long productId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return reviewRepository.findAllByProductId(productId, pageRequest);
    }
}
