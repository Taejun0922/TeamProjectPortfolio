package org.sbproject03.controller;

import org.sbproject03.domain.Member;
import org.sbproject03.domain.ProductOrder;
import org.sbproject03.domain.ProductOrderItem;
import org.sbproject03.service.MemberService;
import org.sbproject03.service.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // ✅ 회원 리스트 (검색 포함)
    @GetMapping("/members")
    public String getMemberList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Member> memberList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            memberList = memberService.searchByMemberId(keyword);
        } else {
            memberList = memberService.findAll();
        }

        // 회원별 주문 내역 존재 여부 체크 맵 생성
        Map<Long, Boolean> orderExistMap = new HashMap<>();
        for (Member member : memberList) {
            // member.id로 주문 내역이 있는지 확인
            List<ProductOrder> orders = orderService.findById(member.getId());
            orderExistMap.put(member.getId(), !orders.isEmpty());
        }

        model.addAttribute("memberList", memberList);
        model.addAttribute("keyword", keyword); // 검색어 유지
        model.addAttribute("orderExistMap", orderExistMap); // 주문 내역 존재 여부 전달

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

        // 🔹 주문별 총 금액 계산
        Map<Long, Integer> orderTotalMap = new HashMap<>();
        for (ProductOrder order : orderList) {
            int total = order.getItems().stream()
                    .mapToInt(ProductOrderItem::getPrice)
                    .sum();
            orderTotalMap.put(order.getId(), total);
        }

        model.addAttribute("orderList", orderList);
        model.addAttribute("orderTotalMap", orderTotalMap);
        model.addAttribute("memberId", memberId);
        return "admin/orderList";
    }

    // ✅ 회원 주문 내역 보기
    // ✅ 특정 회원의 주문 내역을 orderList.html로 출력
    @GetMapping("/member/{id}/orders")
    public String viewMemberOrders(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id); // 회원 정보 가져오기
        if (member == null) {
            return "redirect:/admin/members"; // 없는 회원이면 목록으로
        }

        List<ProductOrder> orderList = orderService.findById(id); // 회원의 주문 목록 조회

        // 주문별 총 금액 계산
        Map<Long, Integer> orderTotalMap = new HashMap<>();
        for (ProductOrder order : orderList) {
            int total = order.getItems().stream()
                    .mapToInt(ProductOrderItem::getPrice)
                    .sum();
            orderTotalMap.put(order.getId(), total);
        }

        model.addAttribute("orderList", orderList);
        model.addAttribute("orderTotalMap", orderTotalMap);
        model.addAttribute("memberId", member.getMemberId()); // 검색창에 자동 입력되도록
        return "admin/orderList";
    }

    // ✅ 주문 상세 보기
    @GetMapping("/order/{orderId}")
    public String viewOrderDetail(@PathVariable Long orderId, Model model) {
        ProductOrder order = orderService.findByOrderId(orderId); // 해당 주문 조회
        if (order == null) {
            return "redirect:/admin/orders"; // 주문이 없으면 목록으로
        }

        int totalPrice = order.getItems().stream()
                .mapToInt(ProductOrderItem::getPrice)
                .sum();

        model.addAttribute("order", order);
        model.addAttribute("totalPrice", totalPrice);
        return "admin/orderDetail"; // orderDetail.html로 이동
    }

}