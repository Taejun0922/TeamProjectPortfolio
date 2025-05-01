package org.sbproject03.controller;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.*;
import org.sbproject03.dto.ProductInfo;
import org.sbproject03.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
      return "redirect:/login";
    }

    model.addAttribute("member", member);

    List<Cart> carts = cartService.getCartsByMemberId(member.getId());
    if (carts.isEmpty()) {
      model.addAttribute("cartItems", List.of());
      model.addAttribute("cart", Cart.createCart(member));
    } else {
      Cart cart = carts.get(0);
      session.setAttribute("cartId", cart.getCartId()); // ✅ 세션에 cartId 저장
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

    Long cartId = Long.parseLong(cartIdObj.toString());

    orderService.placeOrder(cartId, member);
    session.removeAttribute("cartId");

    return "redirect:/main"; // 주문 완료 후 이동
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

