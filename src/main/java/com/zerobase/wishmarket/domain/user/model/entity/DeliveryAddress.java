package com.zerobase.wishmarket.domain.user.model.entity;

import com.zerobase.wishmarket.common.entity.BaseEntity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class DeliveryAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    private String address;
    private String detailAddress;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    // 1 : 1 Mapping relationship with user
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

}
