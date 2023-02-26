package com.zerobase.wishmarket.domain.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.dto.ProductDetailDto;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;


    @Test
    void detail_ProductFound() {
        //given
        Long productId = 1L;
        ProductLikes productLikes = ProductLikes.builder()
                .productId(productId)
                .likes(100)
                .build();
        Product product = Product.builder()
                .productId(productId)
                .name("상품1")
                .productImage("이미지1")
                .category(ProductCategory.CLOTHES)
                .price(50000L)
                .description("상세1")
                .productLikes(productLikes)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductDetailDto result = productService.detail(productId);

        // then
        assertEquals(productId, result.getProductId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getProductImage(), result.getProductImage());
        assertEquals(product.getCategory().getCategoryCode(), result.getCategory());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(productLikes.getLikes(), result.getLikes());

    }

    @Test
    public void detail_ThrowProductNotFoundException() {
        // given
        Long productId = 1L;
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        ProductException exception = assertThrows(ProductException.class, () -> productService.detail(productId));

        // then
        assertEquals(ProductErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

  



}