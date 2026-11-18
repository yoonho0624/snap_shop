package com.yunho.shop.entity.eval;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import com.yunho.shop.entity.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** 평가셋 질문 */
@Entity
@Table(name = "eval_query")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvalQuery extends BaseCreatedTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evalQueryId;

    private String imageUrl;
    private String queryText;
    private String note;

    @OneToMany(mappedBy = "evalQuery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvalAnswer> answers = new ArrayList<>();

    @Builder
    public EvalQuery(String imageUrl, String queryText, String note) {
        this.imageUrl = imageUrl;
        this.queryText = queryText;
        this.note = note;
    }

    public void addAnswer(Product product) {
        answers.add(new EvalAnswer(this, product));
    }
}
