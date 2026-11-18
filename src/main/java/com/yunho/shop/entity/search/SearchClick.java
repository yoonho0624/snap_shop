package com.yunho.shop.entity.search;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/** 검색 결과 클릭 (FK 없이 id만 저장) */
@Entity
@Table(name = "search_click")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_click_id")
    private Long searchClickId;

    private Long searchLogId;
    private Long productId;
    /** 결과 목록에서의 순위(1부터) */
    private int rankPosition;

    @CreatedDate
    @Column(name = "clicked_at", updatable = false)
    private LocalDateTime clickedAt;

    @Builder
    public SearchClick(Long searchLogId, Long productId, int rankPosition) {
        this.searchLogId = searchLogId;
        this.productId = productId;
        this.rankPosition = rankPosition;
    }
}
