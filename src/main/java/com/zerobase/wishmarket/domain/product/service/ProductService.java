package com.zerobase.wishmarket.domain.product.service;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.dto.ProductBestDto;
import com.zerobase.wishmarket.domain.product.model.dto.ProductCategoryDto;
import com.zerobase.wishmarket.domain.product.model.dto.ProductDetailDto;
import com.zerobase.wishmarket.domain.product.model.dto.ProductSearchDto;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import com.zerobase.wishmarket.domain.product.model.entity.RedisBestProducts;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.domain.product.repository.ProductLikesRepository;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.product.repository.RedisBestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductLikesRepository productLikesRepository;

    private final RedisBestRepository redisBestRepository;

    private static final String KEY_BEST = "BEST_PRODUCTS";


    //베스트 상품 N개 업데이트
    public void updateBestProducts() {

        //기존의 베스트 상품의 isBest값을 false로 바꾸기

        List<Product> oldBestproducts = productRepository.findAllByIsBestIsTrue();
        for (Product p : oldBestproducts) {
            p.setIsBestFalse();
        }

        //지금은 정렬하여 50개의 상품을 가져오는 로직으로 마무리
        //베스트 상품의 기준이 현재는 좋아요 수로 판별하지만
        //실제 서비스에서는 사용자들의 클릭 수, 관심도, 좋아요 등
        //다양한 판별기준으로 베스트 알고리즘을 짜는 방법이 있다.
        List<ProductLikes> bestProductLikes = productLikesRepository.findTop50ByOrderByLikesDesc();

        List<Long> ids = new ArrayList<>();
        for (ProductLikes bestProductLike : bestProductLikes) {
            ids.add(bestProductLike.getProductId());
        }

        //새로운 베스트 상품의 isBest값을 true로 바꾸기
        List<Product> newBestproducts = productRepository.findAllByProductIdIn(ids);
        for (Product product : newBestproducts) {
            product.setIsBestTrue();
        }

        //redis repository에 넣기
        //기존에 레디스값에 Set하기 때문에 기존의 레디스를 삭제할 필요가 없음
        redisBestRepository.save(RedisBestProducts.builder()
            .id(KEY_BEST)
            .products(newBestproducts)
            .build());

    }

    //카테고리별 상품 조회
    public Page<ProductCategoryDto> getProductByCategory(ProductCategory category,
        PageRequest pageRequest) {
        Page<Product> pagingProduct = productRepository.findAllByCategory(category,
            pageRequest);

        List<Product> productList = pagingProduct.getContent();
        List<ProductCategoryDto> productCategoryDtoList = new ArrayList<>();

        for (Product product : productList) {
            ProductCategoryDto productCategoryDto = ProductCategoryDto.of(product);
            productCategoryDtoList.add(productCategoryDto);
        }

        return new PageImpl<>(productCategoryDtoList, pageRequest,
            pagingProduct.getTotalElements());

    }

    //베스트 상품 조회
    public Page<ProductBestDto> getBestProducts(PageRequest pageRequest) {

        List<ProductBestDto> productBestDtoList = new ArrayList<>();

        List<Product> productList;

        Optional<RedisBestProducts> optionalRedisBestProducts = redisBestRepository.findById(KEY_BEST);
        if(optionalRedisBestProducts.isPresent()){
            productList = optionalRedisBestProducts.get().getProducts();
        } else {
            //베스트 상품 값이 없으면 빈 값 반환
            return new PageImpl<>(productBestDtoList, pageRequest, 0);
        }

        //직접 페이징 처리
        int start = pageRequest.getPageNumber() * pageRequest.getPageSize();
        int end = Math.min(start + pageRequest.getPageSize(), productList.size());

        for (Product product : productList.subList(start, end)) {
            ProductBestDto productBestDto = ProductBestDto.of(product);
            productBestDtoList.add(productBestDto);
        }

        return new PageImpl<>(productBestDtoList, pageRequest, productList.size());
    }

    public Page<ProductSearchDto> search(String keyword,
        PageRequest pageRequest) {
        Page<Product> pagingProduct = productRepository.findAllByNameContains(keyword, pageRequest);
        List<Product> productList = pagingProduct.getContent();
        List<ProductSearchDto> productSearchDtoList = new ArrayList<>();
        for (Product product : productList) {
            ProductSearchDto productSearchDto = ProductSearchDto.of(product);
            productSearchDtoList.add(productSearchDto);
        }
        return new PageImpl<>(productSearchDtoList, pageRequest, pagingProduct.getTotalElements());

    }

    public ProductDetailDto detail(Long productId) {
        return ProductDetailDto.of(productRepository.findById(productId).
            orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND)));

    }

}

