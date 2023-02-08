package com.zerobase.wishmarket.domain.product.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.zerobase.wishmarket.domain.product.model.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductErrorCodeTest {

    @Autowired
    private ProductRepository productRepository;

    //예외처리 테스트
    @Test
    public void productFindTest() {

        assertThrows(ProductNotFoundException.class, () -> {
            Optional<Product> product = productRepository.findById(1L);
            if (product.isEmpty()) {
                throw new ProductNotFoundException();
            }
        });
    }
}