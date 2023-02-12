package com.zerobase.wishmarket.domain.product.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;



    //카테고리별 상품 조회
    @Test
    public void testSearchProductCategory() {
        PageRequest pageRequest = PageRequest.of(1, 12);

        for(ProductCategory category : ProductCategory.values()){
            Page<List<Product>> productList = productRepository.findAllByCategory(category,
                pageRequest);

            if(productList.isEmpty()){
                throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
            }
            //각 카테고리별로 제품을 성공적으로 가져오는지 확인
            assertNotNull(productList);
        }

    }



    //베스트 상품 조회
    @Test
    public void testBestProducts() {
        List<Product> bestProducts = productService.getBestProducts();
        if(bestProducts.isEmpty()){
            throw new ProductException(ProductErrorCode.BEST_PRODUCT_NOT_FOUND);
        }

        assertNotNull(bestProducts);
    }




}