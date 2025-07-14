package org.sbproject03.repository;

import org.sbproject03.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Product findByProductId(String productId);
    List<Product> findByProductCategory(String productCategory);
    List<Product> findByIsBestItemTrue();
    // category별로 조회 -> 가격에 대한 오름차순
    Page<Product> findByProductCategory(String productCategory, Pageable pageable);
    // 검색 기능
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    // 카테고리별 최대 번호
    @Query(value = "SELECT MAX(CAST(SUBSTRING_INDEX(product_id, '_', -1) AS UNSIGNED)) FROM product WHERE product_id LIKE CONCAT(:prefix, '_%')", nativeQuery = true)
    Integer findMaxNumberByPrefix(@Param("prefix") String prefix);

    // 🔍 상품명 또는 상품 ID로 검색 (대소문자 구분 없이, 페이징)
    Page<Product> findByProductNameContainingIgnoreCaseOrProductIdContainingIgnoreCase(
            String productName,
            String productId,
            Pageable pageable);

}
