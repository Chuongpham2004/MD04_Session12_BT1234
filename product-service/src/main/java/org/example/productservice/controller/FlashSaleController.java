package org.example.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productservice.service.FlashSaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/flash-sale")
@RequiredArgsConstructor
public class FlashSaleController {
    private final FlashSaleService flashSaleService;

    /**
     * API mua đơn lẻ cho từng khách hàng
     */
    @PostMapping("/buy/{productId}")
    public ResponseEntity<String> buySingle(
            @PathVariable("productId") Long productId,
            @RequestParam(defaultValue = "Khách vãng lai") String user) {
        String result = flashSaleService.buyProduct(productId, user);
        return ResponseEntity.ok(result);
    }

    /**
     * API Giả lập 100 User bấm mua cùng 1 tích tắc
     */
    @PostMapping("/simulate/{productId}")
    public ResponseEntity<List<String>> simulateConcurrency(@PathVariable("productId") Long productId) throws InterruptedException {
        int numberOfUsers = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= numberOfUsers; i++) {
            final String user = "User-" + i;
            executor.submit(() -> {
                try {
                    String res = flashSaleService.buyProduct(productId, user);
                    results.add(res);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Đợi cả 100 luồng chạy xong
        executor.shutdown();

        return ResponseEntity.ok(results);
    }
}
