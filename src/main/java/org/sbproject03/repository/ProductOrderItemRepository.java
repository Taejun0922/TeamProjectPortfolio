package org.sbproject03.repository;

import org.sbproject03.domain.ProductOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderItemRepository extends JpaRepository<ProductOrderItem, Long> {

    // 특정 주문에 대한 모든 주문 항목 조회
    List<ProductOrderItem> findAllByOrder_Id(Long orderId);

    // 특정 상품에 대한 모든 주문 항목 조회 (상품의 productId가 String 타입)
    List<ProductOrderItem> findAllByProduct_ProductId(String productId);

    // 특정 주문에 대한 특정 상품의 주문 항목 조회 (상품의 productId가 String 타입)
    ProductOrderItem findByOrder_IdAndProduct_ProductId(Long orderId, String productId);

}
