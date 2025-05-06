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

  // 단일 상품 주문 페이지 이동
  @GetMapping("/{productId}")
  public String showSingleOrderPage(@PathVariable String productId, HttpSession session, Model model) {
    // 로그인된 사용자 확인
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    // 장바구니 ID 확인
    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj == null) {
      return "redirect:/cart";  // 장바구니가 없으면 다시 카트 페이지로 리다이렉트
    }
    Long cartId = Long.valueOf(cartIdObj.toString());

    // 카트에서 단일 상품 조회
    Cart cart = cartService.read(cartId);  // Cart 객체를 가져옵니다.
    if (cart == null) {
      System.out.println("Cart not found for cartId: " + cartId);  // 카트가 없을 경우 확인
      return "redirect:/cart";  // 카트가 없으면 장바구니로 리다이렉트
    }

    System.out.println("Found Cart: " + cart);  // 카트 객체 확인

    // 장바구니에서 상품 찾기
    CartItems item = cartItemService.findByCartIdAndProductId(cartId, productId);
    if (item == null) {
      return "redirect:/cart";  // 장바구니에 해당 상품이 없으면 다시 장바구니로 이동
    }

    Product product = item.getProduct();
    int quantity = item.getQuantity();
    int totalPrice = product.getProductPrice() * quantity;

    // 모델에 데이터 추가
    model.addAttribute("member", member);
    model.addAttribute("product", product);
    model.addAttribute("quantity", quantity);
    model.addAttribute("totalPrice", totalPrice);  // 단일 상품의 총 가격
    model.addAttribute("cartTotalPrice", cart.getTotalPrice());  // 카트의 전체 가격

    // 주문 페이지로 이동
    return "order/orderCustomerInfo";  // 단일 상품 주문 페이지로 이동
  }


  // 단일 상품 주문 처리
  @PostMapping("/{productId}")
  public String orderSingleProduct(@PathVariable String productId) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj == null) {
      return "redirect:/cart";
    }

    Long cartId = Long.parseLong(cartIdObj.toString());

    // 장바구니에서 해당 상품 가져오기
    CartItems item = cartItemService.findByCartIdAndProductId(cartId, productId);
    if (item == null) {
      return "redirect:/cart";
    }

    Product product = item.getProduct();
    int quantity = item.getQuantity();

    // 1. 주문 생성
    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setCartId(cartId);  // 필요 시 장바구니 ID 저장

    // 2. 주문 항목 생성
    ProductOrderItem orderItem = new ProductOrderItem();
    orderItem.setProduct(product);
    orderItem.setQuantity(quantity);
    orderItem.setPrice(product.getProductPrice() * quantity);
    orderItem.setOrder(order);  // 양방향 연관 설정

    // 3. 주문에 항목 추가
    order.getItems().add(orderItem);

    // 4. 저장
    orderService.save(order);

    // 5. 장바구니 항목 제거
    cartItemService.deleteByCartIdAndProductId(cartId, productId);

    // 6. 주문 정보 페이지로 이동 (모델에 전달하려면 모델 파라미터 필요)
    return "redirect:/order/customer-info?orderId=" + order.getId(); // 예시로 주문 정보 페이지로 이동
  }
}

