package com.yunho.shop.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    /** 최상위 카테고리는 null */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // mappedBy = "parent" : FK는 parent 필드가 관리한다
    @OneToMany(mappedBy = "parent")
    @OrderBy("sortOrder ASC")
    private List<Category> children = new ArrayList<>();

    private String name;
    private Integer depth;
    private int sortOrder;

    @Builder
    public Category(Category parent, String name, int sortOrder) {
        this.parent = parent;
        this.name = name;
        this.depth = (parent == null) ? 1 : parent.getDepth() + 1;
        this.sortOrder = sortOrder;
    }

    public void update(String name, int sortOrder) {
        this.name = name;
        this.sortOrder = sortOrder;
    }
}
