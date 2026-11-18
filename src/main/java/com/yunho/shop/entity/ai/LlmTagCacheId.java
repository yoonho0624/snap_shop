package com.yunho.shop.entity.ai;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** llm_tag_cache 복합키 (image_hash, prompt_version) */
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LlmTagCacheId implements Serializable {
    private String imageHash;
    private String promptVersion;
}
