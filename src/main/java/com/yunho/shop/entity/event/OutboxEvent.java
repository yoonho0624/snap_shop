package com.yunho.shop.entity.event;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/** Transactional Outbox */
@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseCreatedTimeEntity {

    /** 발행 순서 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxEventId;

    /** UUID, Consumer 멱등성 키 */
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregate_type")
    private AggregateType aggregateType;

    /** productId, Kafka 메시지 key */
    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false)
    private String payload;

    /** null이면 미발행 */
    private LocalDateTime publishedAt;
    /** 발행 시도 횟수 */
    private int attemptCount;

    private OutboxEvent(AggregateType aggregateType, Long aggregateId, EventType eventType,
                        String topic, String payload) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.payload = payload;
        this.attemptCount = 0;
    }

    public static OutboxEvent of(AggregateType aggregateType, Long aggregateId, EventType eventType,
                                 String topic, String payload) {
        return new OutboxEvent(aggregateType, aggregateId, eventType, topic, payload);
    }

    /** 상품 이벤트 편의 메서드 */
    public static OutboxEvent forProduct(Long productId, EventType eventType, String topic, String payload) {
        return new OutboxEvent(AggregateType.PRODUCT, productId, eventType, topic, payload);
    }

    public void increaseAttempt() {
        this.attemptCount++;
    }

    public void markPublished() {
        this.publishedAt = LocalDateTime.now();
    }

    public boolean isPublished() {
        return publishedAt != null;
    }
}
