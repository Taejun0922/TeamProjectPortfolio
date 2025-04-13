package org.sbproject03.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.Cart;
import org.sbproject03.domain.Member;
import org.sbproject03.domain.Notice;
import org.sbproject03.domain.Product;
import org.sbproject03.service.CartService;
import org.sbproject03.service.MemberService;
import org.sbproject03.service.NoticeService;
import org.sbproject03.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductController {

  @Autowired
  private ProductService productService;
  @Autowired
  private MemberService memberService;
  @Autowired
  private NoticeService noticeService;
  @Autowired
  private HttpServletRequest request;
  @Autowired
  private CartService cartService;

  private List<Product> limitProducts(List<Product> products, int max) {
    return products.stream().limit(max).collect(Collectors.toList());
  }

  // 메인 페이지 (카테고리별 상품 가져오기)
  @GetMapping({"", "main", "product"})
  public String getMainPage(Model model, HttpSession session) {
    // 로그인 여부 체크
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // 로그인된 사용자 처리
    if (principal instanceof org.springframework.security.core.userdetails.User) {
      String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      Member member = memberService.getMemberById(userId);
      session.setAttribute("userLoginInfo", member);  // 세션에 사용자 정보 저장
      model.addAttribute("member", member);  // 로그인된 회원 정보 추가
    } else {
      // 로그인되지 않은 상태에서는 빈 객체 전달
      model.addAttribute("newMember", new Member());  // 빈 객체
    }

    // 그 외 상품 정보 및 공지사항 처리
    Map<String, List<Product>> productByCategory = new HashMap<>();
    productByCategory.put("productCategory_1", limitProducts(productService.getProductByCategory("TENT"), 8));
    productByCategory.put("productCategory_2", limitProducts(productService.getProductByCategory("SLEEPING_BAG"), 8));
    productByCategory.put("productCategory_3", limitProducts(productService.getProductByCategory("GADGET"), 8));
    productByCategory.put("productCategory_4", limitProducts(productService.getProductByCategory("COOKER"), 8));

    List<Product> bestItems = productService.getBestItems();
    List<List<Product>> bestItemGroup = new ArrayList<>();
    for (int i = 0; i < bestItems.size(); i += 5) {
      bestItemGroup.add(bestItems.subList(i, Math.min(i + 5, bestItems.size())));
    }

    model.addAttribute("productByCategory", productByCategory);
    model.addAttribute("bestItemGroup", bestItemGroup);

    // 공지사항 목록
    List<Notice> notices = noticeService.getRecentNotices();
    model.addAttribute("notices", notices);

    return "main";  // 메인 페이지로 이동
  }


  // 상단 카테고리 버튼 상품 연결
//  @GetMapping("/category/{category}")
//  public String showProductsByPath(@PathVariable("category") String category, Model model) {
//    List<Product> products = productService.getProductByCategory(category);
//    model.addAttribute("products", products);
//    model.addAttribute("selectedCategory", category);
//    return "product/category";
//  }


  @GetMapping("/product/{productId}")
  public ModelAndView getProductById(@PathVariable String productId, ModelAndView mav) {
    Product product = productService.getProductById(productId);

    // 로그인 여부 체크
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      // 로그인된 사용자라면 사용자 정보를 가져오기
      String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      Member member = memberService.getMemberById(userId);
      mav.addObject("member", member);  // 로그인된 회원 정보 추가
    } else {
      // 로그인되지 않은 사용자라면 빈 객체 추가
      mav.addObject("newMember", new Member());  // 빈 객체
    }

    mav.addObject("product", product);
    mav.setViewName("product/product");
    return mav;
  }

  // 전체 상품 보기
  @GetMapping("/category/all")  // 전체 상품을 보여주는 경로
  public String showAllProducts(
          @RequestParam(defaultValue = "0") int page,  // 페이지 번호 (기본값: 0)
          @RequestParam(defaultValue = "8") int size,  // 페이지 크기 (기본값: 8)
          Model model) {

    // Pageable 객체 생성 (page와 size는 사용자가 전달한 값)
    Pageable pageable = PageRequest.of(page, size);

    // 전체 상품 목록을 가져오기 (카테고리 없이 모든 상품을 조회)
    Page<Product> productsPage = productService.getAllProducts(pageable);

    // 블럭의 시작번호 설정 (페이징 블록 시작점)
    int blockCount = 10;  // 블록에 표시할 페이지 개수
    int startPage = page - (page % blockCount);  // 시작 페이지 번호 계산

    // 모델에 데이터 추가
    model.addAttribute("products", productsPage.getContent());  // 페이징된 상품 목록
    model.addAttribute("selectedCategory", "all");  // 선택된 카테고리: "all" (전체 상품)
    model.addAttribute("currentPage", page);  // 현재 페이지
    model.addAttribute("totalPages", productsPage.getTotalPages());  // 총 페이지 수
    model.addAttribute("totalItems", productsPage.getTotalElements());  // 총 아이템 수
    model.addAttribute("pageSize", size);  // 페이지 크기
    model.addAttribute("startPage", startPage);  // 블록의 시작 페이지 번호
    model.addAttribute("blockCount", blockCount);  // 블록의 개수

    // 로그인 여부 체크
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      // 로그인된 사용자라면 사용자 정보를 가져오기
      String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      Member member = memberService.getMemberById(userId);
      model.addAttribute("member", member);  // 로그인된 회원 정보 추가
    } else {
      // 로그인되지 않은 사용자라면 빈 객체 추가
      model.addAttribute("newMember", new Member());  // 빈 객체
    }

    return "product/category";  // 전체 상품 페이지
  }


  // 카테고리별 상품 페이징 조회
  @GetMapping("/category/{category}")
  public String showProductsByPath(
          @PathVariable("category") String productCategory,
          @RequestParam(defaultValue = "0") int page, // 페이지 번호 (기본값: 0)
          @RequestParam(defaultValue = "8") int size, // 페이지 크기 (기본값: 10)
          Model model) {

    // Pageable 객체 생성 (page와 size는 사용자가 전달한 값)
    Pageable pageable = PageRequest.of(page, size);

    // 페이징된 데이터 가져오기
    Page<Product> productsPage = productService.getProductByCategory(productCategory, pageable);

    // 블럭의 시작번호 설정 (페이징 블록 시작점)
    int blockCount = 10; // 블록에 표시할 페이지 개수
    int startPage = page - (page % blockCount); // 시작 페이지 번호 계산

    // 모델에 데이터 추가
    model.addAttribute("products", productsPage.getContent()); // 페이징된 상품 목록
    model.addAttribute("selectedCategory", productCategory); // 선택된 카테고리
    model.addAttribute("currentPage", page); // 현재 페이지
    model.addAttribute("totalPages", productsPage.getTotalPages()); // 총 페이지 수
    model.addAttribute("totalItems", productsPage.getTotalElements()); // 총 아이템 수
    model.addAttribute("pageSize", size); // 페이지 크기
    model.addAttribute("startPage", startPage); // 블록의 시작 페이지 번호
    model.addAttribute("blockCount", blockCount); // 블록의 개수

    // 로그인 여부 체크
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      // 로그인된 사용자라면 사용자 정보를 가져오기
      String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      Member member = memberService.getMemberById(userId);
      model.addAttribute("member", member);  // 로그인된 회원 정보 추가
    } else {
      // 로그인되지 않은 사용자라면 빈 객체 추가
      model.addAttribute("newMember", new Member());  // 빈 객체
    }

    return "product/category"; // 카테고리 상품 페이지
  }

  // 검색 기능
  @GetMapping("/category/search")
  public String searchProducts(
          @RequestParam(defaultValue = "0") int page,  // 페이지 번호 (기본값: 0)
          @RequestParam(defaultValue = "8") int size,  // 페이지 크기 (기본값: 8)
          @RequestParam(defaultValue = "") String productName,  // 검색할 제품명 (기본값: 빈 문자열)
          Model model) {

    // Pageable 객체 생성 (page와 size는 사용자가 전달한 값)
    Pageable pageable = PageRequest.of(page, size);

    // 검색 조건에 맞는 제품 목록을 가져오기 (제품명이 포함된 상품을 조회)
    Page<Product> productsPage = productService.searchProductsByName(productName, pageable);

    // 블럭의 시작번호 설정 (페이징 블록 시작점)
    int blockCount = 10;  // 블록에 표시할 페이지 개수
    int startPage = page - (page % blockCount);  // 시작 페이지 번호 계산

    // 모델에 데이터 추가
    model.addAttribute("products", productsPage.getContent());  // 페이징된 상품 목록
    model.addAttribute("selectedCategory", "search");  // 선택된 카테고리: "search" (검색)
    model.addAttribute("currentPage", page);  // 현재 페이지
    model.addAttribute("totalPages", productsPage.getTotalPages());  // 총 페이지 수
    model.addAttribute("totalItems", productsPage.getTotalElements());  // 총 아이템 수
    model.addAttribute("pageSize", size);  // 페이지 크기
    model.addAttribute("startPage", startPage);  // 블록의 시작 페이지 번호
    model.addAttribute("blockCount", blockCount);  // 블록의 개수
    model.addAttribute("searchQuery", productName);  // 사용자가 입력한 검색어

    // 로그인 여부 체크
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      // 로그인된 사용자라면 사용자 정보를 가져오기
      String userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      Member member = memberService.getMemberById(userId);
      model.addAttribute("member", member);  // 로그인된 회원 정보 추가
    } else {
      // 로그인되지 않은 사용자라면 빈 객체 추가
      model.addAttribute("newMember", new Member());  // 빈 객체
    }

    return "product/category";  // 검색 결과 페이지
  }


}
