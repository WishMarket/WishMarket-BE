package com.zerobase.wishmarket.domain.user.model.entity;

import com.zerobase.wishmarket.domain.user.model.type.UserRegistration;
import com.zerobase.wishmarket.domain.user.model.type.UserRoles;
import com.zerobase.wishmarket.domain.user.model.type.UserStatus;
import com.zerobase.wishmarket.entity.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private String nickName;

    private String email;

    private String password; // 암호화

    private Long pointPrice;

    private String phone;

    private String profileImage; // 프로필 이미지 경로

    @Enumerated(EnumType.STRING)
    private UserRegistration userRegistration;

    @Enumerated(EnumType.STRING)
    private UserRoles userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    // 1 : 1 Mapping
    // 주소
    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private DeliveryAddress deliveryAddress;

    public UserEntity update(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
        return this;
    }
}
