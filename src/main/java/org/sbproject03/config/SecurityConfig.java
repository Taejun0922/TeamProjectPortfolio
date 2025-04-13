package org.sbproject03.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //사용자 권한에 따른 접근 권한 설정
    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .authorizeHttpRequests(
        authorize -> authorize
//            .requestMatchers("/order/**").hasAnyRole("USER", "ADMIN")  // 주문 화면은 회원과 관리자만 할 수 있게 설정
            .anyRequest().permitAll()

    )
//    .formLogin(Customizer.withDefaults()); // 폼에서 로그인 성공과 실패를 처리
//        로그인 처리
            .formLogin(formLogin -> formLogin
                    .loginPage("/main")  // 로그인 폼이 있는 페이지 (GET 요청)
                    .loginProcessingUrl("/login")  // ✅ 로그인 처리 URL (POST 요청)
                    .defaultSuccessUrl("/main", true)  // 로그인 성공 시 이동할 페이지
                    .failureUrl("/loginFailed")  // 로그인 실패 시 이동할 페이지
                    .usernameParameter("username")
                    .passwordParameter("password")
            )
            .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
//        로그아웃 처리
        .logout(
            logout -> logout
               .logoutUrl("/logout")
               .logoutSuccessUrl("/main")
        );
    return http.build();
  }
}