package com.yunho.shop;

import com.yunho.shop.entity.product.Category;
import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductOption;
import com.yunho.shop.repository.product.CategoryRepository;
import com.yunho.shop.repository.product.ProductOptionRepository;
import com.yunho.shop.repository.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 재고 차감 방식별 동시성 비교.
 * 주의: 이 클래스에 @Transactional을 붙이면 안 된다 (설명 참고).
 */
@SpringBootTest
@ActiveProfiles("test")
class StockConcurrencyTest {

    private static final int REQUEST_COUNT = 100;

    @Autowired private ProductOptionRepository productOptionRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TransactionTemplate transactionTemplate;

    private Category category;
    private Product product;

    @AfterEach
    void cleanUp() {
        if (product != null) productRepository.deleteById(product.getProductId());   // 옵션도 cascade로 삭제
        if (category != null) categoryRepository.deleteById(category.getCategoryId());
    }

    @Test
    @DisplayName("A. 엔티티 + @Version: 잘못 팔리진 않지만, 재고가 있어도 충돌로 실패한다")
    void entityWithOptimisticLock() throws InterruptedException {
        Long optionId = createOption(100);

        Result result = runConcurrently(() ->
                transactionTemplate.executeWithoutResult(status -> {
                    ProductOption option = productOptionRepository.findById(optionId).orElseThrow();
                    option.decreaseStock(1);   // 커밋할 때 where version = ? 로 UPDATE
                })
        );

        int stock = currentStock(optionId);
        result.print("A. 엔티티 + @Version", stock);

        assertThat(result.success() + result.fail()).isEqualTo(REQUEST_COUNT);
        assertThat(stock).isEqualTo(100 - result.success());   // 성공한 만큼만 정확히 줄었는지
    }

    @Test
    @DisplayName("B. 조건부 UPDATE: 재고 100개에 100건 모두 성공한다")
    void conditionalUpdate() throws InterruptedException {
        Long optionId = createOption(100);

        Result result = runConcurrently(() -> decreaseByQuery(optionId));

        int stock = currentStock(optionId);
        result.print("B. 조건부 UPDATE", stock);

        assertThat(result.success()).isEqualTo(100);
        assertThat(stock).isZero();
    }

    @Test
    @DisplayName("B-2. 조건부 UPDATE: 재고 50개에 100건이면 정확히 50건만 성공한다")
    void conditionalUpdateWithShortage() throws InterruptedException {
        Long optionId = createOption(50);

        Result result = runConcurrently(() -> decreaseByQuery(optionId));

        int stock = currentStock(optionId);
        result.print("B-2. 조건부 UPDATE (재고 50)", stock);

        assertThat(result.success()).isEqualTo(50);
        assertThat(result.fail()).isEqualTo(50);
        assertThat(stock).isZero();
    }

    // ===== 도우미 =====

    private void decreaseByQuery(Long optionId) {
        transactionTemplate.executeWithoutResult(status -> {
            int updated = productOptionRepository.decreaseStock(optionId, 1);
            if (updated == 0) {
                throw new IllegalStateException("재고 부족");
            }
        });
    }

    /** 요청 100개를 최대한 같은 순간에 출발시킨다 */
    private Result runConcurrently(Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(REQUEST_COUNT);
        CountDownLatch ready = new CountDownLatch(REQUEST_COUNT);   // 모든 스레드가 준비될 때까지
        CountDownLatch start = new CountDownLatch(1);               // 출발 신호
        CountDownLatch done = new CountDownLatch(REQUEST_COUNT);    // 모두 끝날 때까지

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        for (int i = 0; i < REQUEST_COUNT; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    task.run();
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        long begin = System.nanoTime();
        start.countDown();                      // 출발!
        done.await(30, TimeUnit.SECONDS);
        long elapsedMs = (System.nanoTime() - begin) / 1_000_000;

        executor.shutdown();
        return new Result(success.get(), fail.get(), elapsedMs);
    }

    private Long createOption(int stock) {
        category = categoryRepository.save(Category.builder().name("동시성 테스트").sortOrder(0).build());

        Product p = Product.builder().category(category).name("테스트 셔츠").price(10000).build();
        p.addOption(ProductOption.builder().color("블랙").size("M").extraPrice(0).stock(stock).build());
        product = productRepository.save(p);

        return product.getOptions().getFirst().getProductOptionId();
    }

    private int currentStock(Long optionId) {
        return productOptionRepository.findById(optionId).orElseThrow().getStock();
    }

    private record Result(int success, int fail, long elapsedMs) {
        void print(String title, int stock) {
            System.out.printf("%n[%s] 성공 %d / 실패 %d / 남은 재고 %d / %d ms%n",
                    title, success, fail, stock, elapsedMs);
        }
    }
}