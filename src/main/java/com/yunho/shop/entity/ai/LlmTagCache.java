package com.yunho.shop.entity.ai;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 이미지 해시별 태깅 결과 캐시.
 * 키를 직접 지정하는 엔티티라 Persistable로 "새 엔티티"임을 알려
 * save() 때 SELECT 후 merge 하지 않고 바로 INSERT 하게 한다.
 */
@Entity
@Table(name = "llm_tag_cache")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LlmTagCache extends BaseCreatedTimeEntity {

    @EmbeddedId
    private LlmTagCacheId llmTagCacheId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_json", nullable = false)
    private String resultJson;

    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    public LlmTagCache(String imageHash, String promptVersion, String resultJson, String modelName) {
        this.llmTagCacheId = new LlmTagCacheId(imageHash, promptVersion);
        this.resultJson = resultJson;
        this.modelName = modelName;
    }
}
