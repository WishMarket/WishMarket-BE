package com.zerobase.wishmarket.domain.product.model.entity;

import com.zerobase.wishmarket.domain.product.model.type.ProductCategory;
import com.zerobase.wishmarket.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;

    private String productImage;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private Long price;

    private String description;

    private boolean isBest;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductLikes productLikes;

    public int getLikes() {
        return productLikes.getLikes();
    }

    public void plusProductLikes(){
        this.productLikes.likesCountPlus();
    }

    public void setIsBestFalse() {
        this.isBest = false;
    }

    public void setIsBestTrue() {
        this.isBest = true;
    }


}
