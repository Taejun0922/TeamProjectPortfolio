package temp;

import org.sbproject03.domain.Product;
import org.sbproject03.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
@RequestMapping("/product")
public class ProductController {

  @Autowired
  private ProductService ProductService;

  @GetMapping
  public String getMainPage() {
    return "main";
  }

  @GetMapping("/{productId}")
  public String getProductById(@PathVariable String productId, Model model) {
    Product product = new Product();
    product = ProductService.getProductById(productId);
    model.addAttribute("product", product);
    return "product";
  }

  @PostMapping("/{productId}")
  public String
}
