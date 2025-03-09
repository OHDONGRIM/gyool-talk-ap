package com.gyooltalk.config;

import com.gyooltalk.security.JwtAuthenticationEntryPoint;
import com.gyooltalk.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 사용자 상세 정보를 제공하는 서비스
    private final UserDetailsService userDetailsService;

    // 인증 예외 처리를 위한 엔트리 포인트
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    // JWT 기반 인증 필터
    private final JwtAuthenticationFilter authenticationFilter;

    public static final String[] AUTH_WHITELIST = {
            "/login",
            "/join/**",
            "/user/**",
            "/ws/**",
            "/",
            "/favicon.ico",
            "/static/**",  // 정적 리소스 경로
            "/index.html", // React 애플리케이션의 진입점
            "/**/*.js",    // JavaScript 파일
            "/**/*.css",   // CSS 파일
            "/**/*.png",   // 이미지 파일
            "/**/*.jpg",
            "/**/*.jpeg",
            "/**/*.gif",
            "/**/*.svg",
            "/**/*.ico",
            "/**/*.html"
    };

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 빈 정의.
     * 스프링 시큐리티의 인증 매니저를 빈으로 등록하여 인증 과정을 관리합니다.
     *
     * @param configuration 인증 구성 정보
     * @return AuthenticationManager 인스턴스
     * @throws Exception 예외 발생 시
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(withDefaults());

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
