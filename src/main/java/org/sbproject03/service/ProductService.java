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

  // 상품 수정 메서드
  public Product updateProduct(Product updatedProduct, MultipartFile mainImage, MultipartFile detailImage) throws IOException {
    Product existing = productRepository.findByProductId(updatedProduct.getProductId());

    if (existing == null) {
      throw new IllegalArgumentException("해당 상품이 존재하지 않습니다: " + updatedProduct.getProductId());
    }

    // 카테고리 변경 여부 확인
    String oldCategory = existing.getProductCategory();
    String newCategory = updatedProduct.getProductCategory().toUpperCase();

    boolean isCategoryChanged = !oldCategory.equalsIgnoreCase(newCategory);

    if (isCategoryChanged) {
      Integer maxNumber = productRepository.findMaxNumberByPrefix(newCategory);
      int newNumber = (maxNumber != null ? maxNumber : 0) + 1;
      String newProductId = newCategory + "_" + newNumber;

      existing.setProductId(newProductId);
      existing.setProductCategory(newCategory);
    }

    // 필드 업데이트
    existing.setProductName(updatedProduct.getProductName());
    existing.setProductPrice(updatedProduct.getProductPrice());
    existing.setProductStock(updatedProduct.getProductStock());
    existing.setProductDescription(updatedProduct.getProductDescription());
    existing.setBestItem(updatedProduct.isBestItem());

    // 이미지 저장
    String imageId = existing.getProductId(); // 변경된 ID 기준
    String mainImageName = "IMG_" + imageId + ".jpg";
    String detailImageName = "IMG_Detail_" + imageId + ".jpg";

    if (mainImage != null && !mainImage.isEmpty()) {
      File mainImageFile = new File(baseDir, mainImageName);
      mainImage.transferTo(mainImageFile);
    }

    if (detailImage != null && !detailImage.isEmpty()) {
      File detailImageFile = new File(baseDir, detailImageName);
      detailImage.transferTo(detailImageFile);
    }

    return productRepository.save(existing); // 변경된 ID 반영을 위해 리턴
  }

  // 상품 삭제 메서드
  public void deleteById(String productId) {
    Product product = productRepository.findByProductId(productId);
    if (product != null) {
      productRepository.delete(product);

      // 이미지도 삭제하려면 아래 코드 사용
      String mainImagePath = baseDir + "IMG_" + productId + "_main.jpg";
      String detailImagePath = baseDir + "IMG_" + productId + "_detail.jpg";

      new File(mainImagePath).delete();
      new File(detailImagePath).delete();
    }
  }
}
