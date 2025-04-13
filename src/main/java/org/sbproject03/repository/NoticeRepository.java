package org.sbproject03.repository;


import org.sbproject03.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 최신 순으로 4개만 보기
    List<Notice> findTop4ByOrderByCreatedDateDesc();
}
