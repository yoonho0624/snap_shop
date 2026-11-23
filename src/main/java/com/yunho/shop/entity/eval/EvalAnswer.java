package com.yunho.shop.entity.eval;

import com.yunho.shop.entity.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 평가셋 정답 (질문 : 상품 = N : M 연결 테이블) */
@Entity
@Table(name = "eval_answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvalAnswer {

    @EmbeddedId
    private EvalAnswerId evalAnswerId;

    @MapsId("evalQueryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eval_query_id")
    private EvalQuery evalQuery;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    /** EvalQuery.addAnswer()로 생성 (EvalQuery를 저장하면 cascade로 함께 INSERT) */
    EvalAnswer(EvalQuery evalQuery, Product product) {
        this.evalQuery = evalQuery;
        this.product = product;
        this.evalAnswerId = new EvalAnswerId(evalQuery.getEvalQueryId(), product.getProductId());
    }
}
