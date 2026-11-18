package com.yunho.shop.entity.product;

import com.yunho.shop.entity.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long productImageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 저장소(S3 등) 경로 */
    private String imageUrl;
    /** SHA-256, 중복 태깅 방지 */
    private String imageHash;
    /** AI 처리 대상인 대표 이미지 */
    private boolean isMain;
    /** 대표 이미지 변경 시 +1, 이벤트의 imageVersion */
    private int imageVersion;
    private int sortOrder;

    @Builder
    public ProductImage(String imageUrl, String imageHash, boolean isMain, int sortOrder) {
        this.imageUrl = imageUrl;
        this.imageHash = imageHash;
        this.isMain = isMain;
        this.imageVersion = 1;
        this.sortOrder = sortOrder;
    }

    /** Product.addImage()에서만 호출 */
    void assignProduct(Product product) {
        this.product = product;
    }

    /** 대표 이미지 교체: 새 파일로 바꾸고 버전을 올린다 */
    public void replaceMainImage(String imageUrl, String imageHash) {
        this.imageUrl = imageUrl;
        this.imageHash = imageHash;
        this.imageVersion++;
    }

    public void markMain() {
        this.isMain = true;
    }

    public void unmarkMain() {
        this.isMain = false;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
