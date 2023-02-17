package com.zerobase.wishmarket.domain.wishList.repository;

import com.zerobase.wishmarket.domain.wishList.model.entity.RedisUserWishList;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserWishListRepository extends CrudRepository<RedisUserWishList, Long> {
}
