package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sbproject03.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

  @Autowired
  private MemberService memberService;

  // 로그인 실패 시 에러 메시지 전달
  @GetMapping("/loginFailed")
  public String loginError(RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호를 확인해주세요.");
    System.out.println("❌ 로그인 실패: 아이디 또는 비밀번호 오류");
    return "redirect:/main"; // 로그인 실패 시 main 페이지로 리디렉션
  }

  @GetMapping("/register")
  public String register() {
    return "register";  // 회원가입 페이지
  }

  // 로그아웃 시 세션 삭제
  @GetMapping("/logout")
  public String logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();  // 세션 삭제
      System.out.println("✅ 로그아웃: 세션 삭제 완료");
    } else {
      System.out.println("⚠️ 로그아웃 시도: 세션이 이미 없음");
    }
    return "redirect:/main";  // 로그아웃 후 main 페이지로 리디렉션
  }
}
