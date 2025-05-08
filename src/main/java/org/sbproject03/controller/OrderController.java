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

    Product product = productService.getProductById(productId);  // 상품 조회
    if (product == null) {
      return "redirect:/cart";
    }

    // 임시 CartItems 생성 (DB 저장 X)
    CartItems item = new CartItems();
    item.setProduct(product);
    item.setQuantity(quantity);

    List<CartItems> cartItems = new ArrayList<>();
    cartItems.add(item);

    // 가상 Cart 생성
    Cart tempCart = Cart.createCart(member);  // 팩토리 메소드 사용
    tempCart.setCartItems(cartItems);
    tempCart.updateTotalPrice();  // subtotal로 총 금액 계산

    model.addAttribute("member", member);
    model.addAttribute("cartItems", cartItems);  // 뷰에 cartItems 제공
    model.addAttribute("cart", tempCart);        // 총 금액 출력용

    return "order/orderCustomerInfo";
  }

  // 단일 상품 주문 처리
  @PostMapping("/{productId}")
  public String orderSingleProduct(@PathVariable String productId, @RequestParam("quantity") int quantity) {
    // 로그인 확인
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";  // 로그인 안된 사용자 리다이렉트
    }

    // 카트 정보 확인
    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj == null) {
      // 카트가 없으면 카트 정보 없이 개별 주문을 처리
      System.out.println("카트 정보 없음. 개별 주문 처리 진행.");
    }

    // 카트에서 상품 찾기: 카트 정보 없이 상품을 찾기
    CartItems item = cartItemService.findCartItemByProductId(productId);  // 카트가 없으면 상품만 찾기
    if (item == null) {
      return "redirect:/cart"; // 상품이 카트에 없으면 카트 페이지로 리다이렉트
    }

    Product product = item.getProduct();
    int itemQuantity = item.getQuantity();

    // 주문 수량이 카트의 수량보다 많으면 리다이렉트
    if (quantity > itemQuantity) {
      return "redirect:/cart"; // 수량 초과 시 다시 카트 페이지로 리다이렉트
    }

    // 주문 생성
    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setStatus(OrderStatus.ORDERED); // 기본 주문 상태

    ProductOrderItem orderItem = new ProductOrderItem();
    orderItem.setProduct(product);
    orderItem.setQuantity(quantity);
    orderItem.setPrice(product.getProductPrice() * quantity); // 가격 계산
    orderItem.setOrder(order);  // 양방향 연관 설정

    order.getItems().add(orderItem);

    orderService.save(order);  // 주문 저장

    // 장바구니에서 해당 상품 삭제 또는 수량 감소: 카트 정보가 없으면 상품을 삭제
    if (cartIdObj != null) {
      Long cartId = Long.parseLong(cartIdObj.toString());
      cartItemService.deleteByProductId(productId);  // 카트에서 상품 완전 삭제
    }

    return "redirect:/main"; // 주문 완료 후 메인 페이지로 이동
  }
}

