package com.zerobase.wishmarket.controller;

import com.zerobase.wishmarket.domain.review.model.dto.ReviewDto;
import com.zerobase.wishmarket.domain.review.service.ReviewService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"리뷰 API"})
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/reviews")
    public ResponseEntity<Page<ReviewDto>> reviews(@RequestParam Long productId,
        @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ResponseEntity.ok().body(reviewService.reviews(productId, pageRequest));
    }
}
