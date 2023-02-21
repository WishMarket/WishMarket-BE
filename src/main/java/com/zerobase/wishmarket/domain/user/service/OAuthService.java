package com.zerobase.wishmarket.domain.user.service;

import com.zerobase.wishmarket.domain.user.model.dto.OAuthAttributes;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserAuthRepository userAuthRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행중인 서비스를 구분 (추후 네이버 등 연동 시 구분 필요)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            // 키가 되는 필드값 (Primary Key와 같은 의미)
            .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
            oAuth2User.getAttributes());

        UserEntity userEntity = saveOrUpdate(attributes);

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("USER")),
            attributes.getAttributes(),
            attributes.getNameAttributeKey());
    }

    private UserEntity saveOrUpdate(OAuthAttributes attributes) {
        UserEntity userEntity = userAuthRepository.findByEmail(attributes.getEmail())
            .map(entity -> entity.update(attributes.getName(), attributes.getProfileImage()))
            .orElse(attributes.toEntity(attributes));

        return userAuthRepository.save(userEntity);
    }
}
