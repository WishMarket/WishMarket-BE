package com.zerobase.wishmarket.domain.user.model.dto;

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
public class InfluencerResponse {

    private Long userId;
    private String name;
    private String email;
    private String nickName;
    private String profileImageUrl;

    public static InfluencerResponse from(UserEntity userEntity) {
        return InfluencerResponse.builder()
            .userId(userEntity.getUserId())
            .name(userEntity.getName())
            .email(userEntity.getEmail())
            .nickName(userEntity.getNickName())
            .profileImageUrl(userEntity.getProfileImage())
            .build();
    }
}
