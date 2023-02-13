package com.zerobase.wishmarket.config;

import com.zerobase.wishmarket.domain.user.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 인증되지 않은 사용자 접근에 대한 handler
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final OAuthService oAuthService;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .httpBasic().disable()
            .csrf().disable() // csrf 방지
            .cors().disable() // cors 방지
            .formLogin().disable()
            .headers().frameOptions().disable();// 기본 로그인 페이지 없애기

        http
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

        // Spring Security에서 session을 생성하거나 사용하지 않도록 설정
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // JWT filter 적용
//        http
//            .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}