package org.sbproject03.repository;

import org.sbproject03.domain.Member;
import org.sbproject03.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);

    // Role이 USER인 멤버 전체 조회
    List<Member> findByRole(Role role);

    // Role이 USER이고, 아이디에 키워드가 포함된 멤버 검색
    List<Member> findByMemberIdContainingAndRole(String memberId, Role role);

    // 페이징처리
    Page<Member> findByMemberIdContaining(String keyword, Pageable pageable);
    Page<Member> findByRole(Role role, Pageable pageable);
    Page<Member> findByMemberIdContainingAndRole(String memberId, Role role, Pageable pageable);
}
