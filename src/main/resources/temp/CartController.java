package temp;

import jakarta.servlet.http.HttpServletRequest;
//import org.sbproject03.domain.Cart;
import org.sbproject03.domain.Cart;
import org.sbproject03.service.CartService;
import org.sbproject03.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@Controller
//@RequestMapping("/cart")
public class CartController {

  @Autowired
  private CartService cartService;
  @Autowired
  private ProductService productService;

//  장바구니 화면 이동
  @GetMapping
  public String requestCartId(HttpServletRequest request) {
    String sessionId = request.getSession(true).getId();
    return "redirect:/cart/" + sessionId;
  }

//  장바구니 추가
  @PostMapping
  public @ResponseBody Cart create(@RequestBody Cart cart) {
    return cartService.create(cart);
  }

//  장바구니 읽기
//  @GetMapping("/{cartId}")
//  public @ResponseBody Cart read(@PathVariable Long cartId) {
//    return cartService.read(cartId);
//  }
}
