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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

  @GetMapping
  public String showOrders(Model model) {
    Member member = (Member) session.getAttribute("userLoginInfo");
    model.addAttribute("member", member);

    List<CartItems> cartItems = cartItemService.getCartItems(member.getCartId());
    List<ProductInfo> productInfoList = new ArrayList<>();

    for (CartItems item : cartItems) {
      Product product = item.getProduct(); // 🔥 이미 연관된 Product 객체
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


  @PostMapping
  public String orderPro() {
    ProductOrder productOrder = new ProductOrder();
    Member member = (Member) session.getAttribute(("userLoginInfo"));
    productOrder.setCartId(member.getCartId());
    orderService.save(productOrder);
    memberService.deleteCartId(member.getMemberId());
    return "main";
  }
}
