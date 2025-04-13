package temp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

// 주문 클래스 - 엔터티
// 주문 아이디(orderId), 고객(엔터티, Customer), 배송(엔터티, Shipping), 주문아이템(도서, Map<String, orderItem>), 주문 총금액(orderTotalPrice)
@Data
@ToString
@Entity(name = "orders")
//@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "customer_id")
  private Member member;

//  주문 1개에는 여러 개의 주문아이템이 매핑될 수 있음
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "order_item_id")

//  private Map<String, OrderItem> orderItems = new HashMap<>();

  private int orderTotalPrice;

}
