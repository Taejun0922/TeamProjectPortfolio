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
@ToString(exclude = "carts")
@Entity
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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

  // ✅ cartId 필드 삭제 완료!

  // 회원과 Cart는 1:N 관계 (회원 1명 = 여러 카트 가능)
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Cart> carts;

  // 회원 생성 메서드
  public static Member createMember(Member member, PasswordEncoder passwordEncoder) {
    Member m = member;
    m.setMemberPassword(passwordEncoder.encode(member.getMemberPassword()));
    m.setRole(Role.USER);
    return m;
  }
}
