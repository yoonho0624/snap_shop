package com.yunho.shop.service.order;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// 외부에 보여 줄 주문번호를 만든다.
@Component
public class OrderNoGenerator {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 헷갈리는 0, O,  1, I, L을 뺀 31글자
    private static final char[] ALPHABET = "23456789ABCDEFGHJKMNPOQRSTUVWXYZ".toCharArray();
    private static final int RANDOM_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder(8 + 1 + RANDOM_LENGTH);
        sb.append(LocalDate.now(KST).format(DATE_FORMAT)).append('-');
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            sb.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }
}
