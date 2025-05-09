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

  // 전체 상품 주문 페이지 이동
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
      model.addAttribute("cart", Cart.createCart(member));  // 팩토리 메소드 사용
    } else {
      Cart cart = carts.get(0);
      session.setAttribute("cartId", cart.getCartId()); // 세션에 cartId 저장
      // 수정된 부분: cartId로 장바구니 아이템을 가져옵니다.
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

  // 단일 상품 주문 페이지 이동
  @GetMapping("/single")
  public String showSingleOrderPage(@RequestParam("id") String productId,
                                    @RequestParam("quantity") int quantity,
                                    Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    // 상품 정보 조회
    Product product = productService.getProductById(productId);  // 상품 조회
    if (product == null) {
      return "redirect:/cart"; // 상품이 없다면 장바구니로 리다이렉트
    }

    // CartItems 생성
    CartItems item = new CartItems();
    item.setProduct(product);
    item.setQuantity(quantity);

    List<CartItems> cartItems = new ArrayList<>();
    cartItems.add(item);  // 주문할 상품을 담은 리스트 생성

    // 모델에 상품 정보 전달
    model.addAttribute("member", member);
    model.addAttribute("cartItems", cartItems);  // 주문할 상품 정보만 전달

    return "order/orderCustomerInfo"; // 주문 정보 페이지로 이동
  }



  // 단일 상품 주문 처리
  @PostMapping("/{productId}")
  public String orderSingleProduct(@PathVariable String productId,
                                   @RequestParam("quantity") int quantity) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    Product product = productService.getProductById(productId);
    if (product == null || quantity <= 0) {
      return "redirect:/cart";
    }

    // 👉 개별 상품만 주문 처리
    orderService.placeSingleOrder(member, product, quantity);

    // 👉 카트에서 해당 상품만 삭제 (cartId는 세션에 저장되어 있어야 함)
    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj != null) {
      Long cartId = Long.parseLong(cartIdObj.toString());
      cartItemService.deleteByCartIdAndProductId(cartId, productId);
    }

    return "redirect:/main";
  }

}

