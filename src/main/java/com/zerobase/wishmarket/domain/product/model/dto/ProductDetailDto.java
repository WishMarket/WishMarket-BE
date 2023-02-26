package com.zerobase.wishmarket.domain.product.model.dto;

import com.zerobase.wishmarket.domain.product.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDto {

    private Long productId;
    private String name;
    private String productImage;
    private int category;
    private Long price;
    private String description;
    private int likes;
    private boolean isBest;

    public static ProductDetailDto of(Product product) {
        return ProductDetailDto.builder()
            .productId(product.getProductId())
            .name(product.getName())
            .productImage(product.getProductImage())
            .category(product.getCategory().getCategoryCode())
            .price(product.getPrice())
            .description(product.getDescription())
            .likes(product.getLikes())
            .isBest(product.isBest())
            .build();
    }
}
