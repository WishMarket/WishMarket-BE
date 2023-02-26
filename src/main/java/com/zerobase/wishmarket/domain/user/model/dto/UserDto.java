package com.zerobase.wishmarket.domain.user.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

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

    public static UserDto from(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .nickName(userEntity.getNickName())
                .phone(userEntity.getPhone())
                .profileImage(userEntity.getProfileImage())
                .address(userEntity.getDeliveryAddress().getAddress())
                .detailAddress(userEntity.getDeliveryAddress().getDetailAddress())
                .userRegistrationType(userEntity.getUserRegistrationType())
                .userStatusType(userEntity.getUserStatusType())
                .userRolesType(userEntity.getUserRoleType())
                .createdAt(userEntity.getCreatedAt())
                .modifiedAt(userEntity.getModifiedAt())
                .build();
    }
}
