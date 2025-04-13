package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.Cart;
import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Member;
import org.sbproject03.domain.Product;
import org.sbproject03.dto.UpdateCartRequest;
import org.sbproject03.service.CartItemService;
import org.sbproject03.service.CartService;
import org.sbproject03.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

  @Autowired
  private CartService cartService;
  @Autowired
  private ProductService productService;
  @Autowired
  private CartItemService cartItemService;
  @Autowired
  private HttpServletRequest request;
  @Autowired
  private HttpSession session;

  // 장바구니 화면 이동
  @GetMapping
  public ModelAndView requestCartId(HttpServletRequest request, ModelAndView mav) {
    Cart cart = getCart(); // 카트 조회 메서드
    String cartId = cart.getCartId().toString(); // cartId는 이제 Long 타입이 아니라 자동 생성됨
    List<CartItems> cartItems = cartItemService.getCartItems(cartId); // 장바구니 항목 조회
    mav.addObject("cart", cart);
    mav.addObject("cartItems", cartItems);
    mav.setViewName("cart/cart");
    return mav;
  }

  // 장바구니 상품 추가
  @PostMapping
  public String addCartItem(@RequestParam(value="productId") String productId, @RequestParam(value="quantity") int quantity, HttpServletRequest request) {
    Cart cart = getCart();
    String cartId = cart.getCartId().toString(); // cartId는 이제 Long 타입이므로 toString()을 사용
    boolean flag = false;

    // 카트에 상품이 있을 경우 카트에 상품 개수 추가
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);
    for (CartItems cartItem : cartItems) {
      if (cartItem.getProductId().equals(productId)) {
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemService.save(cartItem);
        flag = true; // 추가
        break; // 중복 상품 찾았으면 루프 종료
      }
    }
    if(!flag) {
      CartItems cartItem = new CartItems(cartId, productId, quantity);
      cartItems.add(cartItem);
      cartItemService.save(cartItem);
    }

    // 카트에 담긴 상품 가격 반영
    updateTotalPrice(cartId);
    return "redirect:/cart";
  }

  @DeleteMapping("/{productId}")
  public String deleteCartItem(@PathVariable String productId, HttpServletRequest request) {
    Cart cart = getCart();
    String cartId = cart.getCartId().toString(); // cartId는 Long 타입, toString() 사용
    cartItemService.deleteCartItem(productId, cartId);

    // 카트에 담긴 상품 가격 반영
    updateTotalPrice(cartId);
    return "redirect:/cart";
  }

  // 카트 반환 메서드 - memberId로 카트를 찾도록 변경
  public Cart getCart() {
    Member member = (Member) session.getAttribute("userLoginInfo");
    if (member == null) {
      throw new IllegalStateException("로그인 정보가 없습니다.");
    }

    // memberId로 카트 조회
    List<Cart> carts = cartService.getCartsByMemberId(member.getId());

    // 카트가 없으면 새로 생성
    if (carts.isEmpty()) {
      Cart cart = new Cart();
      cart.setMember(member); // 이 카트는 해당 회원에 속함
      cart.setTotalPrice(0); // 초기 총 가격 0
      cartService.save(cart);
      return cart;
    }

    // 첫 번째 카트를 가져옴 (필요에 따라 여러 카트 중 하나를 선택할 수 있음)
    return carts.get(0);
  }

  // 카트 상품가격 업데이트 메서드 (매핑 X, 호출용)
  public void updateTotalPrice(String cartId) {
    Cart cart = cartService.read(Long.parseLong(cartId)); // cartId는 Long 타입이므로 Long.parseLong 사용
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);
    int totalPrice = 0;
    for (CartItems item : cartItems) {
      String productId = item.getProductId();
      Product product = productService.getProductById(productId);
      totalPrice += product.getProductPrice() * item.getQuantity();
    }
    cart.setTotalPrice(totalPrice);
    cartService.save(cart);
  }

  // 수량 변화시 카트의 전체 가격업데이트
  @PostMapping("/update")
  @ResponseBody
  public Map<String, Object> updateCartItemQuantity(@RequestBody UpdateCartRequest request) {
    Cart cart = getCart();
    String cartId = cart.getCartId().toString();

    CartItems targetItem = null;
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);

    for (CartItems item : cartItems) {
      if (item.getProductId().equals(request.getProductId())) {
        item.setQuantity(request.getQuantity());
        cartItemService.save(item);
        targetItem = item;
        break;
      }
    }

    updateTotalPrice(cartId);

    int itemTotalPrice = 0;
    if (targetItem != null) {
      Product product = productService.getProductById(targetItem.getProductId());
      itemTotalPrice = product.getProductPrice() * targetItem.getQuantity();
    }

    Map<String, Object> response = new HashMap<>();
    response.put("itemTotalPrice", itemTotalPrice);
    response.put("cartTotalPrice", cartService.read(Long.parseLong(cartId)).getTotalPrice());

    return response;
  }
}
