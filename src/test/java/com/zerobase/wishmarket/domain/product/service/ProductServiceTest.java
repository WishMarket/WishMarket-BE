package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.dto.ProductSearchDto;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductLikesRepository;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductLikesRepository productLikesRepository;

    //카테고리별 상품 조회
    @Test
    public void testSearchProductCategory() {
        PageRequest pageRequest = PageRequest.of(1, 12);

        for (ProductCategory category : ProductCategory.values()) {
            Page<Product> productList = productRepository.findAllByCategory(category,
                    pageRequest);

            if (productList.isEmpty()) {
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
        if (bestProducts.isEmpty()) {
            throw new ProductException(ProductErrorCode.BEST_PRODUCT_NOT_FOUND);
        }

        assertNotNull(bestProducts);
    }


    //베스트 상품이 변하는지 확인
    @Test
    public void testBestProductsChange() {
        List<Long> oldBestList = new ArrayList<>();
        List<Long> newBestList = new ArrayList<>();

        List<Product> oldBestProducts = productService.getBestProducts();
        for (Product product : oldBestProducts) {
            oldBestList.add(product.getProductId());
        }

        try {
            Thread.sleep(30000);  //일정 시간 뒤 베스트 상품이 변했는지 비교, 원래는 매일 자정2시에 베스트 상품이 변경됨
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //베스트 상품이 변할수 있도록 Id가 30인 상품 Likes 값 조정
        Optional<ProductLikes> optionalProductLikes = productLikesRepository.findByProductId(30L);
        if (optionalProductLikes.isEmpty()) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        //좋아요 수 100으로 변경
        ProductLikes productLikes = optionalProductLikes.get();
        productLikes.setLikes(100);


        List<Product> newBestProducts = productService.getBestProducts();
        for (Product product : newBestProducts) {
            newBestList.add(product.getProductId());
        }

        assertNotEquals(oldBestList, newBestList);


    }


    @Test
    public void testSearchProduct() {
        //given
        String keyword = "t1";
        int page = 2; // 0 일 수 는 없음
        PageRequest pageRequest = PageRequest.of(page - 1, 12);

        //when
        Page<ProductSearchDto> pagingSearchProductList = productService.search(keyword, pageRequest);
        List<ProductSearchDto> resultProductList = pagingSearchProductList.getContent();
        int cnt = 0;
        for (ProductSearchDto productSearchDto : resultProductList) {
            if (productSearchDto.getName().contains(keyword)) {
                cnt++;
            }
        }

        //then
        assertEquals(cnt, resultProductList.size());
        assertEquals(cnt, pagingSearchProductList.getNumberOfElements());
    }


}