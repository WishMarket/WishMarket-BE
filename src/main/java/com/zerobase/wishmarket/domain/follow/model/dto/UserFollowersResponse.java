package com.zerobase.wishmarket.domain.follow.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserFollowersResponse {

    private Long userId;
    private String name;
    private String email;
    private String nickName;
    private String profileImageUrl;

    public static UserFollowersResponse from(UserEntity userEntity){
        return UserFollowersResponse.builder()
            .userId(userEntity.getUserId())
            .name(userEntity.getName())
            .email(userEntity.getEmail())
            .nickName(userEntity.getNickName())
            .profileImageUrl(userEntity.getProfileImage())
            .build();
    }


}
