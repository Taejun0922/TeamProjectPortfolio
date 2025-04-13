package org.sbproject03.service;

import org.sbproject03.domain.Product;
import org.sbproject03.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

  @Autowired
  private ProductRepository productRepository;


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


}
