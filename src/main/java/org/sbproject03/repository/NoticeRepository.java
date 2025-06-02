package org.sbproject03.repository;


import org.sbproject03.domain.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 최신 순으로 4개만 보기
    List<Notice> findTop4ByOrderByCreatedDateDesc();

    @Query("SELECT n FROM Notice n WHERE " +
            "(:type = 'writerId' AND n.writerId LIKE %:keyword%) OR " +
            "(:type = 'noticeSubject' AND n.noticeSubject LIKE %:keyword%) OR " +
            "(:type = 'noticeContent' AND n.noticeContent LIKE %:keyword%)")
    Page<Notice> searchByType(@Param("type") String type, @Param("keyword") String keyword, Pageable pageable);

}
