package com.zerobase.wishmarket.domain.product.controller;

import com.zerobase.wishmarket.domain.product.model.ProductInputForm;
import com.zerobase.wishmarket.domain.product.model.dto.ProductSearchDto;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.service.ProductService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    //카테고리별 상품 조회
    @GetMapping("/category")
    public Page<Product> getProductListByCategory(
            @RequestParam ProductCategory categories,
            @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ResponseEntity.ok()
                .body(productService.getProductByCategory(categories, pageRequest)).getBody();

    }


    //베스트 상품 조회
    @GetMapping("/best")
    public ResponseEntity<List<Product>> getBestProductList() {
        return ResponseEntity.ok().body(productService.getBestProducts());
    }

    //상품 데이터 넣기
    @PostMapping("/api/product/add")
    public ResponseEntity addProduct(@ModelAttribute ProductInputForm productInputForm) {
        productService.addProductData(productInputForm);
        return ResponseEntity.ok().body("상품 정보가 정상적으로 업로드 되었습니다.");
    }

    @GetMapping("/search")
    public Page<ProductSearchDto> search(@RequestParam("keyword") String keyword,
                                         @RequestParam("page") Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 12);
        return ResponseEntity.ok().body(productService.search(keyword, pageRequest)).getBody();
    }


}
