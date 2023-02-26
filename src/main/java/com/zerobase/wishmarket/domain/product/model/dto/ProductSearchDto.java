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
public class ProductSearchDto {

    private Long productId;
    private String name;
    private String productImage;
    private Long price;
    private int likes;
    private boolean isBest;

    public static ProductSearchDto of(Product product) {
        return ProductSearchDto.builder()
            .productId(product.getProductId())
            .name(product.getName())
            .productImage(product.getProductImage())
            .price(product.getPrice())
            .likes(product.getLikes())
            .isBest(product.isBest())
            .build();
    }
}
