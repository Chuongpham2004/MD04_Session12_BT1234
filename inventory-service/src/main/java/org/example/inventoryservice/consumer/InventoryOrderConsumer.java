package org.example.inventoryservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.inventoryservice.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryOrderConsumer {

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("\n--------------------------------------------------");
        log.info("[Inventory-Service] Nhận sự kiện đơn hàng: {}", event.getOrderId());
        log.info("-> Thực hiện trừ tồn kho sản phẩm ID: {}, Số lượng: {}", event.getProductId(), event.getQuantity());
        // Gọi Medicine/Product Repository để trừ tồn kho DB & xóa cache Redis ở đây
        log.info("-> [KHO] Trừ kho thành công cho đơn: {}", event.getOrderId());
        log.info("--------------------------------------------------\n");
    }
}