package org.sbproject03.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(exclude = "member")
@Entity
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto-increment 방식으로 생성
  @Column(name = "cart_id")
  private Long cartId;  // cartId를 Long 타입으로 변경

  private int totalPrice = 0;

  @ManyToOne(fetch = FetchType.LAZY) // Cart 입장에선 여러 개가 하나의 Member에 속함
  @JoinColumn(name = "member_id") // DB에서 member_id라는 FK 컬럼으로 연결
  private Member member;

  // Cart 생성자
  public Cart(Member member) {
    this.member = member;  // Member를 받아서 Cart에 연결
  }
}
