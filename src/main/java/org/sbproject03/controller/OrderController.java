package org.sbproject03.controller;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.*;
import org.sbproject03.dto.ProductInfo;
import org.sbproject03.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private ProductOrderService orderService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private HttpSession session;

  @Autowired
  private CartItemService cartItemService;

  @Autowired
  private CartService cartService;

  @Autowired
  private ProductService productService;

  // 주문 페이지 출력
  @GetMapping
  public String orderPage(HttpSession session, Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login"; // 로그인 안 되어 있으면 리다이렉트
    }

    // 기존 데이터
    model.addAttribute("member", member);

    // ✅ 추가: 카트 및 항목 조회
    List<Cart> carts = cartService.getCartsByMemberId(member.getId());
    if (carts.isEmpty()) {
      model.addAttribute("cartItems", List.of());
      model.addAttribute("cart", Cart.createCart(member));
    } else {
      Cart cart = carts.get(0);
      List<CartItems> cartItems = cartItemService.getCartItems(cart.getCartId());

      model.addAttribute("cart", cart);
      model.addAttribute("cartItems", cartItems);
    }

    return "order/orderCustomerInfo";
  }

  // 전체 주문 처리
  @PostMapping
  public String orderPro() {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj == null) {
      return "redirect:/cart";
    }
    Long cartId = Long.valueOf(cartIdObj.toString()); // cartId를 Long으로 변경

    ProductOrder productOrder = new ProductOrder();
    productOrder.setMember(member);
    productOrder.setCartId(cartId);
    orderService.save(productOrder);

    // 세션에서 cartId 제거
    session.removeAttribute("cartId");

    return "redirect:/main";
  }

// 단일 상품 주문 처리
//  @PostMapping("/{productId}")
//  public String orderSingleProduct(@PathVariable String productId) {
//    Member member = (Member) session.getAttribute("userLoginInfo");
//    if (member == null) {
//      return "redirect:/login";
//    }
//
//    Object cartIdObj = session.getAttribute("cartId");
//    if (cartIdObj == null) {
//      return "redirect:/cart";
//    }
//    Long cartId = Long.valueOf(cartIdObj.toString()); // cartId를 Long으로 변경
//
//    CartItems item = cartItemService.findByCartIdAndProductId(cartId, productId);
//    if (item == null) {
//      return "redirect:/cart";
//    }
//
//    ProductOrder order = new ProductOrder();
//    order.setMember(member);
//    order.setProduct(item.getProduct());
//    order.setQuantity(item.getQuantity());
//    order.setTotalPrice(item.getProduct().getProductPrice() * item.getQuantity());
//
//    orderService.save(order);
//
//    cartItemService.deleteByCartIdAndProductId(cartId, productId);
//
//    return "order/orderCustomerInfo";
//  }
}

