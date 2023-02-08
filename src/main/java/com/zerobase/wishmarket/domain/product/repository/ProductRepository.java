package com.zerobase.wishmarket.domain.product.repository;

import com.zerobase.wishmarket.domain.product.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
