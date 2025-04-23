package org.sbproject03.domain;



import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 공지사항 클래스
// 아이디, 작성자 아이디, 제목, 내용, 작성일, 수정일
@Entity
@Data
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Notice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String writerId;

    @Column(length = 200, nullable = false)
    private String noticeSubject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String noticeContent;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public Notice(Long id, String writerId, String noticeSubject, String noticeContent) {
        this.id = id;
        this.writerId = writerId;
        this.noticeSubject = noticeSubject;
        this.noticeContent = noticeContent;
    }
}

