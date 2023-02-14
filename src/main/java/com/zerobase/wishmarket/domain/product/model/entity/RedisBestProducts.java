package com.zerobase.wishmarket.domain.product.model.entity;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@RedisHash("BEST_PRODUCTS")
@Builder
public class RedisBestProducts {

    @Id
    private Long id;

    private List<Product> products;

}
