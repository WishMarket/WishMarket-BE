package com.zerobase.wishmarket.domain.product.repository;

import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCategory(ProductCategory category, Pageable pageable);

    List<Product> findAllByProductIdIn(List<Long> ids);

    List<Product> findAllByIsBestIsTrue();

    Page<Product> findAllByNameContains(String keyword, PageRequest pageRequest);
}


