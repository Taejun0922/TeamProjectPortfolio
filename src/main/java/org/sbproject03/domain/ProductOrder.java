package org.sbproject03.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "items") // 순환 참조 방지
public class ProductOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Enumerated(EnumType.STRING)
  private OrderStatus status = OrderStatus.ORDERED;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime orderDate = LocalDateTime.now();

  private Long cartId;

  // ✅ 연관관계 설정
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductOrderItem> items = new ArrayList<>();

  // ✅ 연관관계 편의 메서드
  public void addOrderItem(ProductOrderItem item) {
    items.add(item);
    item.setOrder(this);
  }
}

