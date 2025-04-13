package org.sbproject03.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Entity
public class Member {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @Column(unique = true, columnDefinition = "varchar(30)")
  private String memberId;

  @NotBlank
  private String memberPassword;

  @NotBlank
  @Column(columnDefinition = "varchar(16)")
  private String memberName;

  @NotBlank
  @Column(columnDefinition = "varchar(13)")
  private String memberPhone;

  @NotBlank
  @Column(columnDefinition = "varchar(50)")
  private String memberEmail;

  @NotBlank
  private String memberAddress;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String cartId;

  // OneToMany 관계 추가
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Cart> carts;  // 한 회원은 여러 카트를 가질 수 있음

  // 회원 생성 메서드
  public static Member createMember(Member member, PasswordEncoder passwordEncoder) {
    Member m = member;
    // 비밀번호 암호화, 사용자 역할 일반으로 설정
    m.setMemberPassword(passwordEncoder.encode(member.getMemberPassword()));
    m.setRole(Role.USER);
    return m;
  }

}
