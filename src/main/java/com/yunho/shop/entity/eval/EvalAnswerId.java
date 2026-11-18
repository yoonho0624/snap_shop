package com.yunho.shop.entity.eval;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** eval_answer 복합키 (eval_query_id, product_id) */
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EvalAnswerId implements Serializable {
    private Long evalQueryId;
    private Long productId;
}
