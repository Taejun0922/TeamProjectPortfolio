package org.sbproject03.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductRegister {

    private String productId;     // 상품 ID (예: tent_41)
    private String productName;          // 상품 이름
    private int productPrice;            // 가격
    private int productStock;            // 재고 수량
    private String productCategory;      // 카테고리 (예: 텐트, 침낭 등)
    private String productDescription;   // 상품 설명 (상세보기 영역)
    private MultipartFile mainImage;     // 대표 이미지
    private MultipartFile detailImage;   // 상세 이미지

    public ProductRegister() {
        this.productCategory = "Tent"; // ✅ 기본 카테고리 설정
    }
}
