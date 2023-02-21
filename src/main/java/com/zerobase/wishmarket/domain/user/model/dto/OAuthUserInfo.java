package com.zerobase.wishmarket.domain.user.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class OAuthUserInfo implements Serializable {

    private Long userId;
    private String name;
    private String email;
    private String profileImage;

    public OAuthUserInfo(UserEntity userEntity) {
        this.userId = userEntity.getUserId();
        this.name = userEntity.getName();
        this.email = userEntity.getEmail();
        this.profileImage = userEntity.getProfileImage();
    }
}
