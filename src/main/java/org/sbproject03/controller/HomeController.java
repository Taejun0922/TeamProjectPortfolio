package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.Cart;
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
        // 로그 추가: HomeController 진입
        System.out.println("🛠 HomeController 진입!");

        // 인증되지 않은 사용자 처리
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            System.out.println("❌ 인증되지 않은 사용자, main 페이지로 이동");
            return "/main"; // 인증되지 않은 사용자는 main 페이지로 리디렉션
        }

        // 로그인한 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            System.out.println("❗ 인증된 사용자 정보가 User 객체가 아닙니다.");
            return "redirect:/login";
        }

        User user = (User) principal;
        String userId = user.getUsername();

        // 사용자 ID가 null인 경우 처리
        if (userId == null) {
            System.out.println("❗ 사용자 ID가 null입니다. 로그인 페이지로 리다이렉트");
            return "redirect:/login";
        }

        // DB에서 사용자 정보 가져오기
        Member member = memberService.getMemberById(userId);
        if (member == null) {
            System.out.println("❌ DB에서 해당 사용자 정보를 찾을 수 없습니다.");
            return "redirect:/login";
        }
        System.out.println("✅ DB에서 가져온 회원 정보: " + member);

        // 세션에 사용자 정보 저장
        session.setAttribute("userLoginInfo", member);
        System.out.println("✅ 세션에 저장된 userLoginInfo: " + session.getAttribute("userLoginInfo"));

        // 장바구니 정보 가져오기
        Cart cart = memberService.getLatestCartByMember(member);
        if (cart != null) {
            // 장바구니 정보가 존재하면 세션에 cartId 저장
            Long cartId = cart.getCartId();
            if (cartId != null) {
                session.setAttribute("cartId", cartId);
                System.out.println("🛒 세션에 저장된 cartId: " + cartId);
            } else {
                System.out.println("⚠️ cartId가 null입니다. 장바구니 정보가 잘못되었습니다.");
            }
        } else {
            // 장바구니가 없을 경우 경고 메시지 출력
            System.out.println("⚠️ 해당 회원의 Cart가 없습니다.");
        }

        // 회원 정보 모델에 추가
        model.addAttribute("member", member);

        // 로그 추가: 리디렉션 전에 어떤 페이지로 가는지 확인
        System.out.println("➡️ 메인 페이지로 리다이렉트: /main");

        return "redirect:/main"; // 메인 페이지로 리디렉션
    }
}