package com.zerobase.wishmarket.config;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationEntryPoint;
import com.zerobase.wishmarket.common.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 인증되지 않은 사용자 접근에 대한 handler
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable()
                .csrf().disable() // csrf 방지
                // Spring Security에서 session을 생성하거나 사용하지 않도록 설정
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .authorizeRequests()
//                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/auth/sign-up", "/api/auth/sign-in/**", "/api/auth/sign-in/social/**",
                        "/login/**", "/api/auth/email-check", "/api/auth/email-auth/**", "/api/user/password",
                        "/api/products/**", "/api/reviews/**", "/admin/**", "/api/auth/reissue","/api/funding/main").permitAll()
                .anyRequest().authenticated()
                .and()
                // logout 요청시 홈으로 이동 - 기본 logout url = "/logout"
                .logout().logoutSuccessUrl("/");


        // JWT filter 적용
        http
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // exception 처리
        http
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint); // 인증되지 않은 사용자 접근 시
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**", "/swagger-resources/**",
                "/swagger-ui/**",
                "/v2/api-docs");
    }


}

