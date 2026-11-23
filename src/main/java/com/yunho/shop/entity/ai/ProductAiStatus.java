package com.yunho.shop.entity.ai;

import com.yunho.shop.entity.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/** 상품별 AI 처리 상태 (PK = product_id, product와 1:1) */
@Entity
@Table(name = "product_ai_status")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAiStatus {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    /** 지금 처리 중인 이미지 버전 */
    private int imageVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "embedding_status")
    private AiTaskStatus embeddingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "tagging_status")
    private AiTaskStatus taggingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AiStatus status;

    /** 벡터를 만든 모델 버전 */
    private String embeddingModel;
    private String failReason;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ProductAiStatus(Product product, int imageVersion) {
        this.product = product;
        applyReset(imageVersion);
    }

    /** 대표 이미지가 바뀌면 새 버전으로 처음부터 다시 */
    public void reset(int imageVersion) {
        applyReset(imageVersion);
    }

    private void applyReset(int imageVersion) {
        this.imageVersion = imageVersion;
        this.embeddingStatus = AiTaskStatus.PENDING;
        this.taggingStatus = AiTaskStatus.PENDING;
        this.status = AiStatus.PENDING;
        this.failReason = null;
    }

    /** 오래된 버전의 결과는 무시 */
    public boolean isStale(int imageVersion) {
        return imageVersion < this.imageVersion;
    }

    public void completeEmbedding(String embeddingModel) {
        this.embeddingStatus = AiTaskStatus.DONE;
        this.embeddingModel = embeddingModel;
    }

    public void completeTagging() {
        this.taggingStatus = AiTaskStatus.DONE;
    }

    public void failEmbedding(String reason) {
        this.embeddingStatus = AiTaskStatus.FAILED;
        fail(reason);
    }

    public void failTagging(String reason) {
        this.taggingStatus = AiTaskStatus.FAILED;
        fail(reason);
    }

    public boolean isAllDone() {
        return embeddingStatus == AiTaskStatus.DONE && taggingStatus == AiTaskStatus.DONE;
    }

    /**
     * 참고용. 동시에 두 Consumer가 끝날 수 있으므로 실제 READY 전환은
     * Repository의 조건부 UPDATE(영향받은 행 1이면 product.ready 발행)로 처리하는 것을 권장.
     */
    public void markReady() {
        if (!isAllDone()) {
            throw new IllegalStateException("임베딩과 태깅이 모두 끝나야 READY가 됩니다.");
        }
        this.status = AiStatus.READY;
    }

    private void fail(String reason) {
        this.status = AiStatus.FAILED;
        this.failReason = (reason != null && reason.length() > 500) ? reason.substring(0, 500) : reason;
    }
}
