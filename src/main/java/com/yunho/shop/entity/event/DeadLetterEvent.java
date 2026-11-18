package com.yunho.shop.entity.event;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/** DLT 기록과 관리자 재처리 (FK 없음) */
@Entity
@Table(name = "dead_letter_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeadLetterEvent extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deadLetterEventId;

    private String eventId;
    /** 원본 토픽 */
    private String topic;
    /** 실패한 Consumer */
    private String consumerGroup;

    /** 재처리에 쓸 원본 이벤트 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false)
    private String payload;

    /** 예외 클래스명 */
    private String errorType;
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeadLetterStatus status;

    private LocalDateTime resolvedAt;
    /** 처리한 관리자 member_id (FK 없음) */
    private Long resolvedBy;

    @Builder
    public DeadLetterEvent(String eventId, String topic, String consumerGroup, String payload,
                           String errorType, String errorMessage) {
        this.eventId = eventId;
        this.topic = topic;
        this.consumerGroup = consumerGroup;
        this.payload = payload;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.status = DeadLetterStatus.PENDING;
    }

    /** 관리자가 원본 토픽으로 재발행 */
    public void redrive(Long adminId) {
        resolve(DeadLetterStatus.REDRIVEN, adminId);
    }

    /** 관리자가 무시 처리 */
    public void ignore(Long adminId) {
        resolve(DeadLetterStatus.IGNORED, adminId);
    }

    private void resolve(DeadLetterStatus next, Long adminId) {
        if (status != DeadLetterStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 이벤트입니다. (현재 상태: " + status + ")");
        }
        this.status = next;
        this.resolvedBy = adminId;
        this.resolvedAt = LocalDateTime.now();
    }
}
