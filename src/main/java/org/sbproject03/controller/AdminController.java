package org.sbproject03.controller;

import org.sbproject03.domain.Member;
import org.sbproject03.domain.ProductOrder;
import org.sbproject03.service.MemberService;
import org.sbproject03.service.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final ProductOrderService orderService;

    @Autowired
    public AdminController(MemberService memberService, ProductOrderService orderService) {
        this.memberService = memberService;
        this.orderService = orderService;
    }

    // ✅ 회원 리스트 (검색 포함)
    @GetMapping("/members")
    public String getMemberList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Member> memberList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            memberList = memberService.searchByMemberId(keyword);
        } else {
            memberList = memberService.findAll();
        }

        model.addAttribute("memberList", memberList);
        model.addAttribute("keyword", keyword); // 검색어 유지
        return "admin/memberList";
    }

    // ✅ 회원 상세 정보 보기
    @GetMapping("/member/view/{id}")
    public String viewMemberDetail(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id);
        List<ProductOrder> orderList = orderService.findById(id); // 주문 내역 조회

        model.addAttribute("member", member);
        model.addAttribute("hasOrders", !orderList.isEmpty()); // 주문 여부만 전달

        return "admin/memberDetail";
    }

    // ✅ 주문 전체 조회 또는 회원 ID로 필터링
    @GetMapping("/orders")
    public String viewAllOrders(@RequestParam(value = "memberId", required = false) String memberId, Model model) {
        List<ProductOrder> orderList;

        if (memberId != null && !memberId.trim().isEmpty()) {
            orderList = orderService.findByMemberId(memberId);
        } else {
            orderList = orderService.findAll();
        }

        model.addAttribute("orderList", orderList);
        model.addAttribute("memberId", memberId); // 검색어 유지용
        return "admin/orderList";
    }

    // ✅ 회원 주문 내역 보기
    @GetMapping("/member/{id}/orders")
    public String viewMemberOrders(@PathVariable Long id, Model model) {
        List<ProductOrder> orderList = orderService.findById(id);
        model.addAttribute("orderList", orderList);
        return "admin/memberOrderList";
    }

}