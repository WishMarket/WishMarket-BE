package com.zerobase.wishmarket.domain.user.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String name;
    private String email;
    private String nickName;
    private String phone;
    private String profileImage;
    private String address;
    private String detailAddress;
    private UserRegistrationType userRegistrationType;
    private UserStatusType userStatusType;
    private UserRolesType userRolesType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public static UserInfoResponse from(UserEntity userEntity) {

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .nickName(userEntity.getNickName())
                .phone(userEntity.getPhone())
                .address("")
                .detailAddress("")
                .profileImage(userEntity.getProfileImage())
                .userRegistrationType(userEntity.getUserRegistrationType())
                .userStatusType(userEntity.getUserStatusType())
                .userRolesType(userEntity.getUserRoleType())
                .createdAt(userEntity.getCreatedAt())
                .modifiedAt(userEntity.getModifiedAt())
                .build();

        if (userEntity.getDeliveryAddress() != null) {
            userInfo.setAddress(userEntity.getDeliveryAddress().getAddress());
            userInfo.setDetailAddress(userEntity.getDeliveryAddress().getDetailAddress());
        }
        return userInfo;
    }
}
