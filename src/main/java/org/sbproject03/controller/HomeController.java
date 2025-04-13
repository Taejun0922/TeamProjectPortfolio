package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.Member;
import org.sbproject03.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public String welcome(Model model, Authentication authentication, HttpServletRequest request, HttpSession session) {
        System.out.println("🛠 HomeController 진입!");  // ✅ 메서드 실행 확인

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ 인증되지 않은 사용자, main 페이지로 이동");
            return "/main";
        }

        Object principal = authentication.getPrincipal();
        System.out.println("🔍 Principal 객체: " + principal);

        if (!(principal instanceof User)) {
            System.out.println("❌ Principal이 User 객체가 아님");
            return "redirect:/login";
        }

        User user = (User) principal;
        String userId = user.getUsername();
        System.out.println("✅ 로그인한 사용자 ID: " + userId);

        if (userId == null) {
            System.out.println("❌ userId가 null, 로그인 페이지로 이동");
            return "redirect:/login";
        }

        // DB에서 사용자 정보 가져오기
        Member member = memberService.getMemberById(userId);
        System.out.println("✅ DB에서 가져온 회원 정보: " + member);

        // ✅ 세션이 null인지 체크
        if (session == null) {
            System.out.println("❌ 세션이 null 입니다!");
        } else {
            session.setAttribute("userLoginInfo", member);
            System.out.println("✅ 세션에 저장된 userLoginInfo: " + session.getAttribute("userLoginInfo"));
        }

        model.addAttribute("member", member);
        System.out.println("✅ 모델에 추가된 member: " + member);
        return "redirect:/main";
    }

}
