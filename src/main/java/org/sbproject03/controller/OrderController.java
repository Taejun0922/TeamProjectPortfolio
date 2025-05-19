package org.sbproject03.controller;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.*;
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

  @Autowired private ProductOrderService orderService;
  @Autowired private MemberService memberService;
  @Autowired private HttpSession session;
  @Autowired private CartItemService cartItemService;
  @Autowired private CartService cartService;
  @Autowired private ProductService productService;

  // 전체 상품 주문 페이지 이동
  @GetMapping
  public String orderPage(HttpSession session, Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    model.addAttribute("member", member);
    model.addAttribute("isSingleOrder", false); // 👉 전체 주문용

    System.out.println("[GET /order] 전체 주문 페이지 요청 by memberId: " + member.getId());

    List<Cart> carts = cartService.getCartsByMemberId(member.getId());
    if (carts.isEmpty()) {
      System.out.println("장바구니가 비어 있습니다.");
      model.addAttribute("cartItems", List.of());
      model.addAttribute("cart", Cart.createCart(member));
    } else {
      Cart cart = carts.get(0);
      session.setAttribute("cartId", cart.getCartId());
      System.out.println("세션에 cartId 저장됨: " + cart.getCartId());

      List<CartItems> cartItems = cartItemService.getCartItems(cart.getCartId());
      model.addAttribute("cart", cart);
      model.addAttribute("cartItems", cartItems);
      model.addAttribute("orderType", "cart"); // 플래그 추가
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
      System.out.println("[POST /order] 세션에 cartId 없음. 장바구니로 리다이렉트");
      return "redirect:/cart";
    }

    Long cartId = Long.parseLong(cartIdObj.toString());
    System.out.println("[POST /order] 전체 주문 실행 - cartId: " + cartId + ", memberId: " + member.getId());

    orderService.placeOrder(cartId, member);
    session.removeAttribute("cartId");

    return "redirect:/main";
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

    Product product = productService.getProductById(productId);
    if (product == null || quantity <= 0) {
      return "redirect:/cart";
    }

    List<Cart> carts = cartService.getCartsByMemberId(member.getId());
    if (carts.isEmpty()) {
      return "redirect:/cart";
    }

    Cart cart = carts.get(0);
    session.setAttribute("cartId", cart.getCartId());
    System.out.println("[GET /order/single] 단일 주문 페이지 - cartId: " + cart.getCartId() + ", productId: " + productId);

    List<CartItems> cartItems = cartItemService.getCartItems(cart.getCartId());
    CartItems targetItem = null;
    for (CartItems item : cartItems) {
      if (item.getProduct().getProductId().equals(productId)) {
        targetItem = item;
        break;
      }
    }

    if (targetItem == null) {
      System.out.println("장바구니에 해당 상품이 없습니다: " + productId);
      return "redirect:/cart";
    }

    targetItem.setQuantity(quantity);
    List<CartItems> singleItemList = new ArrayList<>();
    singleItemList.add(targetItem);

    model.addAttribute("member", member);
    model.addAttribute("cartItems", singleItemList);
    model.addAttribute("orderType", "single"); // 플래그 추가
    model.addAttribute("productId", productId); // 👉 뷰에서 form action용

    return "order/orderCustomerInfo";
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

    System.out.println("[POST /order/" + productId + "] 단일 주문 실행 - quantity: " + quantity);

    orderService.placeSingleOrder(member, product, quantity);

    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj != null) {
      Long cartId = Long.parseLong(cartIdObj.toString());
      System.out.println("단일 주문 후 해당 상품 삭제 - cartId: " + cartId + ", productId: " + productId);
      cartItemService.deleteByCartIdAndProductId(cartId, productId);
    }

    return "redirect:/main";
  }

  // 카트에서 선택상품 주문페이지 이동
  @PostMapping("/selected")
  public String orderSelectedProducts(@RequestParam List<String> productIds,
                                      @RequestParam List<Integer> quantities,
                                      Model model, HttpSession session) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login"; // 로그인 안 되어 있으면 로그인 페이지로 리다이렉트
    }

    List<CartItems> selectedItems = new ArrayList<>();
    for (int i = 0; i < productIds.size(); i++) {
      String productId = productIds.get(i);
      int quantity = quantities.get(i);

      Product product = productService.getProductById(productId);
      if (product != null && quantity > 0) {
        CartItems cartItem = new CartItems();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        selectedItems.add(cartItem);
      }
    }

    model.addAttribute("member", member);
    model.addAttribute("cartItems", selectedItems);
    model.addAttribute("orderType", "selected"); // 주문 유형 플래그 설정

    return "order/orderCustomerInfo"; // 선택된 상품들을 포함한 주문 페이지로 이동
  }

  // 카트에서 선택한 주문 처리
  @PostMapping("/processSelectedOrder")
  public String orderSelectedProducts(@RequestParam("productIds") List<String> productIds,
                                      @RequestParam("quantities") List<Integer> quantities) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) return "redirect:/login";

    for (int i = 0; i < productIds.size(); i++) {
      String productId = productIds.get(i);
      int quantity = quantities.get(i);

      Product product = productService.getProductById(productId);
      if (product != null && quantity > 0) {
        orderService.placeSingleOrder(member, product, quantity);

        Object cartIdObj = session.getAttribute("cartId");
        if (cartIdObj != null) {
          Long cartId = Long.parseLong(cartIdObj.toString());
          cartItemService.deleteByCartIdAndProductId(cartId, productId);
        }
      }
    }

    return "redirect:/main";
  }

  // 상세페이지에서 주문페이지로 이동
  @GetMapping("/direct")
  public String directOrderPage(@RequestParam("productId") String productId,
                                @RequestParam("quantity") int quantity,
                                Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    Product product = productService.getProductById(productId);
    if (product == null || quantity <= 0) {
      return "redirect:/main";
    }

    // 직접 주문용 가짜 CartItems 리스트 만들기
    CartItems tempItem = new CartItems();
    tempItem.setProduct(product);
    tempItem.setQuantity(quantity);

    List<CartItems> directOrderList = new ArrayList<>();
    directOrderList.add(tempItem);

    model.addAttribute("member", member);
    model.addAttribute("cartItems", directOrderList);
    model.addAttribute("orderType", "direct"); // ← 플래그 추가
    model.addAttribute("productId", productId); // ← 뷰에서 POST에 필요

    return "order/orderCustomerInfo";
  }

  // 상세페이지에서 주문처리
  @PostMapping("/direct")
  public String placeDirectOrder(@RequestParam("productId") String productId,
                                 @RequestParam("quantity") int quantity) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    Product product = productService.getProductById(productId);
    if (product == null || quantity <= 0) {
      return "redirect:/main";
    }

    System.out.println("[POST /order/direct] 직접 주문 실행 - productId: " + productId + ", quantity: " + quantity);
    orderService.placeSingleOrder(member, product, quantity);

    return "redirect:/main";
  }

}