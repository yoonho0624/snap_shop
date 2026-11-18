package com.yunho.shop.entity.ai;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import com.yunho.shop.entity.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** LLM 속성 태그 (상품당 1행) */
@Entity
@Table(name = "product_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductTag extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_tag_id")
    private Long productTagId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
    /** 오래된 결과로 덮어쓰기 방지 */
    private int imageVersion;
    private String itemType;
    private String color;
    private String pattern;
    private String neckline;
    private String sleeve;
    private String fit;

    /** LLM 원본 응답 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_json")
    private String rawJson;
    private String modelName;
    private String promptVersion;

    @Builder
    public ProductTag(Product product, int imageVersion, String itemType, String color, String pattern,
                      String neckline, String sleeve, String fit, String rawJson,
                      String modelName, String promptVersion) {
        this.product = product;
        this.imageVersion = imageVersion;
        this.itemType = itemType;
        this.color = color;
        this.pattern = pattern;
        this.neckline = neckline;
        this.sleeve = sleeve;
        this.fit = fit;
        this.rawJson = rawJson;
        this.modelName = modelName;
        this.promptVersion = promptVersion;
    }

    /**
     * 새 태깅 결과로 갱신. 더 오래된 imageVersion이면 무시하고 false 반환.
     */
    public boolean updateIfNewer(int imageVersion, String itemType, String color, String pattern,
                                 String neckline, String sleeve, String fit, String rawJson,
                                 String modelName, String promptVersion) {
        if (imageVersion < this.imageVersion) {
            return false;
        }
        this.imageVersion = imageVersion;
        this.itemType = itemType;
        this.color = color;
        this.pattern = pattern;
        this.neckline = neckline;
        this.sleeve = sleeve;
        this.fit = fit;
        this.rawJson = rawJson;
        this.modelName = modelName;
        this.promptVersion = promptVersion;
        return true;
    }
}
