package com.yunho.shop.entity.eval;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 평가 실행 결과 */
@Entity
@Table(name = "eval_run")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvalRun extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evalRunId;

    /** 바꾼 내용 (예: 크롭 전처리 추가) */
    private String description;
    private String embeddingModel;
    private String promptVersion;
    private int queryCount;
    private BigDecimal recallAt10;
    private Integer p95Ms;

    @Builder
    public EvalRun(String description, String embeddingModel, String promptVersion,
                   int queryCount, BigDecimal recallAt10, Integer p95Ms) {
        this.description = description;
        this.embeddingModel = embeddingModel;
        this.promptVersion = promptVersion;
        this.queryCount = queryCount;
        this.recallAt10 = recallAt10;
        this.p95Ms = p95Ms;
    }
}
