package com.zerobase.wishmarket.domain.user.model.dto;

import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;
import com.zerobase.wishmarket.domain.user.model.type.UserRolesType;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String profileImage;
    private String userRegistration;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email,
        String profileImage, String userRegistration) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.userRegistration = userRegistration;
    }

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
            .email((String) attributes.get("email"))
            .profileImage((String) attributes.get("picture"))
            .userRegistration(String.valueOf(UserRegistrationType.GOOGLE))
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
            .name((String) response.get("name"))
            .email((String) response.get("email"))
            .profileImage((String) response.get("picture"))
            .userRegistration(String.valueOf(UserRegistrationType.NAVER))
            .attributes(response)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    // User Entity 생성
    // 엔터티 생성시점 : 처음 가입 및 로그인(소셜 계정)할 때
    public UserEntity toEntity() {
        return UserEntity.builder()
            .name(name)
            .email(email)
            .profileImage(profileImage)
            .userRoleType(UserRolesType.USER)
            .build();
    }
}