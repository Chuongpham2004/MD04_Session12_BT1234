package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.OrderCreatedEvent;
import org.example.orderservice.service.OrderProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderCreatedEvent request) {
        long startTime = System.currentTimeMillis();

        // 1. Tạo mã đơn hàng & gán thời gian
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        request.setOrderId(orderId);
        request.setCreatedAt(LocalDateTime.now().toString());

        // 2. Giả lập lưu đơn vào DB (trạng thái PENDING)
        System.out.println("--> Đã lưu đơn hàng vào DB: " + orderId);

        // 3. Đẩy sự kiện vào Kafka Topic "order-events"
        orderProducer.sendOrderEvent(request);

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("===> Thời gian xử lý API đặt hàng: " + duration + " ms (Non-blocking)");

        return ResponseEntity.ok("Đặt hàng thành công! Mã đơn: " + orderId);
    }
}