package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.dto.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private static final String TOPIC = "order-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderEvent(OrderCreatedEvent event) {
        log.info("==> [Order-Service] Bắn sự kiện đặt hàng vào topic '{}': Mã đơn {}", TOPIC, event.getOrderId());
        // Gửi bất đồng bộ vào topic
        kafkaTemplate.send(TOPIC, event.getOrderId(), event);
    }
}