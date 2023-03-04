package com.zerobase.wishmarket.domain.review.model.dto;

import com.zerobase.wishmarket.domain.review.model.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

    private Long id;

    private Long userId;

    private String userName;

    private String comment;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
            .id(review.getId())
            .userId(review.getUserId())
            .userName(review.getUserName())
            .comment(review.getComment())
            .build();
    }

}
