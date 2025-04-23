package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.sbproject03.domain.Member;
import org.sbproject03.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/myPage")
public class MemberController {

  @Autowired
  private MemberService memberService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping
  public String myPage(Model model) {
    // 로그인한 사용자의 정보를 가져와서 보여주는 로직
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String userId = authentication.getName();
      Member member = memberService.getMemberById(userId);
      model.addAttribute("member", member);
    }
    return "member/myPage";
  }

  // 회원가입 후 자동 로그인 기능 제거 및 세션 저장
  @PostMapping("/register")
  public String register(@Valid @ModelAttribute Member member, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes, HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("alertMessage", "모든 항목을 입력해주세요.");
      return "redirect:/main";
    }

    try {
      // 비밀번호 암호화 후 회원 저장
      Member newMember = Member.createMember(member, passwordEncoder);
      memberService.register(newMember);

      // 로그인 처리: 인증 객체를 생성하여 SecurityContext에 설정
      Authentication authentication = new UsernamePasswordAuthenticationToken(
              newMember.getMemberName(), newMember.getMemberPassword());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 세션에 로그인 정보 저장 (명시적으로 userLoginInfo 세션에 저장)
      HttpSession session = request.getSession();
      session.setAttribute("userLoginInfo", newMember); // 세션에 사용자 정보 저장

      System.out.println("회원가입 성공: 세션에 사용자 정보 저장 -> " + newMember);

      redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다!");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
      return "redirect:/main";
    }

    return "redirect:/main";
  }

  // 회원정보 수정 후 세션 업데이트
  @PostMapping("/edit")
  public String updateMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String userId = authentication.getName();
      Member existingMember = memberService.getMemberById(userId);

      if (existingMember != null) {
        // 수정된 정보 적용
        existingMember.setMemberName(member.getMemberName());
        existingMember.setMemberEmail(member.getMemberEmail());
        existingMember.setMemberPhone(member.getMemberPhone());
        existingMember.setMemberAddress(member.getMemberAddress());

        // 비밀번호 변경이 있는 경우만 업데이트
        if (member.getMemberPassword() != null && !member.getMemberPassword().isEmpty()) {
          existingMember.setMemberPassword(passwordEncoder.encode(member.getMemberPassword()));
        }

        // 회원 정보 업데이트
        memberService.updateMember(existingMember);

        // 세션 정보 업데이트
        HttpSession session = request.getSession();
        session.setAttribute("userLoginInfo", existingMember); // 세션 정보 업데이트
        System.out.println("세션 정보 업데이트 완료: " + existingMember);

        redirectAttributes.addFlashAttribute("successMessage", "회원정보가 수정되었습니다.");
      }
    } else {
      redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
      return "redirect:/main";
    }

    return "redirect:/main";
  }

  // 회원 탈퇴
  @PostMapping("/delete")
  public String deleteMember(HttpServletRequest request, HttpServletResponse response,
                             RedirectAttributes redirectAttributes) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      redirectAttributes.addFlashAttribute("alertMessage", "로그인한 사용자가 없습니다.");
      return "redirect:/main"; // 로그인 정보 없으면 메인으로 이동
    }

    String memberId = authentication.getName();
    try {
      // 회원 삭제 수행
      memberService.deleteMember(memberId);

      // 로그아웃 처리 (Spring Security의 로그아웃 처리)
      new SecurityContextLogoutHandler().logout(request, response, authentication);

      // 세션 삭제
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
        System.out.println("세션 삭제 완료");
      }

      redirectAttributes.addFlashAttribute("successMessage", "회원 탈퇴가 완료되었습니다.");
      return "redirect:/main";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("alertMessage", "회원 탈퇴 중 오류가 발생했습니다.");
      return "redirect:/myPage";
    }
  }

  // 로그아웃
  @GetMapping("/logout")
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Spring Security 로그아웃 처리
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }

    // 세션 직접 삭제
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
      System.out.println("로그아웃: 세션 삭제 완료");
    }

    return "redirect:/main";
  }

  @GetMapping("/productOrder")
  public String orderPage(HttpSession session, Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    model.addAttribute("member", member);
    return "order/orderCustomerInfo";
  }
}
