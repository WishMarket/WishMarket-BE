package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.model.ProductInputForm;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductAddServiceTest {

    @Autowired
    private ProductAddService productAddService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void addProductsTest() throws IOException {

        //given
        String name = "테스트 상품";
        ProductInputForm productInputForm = ProductInputForm.builder()
            .name(name)
            .categoryCode(1)
            .price(1000)
            .description("상품 넣기 테스트 설명")
            .build();

        //when
        productAddService.addProductData(productInputForm);


        //then
        Boolean result = productRepository.existsByName(name);

        Assertions.assertEquals(result , true);

    }

}
