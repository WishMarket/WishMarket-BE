package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.entity.RedisBestProducts;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductLikesRepository;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.product.repository.RedisBestRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLikesRepository productLikesRepository;

    private final RedisBestRepository redisBestRepository;

    private static final Long KEY_BEST_PRODUCTS = 1L;


    //임시로 제품 정보 넣기, Batch로 서버 시작시 데이터 저장, 추후 변경
    public void addProduct() {
        for (ProductCategory category : ProductCategory.values()) {
            for (int i = 1; i < 21; i++) {
                Product product = Product.builder()
                    .name("product" + i)
                    .productImage("제품" + i + "파일 경로")
                    .category(category)
                    .price(1000)
                    .description("제품설명" + i)
                    .build();
                productRepository.save(product);

                ProductLikes productLikes = ProductLikes.builder()
                    .productId(product.getProductId())
                    .likes(0)
                    .build();
                productLikesRepository.save(productLikes);
            }
        }

        //베스트 상품 등록을 위해 likes 수 몇개만 변경
        for (Long i = 1L; i < 13L; i++) {
            ProductLikes productLikes = productLikesRepository.findById(i)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
            productLikes.setLikes(10);
        }
    }


    //베스트 상품 N개 업데이트
    public boolean updateBestProducts() {

        //베스트 상품
        redisBestRepository.deleteAll();

        //문제의 정렬 부분
        List<ProductLikes> bestProductLikes = productLikesRepository.findTop50ByOrderByLikesDesc();

        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < bestProductLikes.size(); i++) {
            ids.add(bestProductLikes.get(i).getProductId());
        }

        //redis repository에 넣기
        List<Product> productList = productRepository.findAllByProductIdIn(ids);
        redisBestRepository.save(RedisBestProducts.builder()
            .id(KEY_BEST_PRODUCTS)
            .products(productList)
            .build());

        return true;
    }

    //카테고리별 상품 조회
    public Page<Product> getProductByCategory(ProductCategory category,
        PageRequest pageRequest) {
        Page<Product> productList = productRepository.findAllByCategory(category,
            pageRequest);
        return productList;
    }


    //베스트 상품 조회
    public List<Product> getBestProducts() {
        List<Product> productList = redisBestRepository.findById(KEY_BEST_PRODUCTS).get().getProducts();
        return productList;
    }



}
