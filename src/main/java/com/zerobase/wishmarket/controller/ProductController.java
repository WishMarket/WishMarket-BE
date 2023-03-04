package com.zerobase.wishmarket.controller;

import com.zerobase.wishmarket.domain.product.model.dto.ProductBestDto;
import com.zerobase.wishmarket.domain.product.model.dto.ProductCategoryDto;
import com.zerobase.wishmarket.domain.product.model.dto.ProductDetailDto;
import com.zerobase.wishmarket.domain.product.model.dto.ProductSearchDto;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    //카테고리별 상품 조회
    @GetMapping("/category")
    public Page<ProductCategoryDto> getProductListByCategory(
        @RequestParam int categoryCode,
        @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        ProductCategory[] categories = ProductCategory.values();

        return ResponseEntity.ok()
            .body(productService.getProductByCategory(categories[categoryCode-1], pageRequest))
            .getBody();
    }

    //베스트 상품 조회
    @GetMapping("/best")
    public Page<ProductBestDto> getBestProductList(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ResponseEntity.ok().body(productService.getBestProducts(pageRequest)).getBody();
    }

    @GetMapping("/search")
    public Page<ProductSearchDto> search(@RequestParam("keyword") String keyword,
        @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ResponseEntity.ok().body(productService.search(keyword, pageRequest)).getBody();
    }

    @GetMapping("/{productId}/detail")
    public ResponseEntity<ProductDetailDto> productDetail(@PathVariable Long productId) {
        ProductDetailDto responseDto = productService.detail(productId);
        return ResponseEntity.ok().body(responseDto);
    }


}
