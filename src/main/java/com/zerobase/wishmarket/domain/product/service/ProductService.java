package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductLikesRepository;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLikesRepository productLikesRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private static SetOperations<String, String> setOperations;

    private static final String KEY_BEST_PRODUCTS = "BEST_PRODUCTS";


    //임시로 제품 정보 넣기, Batch로 서버 시작시 데이터 저장, 추후 변경
    public void addProduct() {
        for (ProductCategory category : ProductCategory.values()) {
            for (int i = 0; i < 20; i++) {
                Product product = Product.builder()
                    .name("제품" + i)
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
    }

    //베스트 상품 12개 업데이트
    public boolean updateBestProducts() {

        //남아있던 정보 삭제
        redisTemplate.delete(KEY_BEST_PRODUCTS);
        List<ProductLikes> bestProductLikes = productLikesRepository.findTop12ByOrderByLikesDesc();

        setOperations = redisTemplate.opsForSet();

        for (int i = 0; i < bestProductLikes.size(); i++) {
            setOperations.add(KEY_BEST_PRODUCTS,
                Long.toString(bestProductLikes.get(i).getProductId()));
        }
        return true;
    }

    //카테고리별 상품 조회
    public Page<List<Product>> getProductByCategory(ProductCategory category,
        PageRequest pageRequest) {
        Page<List<Product>> productList = productRepository.findAllByCategory(category,
            pageRequest);
        return productList;
    }


    //베스트 상품 조회
    public List<Product> getBestProducts() {

        //Redis에 저장되어있는 Best 상품에 대한 ID들을 가져옴
        Set<String> bestSets= setOperations.members(KEY_BEST_PRODUCTS);
        if(bestSets.isEmpty()){
            throw new ProductException(ProductErrorCode.BEST_PRODUCT_NOT_FOUND);
        }

        //Long 타입으로 전환 (redis가 Long타입으로 저장이 안되는데 아시는분은 말씀해주세요!,,)
        Set<Long> bestListToLong = new HashSet<>();
        for (String s : bestSets) {
            bestListToLong.add(Long.valueOf(s));
        }

        List<Product> productList = productRepository.findAllByProductIdIn(bestListToLong);
        if (productList.isEmpty()) {
            throw new ProductException(ProductErrorCode.BEST_PRODUCT_NOT_FOUND);
        }

        return productList;
    }



}
