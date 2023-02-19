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
public class ProductBestDto {
    Long productId;
    String name;
    String productImageUrl;
    int category;
    int price;
    String description;
    int likes;
    private boolean isBest;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public static ProductBestDto of(Product product){
        return ProductBestDto.builder()
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
