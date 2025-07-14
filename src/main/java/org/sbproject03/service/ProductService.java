package org.sbproject03.service;

import org.sbproject03.domain.Product;
import org.sbproject03.dto.ProductRegister;
import org.sbproject03.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Value("${file.uploadDir}")
  private String baseDir;  // application.yml에서 지정한 경로 자동 주입


  // 특정 카테고리 상품 가져오기
  public List<Product> getProductByCategory(String productCategory) {
    List<Product> products = productRepository.findByProductCategory(productCategory);
    return products;
  }

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  // 특정 상품 가져오기
  public Product getProductById(String productId) {
    return productRepository.findByProductId(productId);
  }

  // 베스트 아이템 조회
  public List<Product> getBestItems() {
    return productRepository.findByIsBestItemTrue();
  }

  // 전체 상품보기
  public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  // 카테고리별 상품 전체 보기 -> 페이징
  public Page<Product> getProductByCategory(String productCategory, Pageable pageable) {
    return productRepository.findByProductCategory(productCategory, pageable);
  }

  // 검색 기능
  public Page<Product> searchProductsByName(String productName, Pageable pageable) {
    // 제품명이 비어 있지 않으면 검색 수행
    if (productName != null && !productName.isEmpty()) {
      return productRepository.findByProductNameContainingIgnoreCase(productName, pageable);
    }
    // 검색어가 비어 있으면 모든 상품을 반환
    return productRepository.findAll(pageable);
  }

  // 카테고리별 최대 번호
  public int findNextProductIdNumber(String prefix) {
    Integer maxNumber = productRepository.findMaxNumberByPrefix(prefix);
    if (maxNumber == null) {
      return 1; // 신규 카테고리일 경우
    }
    return maxNumber + 1;
  }


  // 상품등록코드
  public void registerNewProduct(ProductRegister productRegister) throws IOException {
    // 1. DTO -> Entity 변환
    Product product = new Product();
    product.setProductId(productRegister.getProductId());
    product.setProductName(productRegister.getProductName());
    product.setProductPrice(productRegister.getProductPrice());
    product.setProductStock(productRegister.getProductStock());
    product.setProductCategory(productRegister.getProductCategory().toUpperCase());
    product.setProductDescription(productRegister.getProductDescription());

    // 2. DB에 상품 저장
    productRepository.save(product);

    // 3. 이미지 저장
    MultipartFile mainImage = productRegister.getMainImage();
    MultipartFile detailImage = productRegister.getDetailImage();

    // baseDir 값은 application.yml의 file.uploadDir 설정값
    if (mainImage != null && !mainImage.isEmpty()) {
      String mainImageName = "IMG_" + product.getProductId() + "_main.jpg";
      mainImage.transferTo(new File(baseDir + mainImageName));
    }

    if (detailImage != null && !detailImage.isEmpty()) {
      String detailImageName = "IMG_" + product.getProductId() + "_detail.jpg";
      detailImage.transferTo(new File(baseDir + detailImageName));
    }
  }

  // 상품명 또는 상품 ID로 검색 (페이징 포함)
  public Page<Product> searchByNameOrId(String keyword, Pageable pageable) {
    return productRepository.findByProductNameContainingIgnoreCaseOrProductIdContainingIgnoreCase(keyword, keyword, pageable);
  }

  // 전체 상품 조회 (페이징)
  public Page<Product> findAll(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

}
