package com.zerobase.wishmarket.domain.user.model.entity;

import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
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
    private UserRegistrationType userRegistrationType;

    @Enumerated(EnumType.STRING)
    private UserRolesType userRoleType;

    @Enumerated(EnumType.STRING)
    private UserStatusType userStatusType;

    // 1 : 1 Mapping
    // 주소
    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private DeliveryAddress deliveryAddress;

    // 회원 가입 시 가입 정보 입력
    public static UserEntity of(SignUpForm form, UserRegistrationType userRegistrationType, UserStatusType userStatusType) {
        return UserEntity.builder()
            .name(form.getName())
            .email(form.getEmail())
            .nickName(form.getNickName())
            .password(form.getPassword())
            .userRegistrationType(userRegistrationType)
            .userStatusType(userStatusType)
            .build();
    }

    public void setUserStatusType(UserStatusType userStatusType) {
        this.userStatusType = userStatusType;
    }

    public UserEntity update(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
        return this;

    }


}
