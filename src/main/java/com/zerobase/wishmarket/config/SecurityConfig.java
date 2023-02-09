package com.zerobase.wishmarket.config;


import com.zerobase.wishmarket.domain.user.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final OAuthService oAuthService;

    @Deprecated
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // h2-console 사용을 위한 옵션 disable
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .authorizeRequests()
                    .antMatchers("/api/**").permitAll()
                .and()
                    // logout 요청시 홈으로 이동 - 기본 logout url = "/logout"
                    .logout().logoutSuccessUrl("/")
                .and()
                    // OAuth2 로그인 설정 시작점
                    .oauth2Login()
                    // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정 담당
                    .userInfoEndpoint()
                    // OAuth2 로그인 성공 시, 작업을 진행할 MemberService
                    .userService(oAuthService);
    }
}

