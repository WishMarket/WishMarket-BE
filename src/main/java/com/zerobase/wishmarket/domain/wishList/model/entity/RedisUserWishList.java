package com.zerobase.wishmarket.domain.wishList.model.entity;

import java.util.List;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@ToString
@RedisHash("USER_WISHLIST")
@Builder
public class RedisUserWishList {

    @Id
    private Long id;

    private List<WishList> wishLists;

}
