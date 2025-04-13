package temp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

//@Controller
public class LoginController {

  @PostMapping("/login")
  public String login() {
    return null;
  }

  @GetMapping("/register")
  public String register() {
    return "register";
  }

  @GetMapping("/logout")
  public String logout() {
    return "logout";
  }


}
