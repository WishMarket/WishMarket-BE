package com.zerobase.wishmarket.domain.user.oauth;

import com.zerobase.wishmarket.domain.user.model.type.UserRegistrationType;

import java.util.Arrays;
import java.util.Map;

public enum OauthAttributes {
    GITHUB("github") {
        @Override
        public OAuthUserProfile of(Map<String, Object> attributes) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return OAuthUserProfile.builder()
                    .userRegistrationType(UserRegistrationType.valueOf("GITHUB"))
                    .email((String) response.get("email"))
                    .name((String) response.get("name"))
                    .profileImageUrl((String) response.get("avatar_url"))
                    .build();
        }
    },
    NAVER("naver") {
        @Override
        public OAuthUserProfile of(Map<String, Object> attributes) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return OAuthUserProfile.builder()
                    .userRegistrationType(UserRegistrationType.valueOf("NAVER"))
                    .email((String) response.get("email"))
                    .name((String) response.get("name"))
                    .profileImageUrl((String) response.get("profile_image"))
                    .build();
        }
    };

    private final String providerName;

    OauthAttributes(String name) {
        this.providerName = name;
    }

    public static OAuthUserProfile extract(String providerName, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> providerName.equals(provider.providerName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of(attributes);
    }

    public abstract OAuthUserProfile of(Map<String, Object> attributes);
}
