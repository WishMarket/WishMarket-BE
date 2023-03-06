package com.zerobase.wishmarket.domain.product.repository;

import com.zerobase.wishmarket.domain.product.model.entity.RedisBestProducts;
import org.springframework.data.repository.CrudRepository;

public interface RedisBestRepository extends CrudRepository<RedisBestProducts, String> {

}
