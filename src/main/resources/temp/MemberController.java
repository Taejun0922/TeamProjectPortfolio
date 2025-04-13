package temp;

import org.springframework.web.bind.annotation.GetMapping;

//@Controller
//@RequestMapping("/mypage")
public class MemberController {

  @GetMapping
  public String mypage() {
    return "/member/mypage";
  }

  @GetMapping("/edit")
  public String mypageEdit() {
    return "/member/edit";
  }


}