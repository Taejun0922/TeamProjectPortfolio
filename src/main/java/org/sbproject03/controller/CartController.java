package org.sbproject03.controller;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.Cart;
import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Member;
import org.sbproject03.domain.Product;
import org.sbproject03.dto.CartResponse;
import org.sbproject03.dto.UpdateCartRequest;
import org.sbproject03.service.CartItemService;
import org.sbproject03.service.CartService;
import org.sbproject03.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
  private HttpSession session;

  // 장바구니 화면 이동
  @GetMapping
  public ModelAndView requestCartId(ModelAndView mav) {
    Cart cart = getCart(); // 카트 조회 메서드
    Long cartId = cart.getCartId(); // cartId는 Long 타입으로 수정
    List<CartItems> cartItems = cartItemService.getCartItems(cartId); // 장바구니 항목 조회

    // 로그 출력
    System.out.println("===== 카트 정보 =====");
    System.out.println("cartId: " + cartId);
    System.out.println("totalPrice: " + cart.getTotalPrice());
    System.out.println("cart 객체: " + cart);
    System.out.println("=====================");

    mav.addObject("cart", cart);
    mav.addObject("cartItems", cartItems);
    mav.setViewName("cart/cart");
    return mav;
  }


  // 장바구니 상품 추가
  @PostMapping
  public String addCartItem(@RequestParam("productId") String productId, @RequestParam("quantity") int quantity) {
    Cart cart = getCart();
    Long cartId = cart.getCartId(); // cartId는 Long 타입으로 수정

    Product product = productService.getProductById(productId);
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);

    boolean found = false;
    for (CartItems cartItem : cartItems) {
      if (cartItem.getProduct().getProductId().equals(productId)) {
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemService.save(cartItem);
        found = true;
        break;
      }
    }

    // 장바구니가 없을 때
    if (!found) {
      CartItems cartItem = new CartItems(cart, product, quantity);
      cartItems.add(cartItem);
      cartItemService.save(cartItem);
    }

    updateTotalPrice(cartId);
    return "redirect:/cart";
  }

  // 장바구니 상품 삭제
  @DeleteMapping("/{productId}")
  public String deleteCartItem(@PathVariable String productId) {
    Cart cart = getCart();
    Long cartId = cart.getCartId(); // cartId는 Long 타입으로 수정
    cartItemService.deleteCartItem(productId);

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
      // Cart를 생성할 때는 이제 Cart.createCart(member)로 생성해야 합니다.
      Cart cart = cartService.createCart(member);
      cartService.save(cart); // 생성된 카트를 저장합니다.
      return cart;
    }

    // 첫 번째 카트를 가져옴 (필요에 따라 여러 카트 중 하나를 선택할 수 있음)
    return carts.get(0);
  }

  // 카트 상품가격 업데이트 메서드 (매핑 X, 호출용)
  public void updateTotalPrice(Long cartId) { // cartId를 Long 타입으로 수정
    Cart cart = cartService.read(cartId); // cartId는 Long 타입으로 수정
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);

    int totalPrice = 0;
    for (CartItems item : cartItems) {
      Product product = item.getProduct(); // 🔥 여기도 바로 접근
      totalPrice += product.getProductPrice() * item.getQuantity();
    }

    cart.setTotalPrice(totalPrice);
    cartService.save(cart);
  }

  // 수량 변화시 카트의 전체 가격업데이트
  @PostMapping("/update")
  @ResponseBody
  public CartResponse updateCartItemQuantity(@RequestBody UpdateCartRequest request) {
    Cart cart = getCart();
    Long cartId = cart.getCartId(); // cartId는 Long 타입으로 수정

    CartItems targetItem = null;
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);

    for (CartItems item : cartItems) {
      if (item.getProduct().getProductId().equals(request.getProductId())) {
        item.setQuantity(request.getQuantity());
        cartItemService.save(item);
        targetItem = item;
        break;
      }
    }

    updateTotalPrice(cartId);

    int itemTotalPrice = 0;
    if (targetItem != null) {
      Product product = targetItem.getProduct();
      itemTotalPrice = product.getProductPrice() * targetItem.getQuantity();
    }

    // 응답 객체 생성
    CartResponse response = new CartResponse(true, itemTotalPrice, cartService.read(cartId).getTotalPrice());

    return response;
  }
}
