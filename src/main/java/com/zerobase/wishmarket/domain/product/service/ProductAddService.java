package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.common.util.S3Util;
import com.zerobase.wishmarket.domain.product.model.ProductInputForm;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductLikesRepository;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
@Slf4j
@RequiredArgsConstructor
//백엔드 단에서만 사용!, 상품 데이터 넣기
public class ProductAddService {

    private final ProductRepository productRepository;

    private final ProductLikesRepository productLikesRepository;

    private final S3Util s3Util;

    private static final String PRODUCTS_DIRECTORY = "products";


    //상품 넣기
    public void addProductData(ProductInputForm productInputForm) throws IOException {

        ProductCategory productCategory = ProductCategory.values()[productInputForm.getCategoryCode()];
        String category = productCategory.toString();

        String storedFileName = "";
        if (productInputForm.getImage() != null) {
            if (!productInputForm.getImage().isEmpty()) {
                storedFileName = s3Util.upload(PRODUCTS_DIRECTORY,
                    category, productInputForm.getImage());
            }
        }

        Product product = Product.builder()
            .name(productInputForm.getName())
            .productImage(storedFileName)
            .category(productCategory)
            .price(productInputForm.getPrice())
            .description(productInputForm.getDescription())
            .build();
        productRepository.save(product);

        ProductLikes productLikes = ProductLikes.builder()
            .productId(product.getProductId())
            .likes(0)
            .build();
        productLikesRepository.save(productLikes);


    }

}