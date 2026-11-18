package com.yunho.shop.entity.search;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

/** 검색 로그 (로그 테이블이라 FK 없이 id만 저장) */
@Entity
@Table(name = "search_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLog extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_log_id")
    private Long searchLogId;

    /** 비회원은 null */
    private Long memberId;
    /** 원본 이미지는 저장하지 않음 */
    private String queryImageHash;
    private String queryText;

    /** LLM이 바꾼 필터 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_filter")
    private String parsedFilter;

    /** 상위 20개, 순서대로 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_product_ids", nullable = false)
    private List<Long> resultProductIds = new ArrayList<>();

    private int latencyMs;
    /** 텍스트 검색으로 전환 여부 */
    private boolean fallbackUsed;

    @Builder
    public SearchLog(Long memberId, String queryImageHash, String queryText, String parsedFilter,
                     List<Long> resultProductIds, int latencyMs, boolean fallbackUsed) {
        this.memberId = memberId;
        this.queryImageHash = queryImageHash;
        this.queryText = queryText;
        this.parsedFilter = parsedFilter;
        this.resultProductIds = (resultProductIds != null) ? new ArrayList<>(resultProductIds) : new ArrayList<>();
        this.latencyMs = latencyMs;
        this.fallbackUsed = fallbackUsed;
    }

    /** 클릭한 상품의 순위(1부터). 결과에 없으면 -1 */
    public int rankOf(Long productId) {
        int index = resultProductIds.indexOf(productId);
        return (index < 0) ? -1 : index + 1;
    }
}
