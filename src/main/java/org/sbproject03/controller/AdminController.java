package org.sbproject03.controller;

import org.sbproject03.domain.Member;
import org.sbproject03.domain.Product;
import org.sbproject03.domain.ProductOrder;
import org.sbproject03.domain.ProductOrderItem;
import org.sbproject03.dto.ProductRegister;
import org.sbproject03.service.MemberService;
import org.sbproject03.service.ProductOrderService;
import org.sbproject03.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final ProductOrderService orderService;

    @Autowired
    public AdminController(MemberService memberService, ProductOrderService orderService) {
        this.memberService = memberService;
        this.orderService = orderService;
    }

    @Autowired
    private ProductService productService;

    // ✅ 회원 리스트 (검색 포함)
    @GetMapping("/members")
    public String getMemberList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").descending());  // 한 페이지 20개
        Page<Member> memberPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            memberPage = memberService.searchByMemberId(keyword, pageable);
        } else {
            memberPage = memberService.findAll(pageable);
        }

        Map<Long, Boolean> orderExistMap = new HashMap<>();
        for (Member member : memberPage.getContent()) {
            List<ProductOrder> orders = orderService.findById(member.getId());
            orderExistMap.put(member.getId(), !orders.isEmpty());
        }

        model.addAttribute("memberList", memberPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("orderExistMap", orderExistMap);
        return "admin/memberList";
    }

    // ✅ 회원 상세 정보 보기
    @GetMapping("/member/view/{id}")
    public String viewMemberDetail(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id);
        List<ProductOrder> orderList = orderService.findById(id); // 주문 내역 조회

        model.addAttribute("member", member);
        model.addAttribute("hasOrders", !orderList.isEmpty()); // 주문 여부만 전달

        return "admin/memberDetail";
    }

    // ✅ 주문 전체 조회 또는 회원 ID로 필터링
    @GetMapping("/orders")
    public String viewAllOrders(
            @RequestParam(value = "memberId", required = false) String memberId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("orderDate").descending());
        Page<ProductOrder> orderPage;

        if (memberId != null && !memberId.trim().isEmpty()) {
            orderPage = orderService.findByMemberId(memberId, pageable);
        } else {
            orderPage = orderService.findAll(pageable);
        }

        Map<Long, Integer> orderTotalMap = new HashMap<>();
        for (ProductOrder order : orderPage.getContent()) {
            int total = order.getItems().stream()
                    .mapToInt(ProductOrderItem::getPrice)
                    .sum();
            orderTotalMap.put(order.getId(), total);
        }

        model.addAttribute("orderList", orderPage); // Page 객체 전달
        model.addAttribute("orderTotalMap", orderTotalMap);
        model.addAttribute("memberId", memberId);
        return "admin/orderList";
    }


    // ✅ 회원 주문 내역 보기
    // ✅ 특정 회원의 주문 내역을 orderList.html로 출력
    @GetMapping("/member/{id}/orders")
    public String viewMemberOrders(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id); // 회원 정보 가져오기
        if (member == null) {
            return "redirect:/admin/members"; // 없는 회원이면 목록으로
        }

        List<ProductOrder> orderList = orderService.findById(id); // 회원의 주문 목록 조회

        // 주문별 총 금액 계산
        Map<Long, Integer> orderTotalMap = new HashMap<>();
        for (ProductOrder order : orderList) {
            int total = order.getItems().stream()
                    .mapToInt(ProductOrderItem::getPrice)
                    .sum();
            orderTotalMap.put(order.getId(), total);
        }

        model.addAttribute("orderList", orderList);
        model.addAttribute("orderTotalMap", orderTotalMap);
        model.addAttribute("memberId", member.getMemberId()); // 검색창에 자동 입력되도록
        return "admin/orderList";
    }

    // ✅ 주문 상세 보기
    @GetMapping("/order/{orderId}")
    public String viewOrderDetail(@PathVariable Long orderId, Model model) {
        ProductOrder order = orderService.findByOrderId(orderId); // 해당 주문 조회
        if (order == null) {
            return "redirect:/admin/orders"; // 주문이 없으면 목록으로
        }

        int totalPrice = order.getItems().stream()
                .mapToInt(ProductOrderItem::getPrice)
                .sum();

        model.addAttribute("order", order);
        model.addAttribute("totalPrice", totalPrice);
        return "admin/orderDetail"; // orderDetail.html로 이동
    }

    // ✅ 상품 등록 페이지로 이동
    @GetMapping("/product/register")
    public String showProductRegisterForm(Model model) {
        ProductRegister productRegister = new ProductRegister();
        model.addAttribute("productRegister", productRegister);  // 🔹 반드시 등록되어야 함
        return "admin/productRegister";
    }

    // ✅ 카테고리별 가장 큰 번호 + 1 반환
    @GetMapping("/products/max-id")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMaxProductId(@RequestParam String category) {
        // 예: "Tent" 카테고리 → "tent_40" → 숫자 추출 후 +1
        String prefix = category.trim().toLowerCase().replaceAll("\\s+", "_"); // tent, table 등
        int nextNumber = productService.findNextProductIdNumber(prefix);

        Map<String, Object> response = new HashMap<>();
        response.put("prefix", prefix);
        response.put("nextNumber", nextNumber); // JS가 productId 조합할 수 있도록

        return ResponseEntity.ok(response);
    }

    // 상품 등록 코드
    @PostMapping("/products/register")
    public String registerProduct(
            @ModelAttribute("productRegister") ProductRegister dto,
            Model model
    ) {
        try {
            String productId = dto.getProductId();
            if (productId == null || productId.isEmpty()) {
                // 예외 처리
                model.addAttribute("error", "상품 ID가 없습니다.");
                return "admin/productRegister";
            }

            // 이미지 저장 경로
            String uploadDir = "D:/upload/images/";

            // 대표이미지 저장
            MultipartFile mainImage = dto.getMainImage();
            MultipartFile detailImage = dto.getDetailImage();

            if (mainImage != null && !mainImage.isEmpty()) {
                String mainFileName = "IMG_" + productId + ".jpg";
                String detailFileName = "IMG_Detail_" + productId + ".jpg";

                File mainFile = new File(uploadDir + mainFileName);
                File detailFile = new File(uploadDir + detailFileName);

                // 두 파일의 이름이 같은지 여부만 미리 저장 (파일 스트림에 접근 X)
                boolean isSameFile = detailImage != null
                        && !detailImage.isEmpty()
                        && mainImage.getOriginalFilename().equals(detailImage.getOriginalFilename());

                // 메모리로 한 번 읽어서 재사용 (주의: 대용량 이미지일 경우 리스크 있음)
                byte[] mainImageBytes = mainImage.getBytes();
                Files.write(mainFile.toPath(), mainImageBytes);

                if (isSameFile) {
                    // 같은 파일이면 복사
                    Files.copy(mainFile.toPath(), detailFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else if (detailImage != null && !detailImage.isEmpty()) {
                    // 다른 파일이면 따로 저장
                    detailImage.transferTo(detailFile);
                }
            }

            // Product 객체로 변환 후 저장
            productService.registerNewProduct(dto);

            return "redirect:/admin/product/register?registerSuccess=true"; // 상품 목록 페이지로 이동
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "상품 등록 중 오류가 발생했습니다.");
            return "redirect:/admin/product/register";
        }
    }

    // ✅ 상품 목록 조회 (검색 + 페이징 포함)
    @GetMapping("/productList")
    public String getProductList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("productId").descending());
        Page<Product> productPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchByNameOrId(keyword, pageable);
        } else {
            productPage = productService.findAll(pageable);
        }

        model.addAttribute("productList", productPage);
        model.addAttribute("keyword", keyword);

        return "admin/productList"; // → templates/admin/productList.html
    }

    // 상품 정보 페이지로 이동
    @GetMapping("/product/view/{id}")
    public String viewProduct(@PathVariable String id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/productDetail";
    }

    // 상품 정보 수정
    @PostMapping("/product/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("mainImage") MultipartFile mainImage,
                                @RequestParam("detailImage") MultipartFile detailImage) throws IOException {
        // 수정된 상품을 반환받아야 변경된 ID를 사용할 수 있음
        Product savedProduct = productService.updateProduct(product, mainImage, detailImage);
        return "redirect:/admin/product/view/" + savedProduct.getProductId();
    }

    // 상품 정보 삭제
    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam String productId) {
        productService.deleteById(productId);
        return "redirect:/admin/productList";
    }
}