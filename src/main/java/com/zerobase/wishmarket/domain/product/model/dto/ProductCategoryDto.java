package com.zerobase.wishmarket.domain.product.model.dto;

import com.zerobase.wishmarket.domain.product.model.entity.Product;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryDto {

    private Long productId;
    private String name;
    private String productImageUrl;
    private int category;
    private Long price;
    private String description;
    private int likes;
    private boolean isBest;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductCategoryDto of(Product product) {
        return ProductCategoryDto.builder()
            .productId(product.getProductId())
            .name(product.getName())
            .productImageUrl(product.getProductImage())
            .category(product.getCategory().getCategoryCode())
            .price(product.getPrice())
            .description(product.getDescription())
            .likes(product.getProductLikes().getLikes())
            .isBest(product.isBest())
            .createdAt(product.getCreatedAt())
            .modifiedAt(product.getModifiedAt())
            .build();
    }


}
