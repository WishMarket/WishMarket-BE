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
    Long productId;
    String name;
    String productImage;
    int category;
    int price;
    String description;
    int likes;

    public static ProductDetailDto of(Product product){
        return ProductDetailDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .productImage(product.getProductImage())
                .category(product.getCategory().getCategoryCode())
                .price(product.getPrice())
                .description(product.getDescription())
                .likes(product.getLikes())
                .build();
    }
}
