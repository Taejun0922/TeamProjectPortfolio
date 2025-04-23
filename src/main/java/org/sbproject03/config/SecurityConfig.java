package org.sbproject03.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 스프링 시큐리티 설정 클래스
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // 비밀번호 암호화
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 사용자 권한에 따른 접근 권한 설정
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf().disable() // CSRF 보호 비활성화
            .authorizeHttpRequests(authorize -> authorize
                    .anyRequest().permitAll()  // 모든 요청에 대해 접근 허용
            )
            .formLogin(formLogin -> formLogin
                    .loginPage("/main")  // 로그인 폼이 있는 페이지 (GET 요청)
                    .loginProcessingUrl("/login")  // 로그인 처리 URL (POST 요청)
                    .defaultSuccessUrl("/main", true)  // 로그인 성공 시 이동할 페이지
                    .failureUrl("/loginFailed")  // 로그인 실패 시 이동할 페이지
                    .usernameParameter("username")
                    .passwordParameter("password")
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/main")  // 로그아웃 후 이동할 페이지
            );

    return http.build();
  }
}
