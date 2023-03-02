package com.zerobase.wishmarket.domain.user.oauth;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthUserProfile {
    private final UserRegistrationType userRegistrationType;
    private final String email;
    private final String name;
    private final String profileImageUrl;

    @Builder
    public OAuthUserProfile(UserRegistrationType userRegistrationType, String email, String name, String profileImageUrl) {
        this.userRegistrationType = userRegistrationType;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .userRegistrationType(userRegistrationType)
                .email(email)
                .name(name)
                .nickName(name)
                .pointPrice(0L)
                .profileImage(profileImageUrl)
                .userRoleType(UserRolesType.USER)
                .userStatusType(UserStatusType.ACTIVE)
                .build();
    }
}
