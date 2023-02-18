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
public class UserSearchResponse {
    private Long userId;
    private String name;
    private String nickName;
    private String email;
    private String profileImageUrl;
    private Boolean isFriend;
    // 위시 리스트 추가?

    public static UserSearchResponse of(UserEntity userEntity, boolean isFriend){
        return UserSearchResponse.builder()
            .userId(userEntity.getUserId())
            .email(userEntity.getEmail())
            .name(userEntity.getName())
            .nickName(userEntity.getNickName())
            .profileImageUrl(userEntity.getProfileImage())
            .isFriend(isFriend)
            .build();
    }

}
