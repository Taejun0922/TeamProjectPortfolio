package org.sbproject03.repository;

import org.sbproject03.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);
}
