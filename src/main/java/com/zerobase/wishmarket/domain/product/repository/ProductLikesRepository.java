package com.zerobase.wishmarket.domain.product.repository;

import com.zerobase.wishmarket.domain.product.model.entity.ProductLikes;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLikesRepository extends JpaRepository<ProductLikes, Long> {

    Optional<ProductLikes> findByProductId(Long id);

    List<ProductLikes> findTop50ByOrderByLikesDesc();


}
