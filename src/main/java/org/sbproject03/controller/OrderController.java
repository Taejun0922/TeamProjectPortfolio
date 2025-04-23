package org.sbproject03.controller;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.*;
import org.sbproject03.dto.ProductInfo;
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

    // ✅ 세션에서 cartId 가져오기 (Long 타입으로 변경)
    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj == null) {
      return "redirect:/cart";
    }

    Long cartId = Long.valueOf(cartIdObj.toString()); // cartId를 Long으로 변경

    List<CartItems> cartItems = cartItemService.getCartItems(cartId);
    List<ProductInfo> productInfoList = new ArrayList<>();

    for (CartItems item : cartItems) {
      Product product = item.getProduct();

      // ProductInfo 객체 생성 시 필요한 인자들 전달
      ProductInfo productInfo = new ProductInfo(
          product.getProductId(),  // productId
          product.getProductName(), // productName
          product.getProductPrice(), // productPrice
          item.getQuantity()        // quantity
      );

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
    Long cartId = Long.valueOf(cartIdObj.toString()); // cartId를 Long으로 변경

    CartItems item = cartItemService.findByCartIdAndProductId(cartId, productId);
    if (item == null) {
      return "redirect:/cart";
    }

    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setProduct(item.getProduct());
    order.setQuantity(item.getQuantity());
    order.setTotalPrice(item.getProduct().getProductPrice() * item.getQuantity());

    orderService.save(order);

    cartItemService.deleteByCartIdAndProductId(cartId, productId);

    return "order/orderCustomerInfo";
  }
}
