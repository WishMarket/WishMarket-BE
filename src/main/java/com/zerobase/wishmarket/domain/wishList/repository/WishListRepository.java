package com.zerobase.wishmarket.domain.wishList.repository;

import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    List<WishList> findAllByUserId(Long userId);

    Optional<WishList> findByWishListId(Long wishListId);
}
