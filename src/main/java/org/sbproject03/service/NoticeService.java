package org.sbproject03.service;

import org.sbproject03.domain.Notice;
import org.sbproject03.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    // Notice 목록 읽기(전체) -> 페이징 처리
    public Page<Notice> getNoticeList(int pageNum, String sortField, String sortDir) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
        return noticeRepository.findAll(pageable);
    }

    // 글 저장
    public void save(Notice notice) {
       noticeRepository.save(notice.toEntity());
    }

    // newNotice 1건 읽기
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id).get();
    }

    // 게시글 삭제
    public void deleteNoticeById(Long id) {
        noticeRepository.deleteById(id);
    }

    // 최신순으로 4개만 보기
    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop4ByOrderByCreatedDateDesc();
    }

}
