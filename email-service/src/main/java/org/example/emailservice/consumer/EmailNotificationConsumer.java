package org.example.emailservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.emailservice.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationConsumer {

    @KafkaListener(topics = "order-events", groupId = "email-group")
    public void handleOrderNotification(OrderCreatedEvent event) {
        log.info("\n==================================================");
        log.info("[Email-Service] Gửi email xác nhận đơn hàng");
        log.info("-> Tới email: {}", event.getCustomerEmail());
        log.info("-> Nội dung: Cảm ơn bạn đã mua đơn hàng {} với tổng tiền {} VND",
                event.getOrderId(), event.getTotalPrice());
        log.info("-> Trạng thái: Đã gửi email thành công!");
        log.info("==================================================\n");
    }
}