package com.yunho.shop.entity.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Consumer 멱등성.
 * Persistable로 항상 INSERT 되게 해서, 이미 처리한 이벤트면
 * 중복 키 예외(DataIntegrityViolationException)가 나도록 한다.
 * (Persistable이 없으면 save()가 merge로 동작해 중복을 조용히 덮어써 버림)
 */
@Entity
@Table(name = "processed_event")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent {

    @EmbeddedId
    private ProcessedEventId processedEventId;

    @CreatedDate
    @Column(name = "processed_at", updatable = false)
    private LocalDateTime processedAt;

    public ProcessedEvent(String eventId, String consumerGroup) {
        this.processedEventId = new ProcessedEventId(eventId, consumerGroup);
    }
}
