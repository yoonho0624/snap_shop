package com.yunho.shop.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 이 클래스는 설정 파일이라고 Spring에 알림
@Configuration
/** @CreatedDate, @LastModifiedDate가 동작하게 함 */
@EnableJpaAuditing
public class JpaConfig {
}
