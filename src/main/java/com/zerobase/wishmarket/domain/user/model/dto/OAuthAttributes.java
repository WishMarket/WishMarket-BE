package com.zerobase.wishmarket.domain.user.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import java.util.Map;

import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String profileImage;
    private String nickName;
    private Long pointPrice;
    private String phone;
    private UserRegistrationType userRegistrationType;
    private UserStatusType userStatusType;
    private UserRolesType userRoleType;

    // 반환하는 사용자 정보는 Map
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
        Map<String, Object> attributes) {

        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .nickName((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profileImage((String) attributes.get("picture"))
                .attributes(attributes)
                .userRegistrationType(UserRegistrationType.GOOGLE)
                .userRoleType(UserRolesType.USER)
                .userStatusType(UserStatusType.ACTIVE)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .nickName((String) response.get("name"))
                .email((String) response.get("email"))
                .profileImage((String) response.get("profile_image"))
                .attributes(response)
                .userRegistrationType(UserRegistrationType.NAVER)
                .userRoleType(UserRolesType.USER)
                .userStatusType(UserStatusType.ACTIVE)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User Entity 생성
    // 엔터티 생성시점 : 처음 로그인(소셜 계정)할 때
    public UserEntity toEntity(OAuthAttributes oAuthAttributes) {
        return UserEntity.builder()
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .nickName(name)
                .pointPrice(0L)
                .userRoleType(oAuthAttributes.getUserRoleType())
                .userRegistrationType(oAuthAttributes.getUserRegistrationType())
                .userStatusType(oAuthAttributes.getUserStatusType())
                .build();
    }
}