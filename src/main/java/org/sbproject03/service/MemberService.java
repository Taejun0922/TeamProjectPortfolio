package org.sbproject03.service;

import lombok.RequiredArgsConstructor;
import org.sbproject03.domain.Member;
import org.sbproject03.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;

  // 회원 추가
  public void register(Member member) {
    validateDuplicateMember(member);
    memberRepository.save(member);
  }

  // 회원 ID 중복 체크
  private void validateDuplicateMember(Member member) {
    memberRepository.findByMemberId(member.getMemberId())
      .ifPresent(existingMember -> {
        throw new IllegalStateException("이미 가입한 회원입니다.");
      });
  }

  // 회원 정보 획득(1건)
  public Member getMemberById(String memberId) {
    System.out.println("🔍 DB에서 회원 정보 조회: " + memberId);

    return memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다: " + memberId));
  }

  // 인증시에 회원 정보를 획득
  @Override
  public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
    System.out.println("로그인 요청: " + memberId);

    Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

    System.out.println("로그인 성공: " + member.getMemberId());

    return User.builder()
            .username(member.getMemberId())  // 여기서 memberId가 정상적으로 들어가는지 확인!
            .password(member.getMemberPassword())
            .roles(member.getRole().toString())
            .build();
  }

  // 회원 정보 수정 메서드 추가
  public void updateMember(Member member) {
    System.out.println("회원 정보 업데이트: " + member.getMemberId());

    // 기존 회원 정보 확인
    Member existingMember = memberRepository.findByMemberId(member.getMemberId())
            .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다: " + member.getMemberId()));

    // 정보 업데이트
    existingMember.setMemberName(member.getMemberName());
    existingMember.setMemberEmail(member.getMemberEmail());
    existingMember.setMemberPhone(member.getMemberPhone());
    existingMember.setMemberAddress(member.getMemberAddress());

    // 비밀번호가 변경된 경우만 업데이트
    if (member.getMemberPassword() != null && !member.getMemberPassword().isEmpty()) {
      existingMember.setMemberPassword(member.getMemberPassword()); // 암호화된 비밀번호 사용
    }

    // 저장 (JPA의 save() 메서드는 기존 데이터가 있으면 업데이트를 수행)
    memberRepository.save(existingMember);
  }

  @Transactional
  public void deleteMember(String memberId) {
    System.out.println("회원 삭제 요청: " + memberId);
    memberRepository.deleteByMemberId(memberId);

    // 삭제 후 확인
    boolean exists = memberRepository.findByMemberId(memberId).isPresent();
    if (exists) {
      throw new IllegalStateException("회원 삭제 실패: " + memberId);
    }
    System.out.println("회원 삭제 성공: " + memberId);
  }

  public void addCartId(String memberId, String cartId) {
    memberRepository.addCartId(memberId, cartId);
  }

  public void deleteCartId(String memberId) {
    memberRepository.deleteCartId(memberId);
  }
}
