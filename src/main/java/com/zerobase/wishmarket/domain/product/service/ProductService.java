package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.dto.ProductDetailDto;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDetailDto detail(Long productId) {
        return ProductDetailDto.of(productRepository.findById(productId).
                orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND)));
    }
}
