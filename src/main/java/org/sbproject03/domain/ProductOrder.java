package org.sbproject03.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Entity
public class ProductOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 주문자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  // 주문한 상품
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  // 주문 수량
  private int quantity;

  // 총 주문 금액
  private int totalPrice;

  // 주문 상태 (OrderStatus Enum으로 변경)
  @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
  private OrderStatus status = OrderStatus.ORDERED;  // 기본 상태는 ORDERED

  // 주문 시간
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime orderDate = LocalDateTime.now();

  // 카트 ID (String 타입으로 추가)
  private String cartId;
}
