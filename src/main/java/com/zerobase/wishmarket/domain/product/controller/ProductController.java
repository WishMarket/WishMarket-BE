package com.zerobase.wishmarket.domain.product.controller;

import com.zerobase.wishmarket.domain.product.model.dto.ProductDetailDto;
import com.zerobase.wishmarket.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/product/{productId}/detail")
    public ResponseEntity<?> productDetail(@PathVariable Long productId) {
        ProductDetailDto responseDto = productService.detail(productId);
        return ResponseEntity.ok().body(responseDto);
    }


}
