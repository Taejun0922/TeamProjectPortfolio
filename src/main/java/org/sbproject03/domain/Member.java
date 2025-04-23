package org.sbproject03.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "carts")
public class Member {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @Column(unique = true, length = 30)
  private String memberId;

  @NotBlank
  private String memberPassword;

  @NotBlank @Column(length = 16)
  private String memberName;

  @NotBlank @Column(length = 13)
  private String memberPhone;

  @NotBlank @Column(length = 50)
  private String memberEmail;

  @NotBlank
  private String memberAddress;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Cart> carts;

  public static Member createMember(Member member, PasswordEncoder passwordEncoder) {
    Member m = new Member();
    m.setMemberId(member.getMemberId());
    m.setMemberPassword(passwordEncoder.encode(member.getMemberPassword()));
    m.setMemberName(member.getMemberName());
    m.setMemberPhone(member.getMemberPhone());
    m.setMemberEmail(member.getMemberEmail());
    m.setMemberAddress(member.getMemberAddress());
    return m;
  }
}

