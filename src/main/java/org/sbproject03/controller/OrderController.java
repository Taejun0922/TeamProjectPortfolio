package org.sbproject03.controller;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.*;
import org.sbproject03.service.CartItemService;
import org.sbproject03.service.MemberService;
import org.sbproject03.service.ProductOrderService;
import org.sbproject03.service.ProductService;
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
  private ProductService productService;

  // 주문 페이지 출력
  @GetMapping
  public String showOrders(Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      return "redirect:/login";
    }

    model.addAttribute("member", member);

    // ✅ 세션에서 cartId 가져오기
    Object cartIdObj = session.getAttribute("cartId");
    System.out.println("🔍 [OrderController] 세션에서 가져온 cartId: " + cartIdObj);

    if (cartIdObj == null) {
      System.out.println("❗ cartId가 세션에 없음! -> /cart로 이동");
      return "redirect:/cart";
    }
    String cartId = cartIdObj.toString();

    List<CartItems> cartItems = cartItemService.getCartItems(cartId);
    List<ProductInfo> productInfoList = new ArrayList<>();

    for (CartItems item : cartItems) {
      Product product = item.getProduct();
      ProductInfo productInfo = new ProductInfo();
      productInfo.setProductId(product.getProductId());
      productInfo.setProductName(product.getProductName());
      productInfo.setProductPrice(product.getProductPrice());
      productInfo.setQuantity(item.getQuantity());
      productInfoList.add(productInfo);
    }

    model.addAttribute("cartItems", productInfoList);
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
    String cartId = cartIdObj.toString();

    ProductOrder productOrder = new ProductOrder();
    productOrder.setMember(member);
    productOrder.setCartId(cartId);
    orderService.save(productOrder);

    // 세션에서 cartId 제거
    session.removeAttribute("cartId");

    return "redirect:/main";
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
    String cartId = cartIdObj.toString();

    System.out.println("🛒 주문 시도 - cartId: " + cartId + ", productId: " + productId);

    CartItems item = cartItemService.findByCartIdAndProductId(cartId, productId);
    if (item == null) {
      System.out.println("❌ 해당 장바구니 항목을 찾을 수 없습니다.");
      return "redirect:/cart";
    }

    System.out.println("✅ 장바구니 항목 확인됨: 상품명 = " + item.getProduct().getProductName() + ", 수량 = " + item.getQuantity());

    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setProduct(item.getProduct());
    order.setQuantity(item.getQuantity());
    order.setTotalPrice(item.getProduct().getProductPrice() * item.getQuantity());

    orderService.save(order);

    cartItemService.deleteByCartIdAndProductId(cartId, productId);
    System.out.println("🧹 주문 후 장바구니에서 상품 삭제 완료");

    return "order/orderCustomerInfo";
  }
}
