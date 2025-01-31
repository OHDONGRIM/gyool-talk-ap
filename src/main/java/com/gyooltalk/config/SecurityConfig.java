package com.gyooltalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 필요시 활성화 고려
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login").permitAll() // 로그인 페이지 허용
                        .anyRequest().authenticated() // 그 외 경로는 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 페이지
                        .defaultSuccessUrl("/", true) // 로그인 성공 후 이동
                        .failureUrl("/login?error=true") // 로그인 실패 시 이동
                        .permitAll()
                );
        return http.build();
    }
}
