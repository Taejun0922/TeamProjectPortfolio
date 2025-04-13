package org.sbproject03;

import org.sbproject03.domain.Member;
import org.sbproject03.domain.Role;
import org.sbproject03.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication
public class SpringBootApp {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootApp.class, args);
  }

  @Bean
  public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
    return new HiddenHttpMethodFilter();
  }
/*
  // 관리자 정보를 Member 엔터티에 등록
  @Bean
  public CommandLineRunner run(MemberService memberService) throws Exception {
    return (String[] args) -> {
      Member member = new Member();
      member.setMemberId("admin");
      member.setMemberName("관리자");
      member.setMemberPhone("010-9999-9999");
      member.setMemberEmail("admin@korea.com");
      String password = new BCryptPasswordEncoder().encode("a1234");
      member.setMemberPassword(password);
      member.setRole(Role.ADMIN);
      member.setMemberAddress("서울시 , 11번지");
      memberService.register(member);
    };
  }

  // 유저 정보를 Member 엔터티에 등록
  @Bean
  public CommandLineRunner runUser(MemberService memberService) throws Exception {
    return (String[] args) -> {
      Member member = new Member();
      member.setMemberId("aaaa");
      member.setMemberName("홍길동");
      member.setMemberPhone("010-1111-1111");
      member.setMemberEmail("aaaa@korea.com");
      String password = new BCryptPasswordEncoder().encode("1234");
      member.setMemberPassword(password);
      member.setRole(Role.USER);
      member.setMemberAddress("부산시 , 99번지");
      memberService.register(member);
    };
  }
*/
}
