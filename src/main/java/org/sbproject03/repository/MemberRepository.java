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

    @Transactional
    @Modifying
    @Query(value = "update member set cart_id=:cartId where member_id=:memberId", nativeQuery = true)
    void addCartId(String memberId, String cartId);

    @Transactional
    @Modifying
    @Query(value = "update member set cart_id=null where member_id=:memberId", nativeQuery = true)
    void deleteCartId(String memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);
}
