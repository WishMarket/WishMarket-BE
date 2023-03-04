package com.zerobase.wishmarket.domain.review.service;

import com.zerobase.wishmarket.domain.review.model.dto.ReviewDto;
import com.zerobase.wishmarket.domain.review.model.entity.Review;
import com.zerobase.wishmarket.domain.review.repository.ReviewRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Page<ReviewDto> reviews(Long productId, PageRequest pageRequest) {
        Page<Review> reviewPage = reviewRepository.findAllByProductId(productId, pageRequest);
        List<ReviewDto> reviewDtoList = reviewPage
            .getContent()
            .stream()
            .map(ReviewDto::of)
            .collect(Collectors.toList());

        return new PageImpl<>(reviewDtoList, pageRequest, reviewPage.getTotalElements());
    }
}
