package com.yunho.shop.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand")
@Getter
// access = AccessLevel.PROTECTED : 기본 생성자를 JPA만 쓰게 하고, 외부에선 빈 객체 못 만들게 막음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;

    private String name;

    public Brand(String name) {
        this.name = name;
    }

    public void rename(String name) {
        this.name = name;
    }
}
