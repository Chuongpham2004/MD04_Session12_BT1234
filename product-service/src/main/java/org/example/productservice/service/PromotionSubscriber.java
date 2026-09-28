package org.example.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.dto.res.PromotionMessage;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionSubscriber implements MessageListener {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            PromotionMessage promo = objectMapper.readValue(json, PromotionMessage.class);

            log.info("\n=======================================================");
            log.info("--> [Product-Service] Nhận sự kiện khuyến mãi từ Redis Channel: promotion-updates");
            log.info("--> Sản phẩm ID: {}", promo.getProductId());
            log.info("--> Nội dung: {}", promo.getDescription());

            // Thực hiện xóa cache sản phẩm để lần đọc sau buộc phải lấy dữ liệu mới
            Cache productCache = cacheManager.getCache("products");
            if (productCache != null) {
                productCache.evict(promo.getProductId());
                log.info("--> [CACHE EVICTED] Đã xóa cache key 'products::{}'", promo.getProductId());
            }
            log.info("=======================================================\n");

        } catch (Exception e) {
            log.error("Lỗi khi xử lý thông báo khuyến mãi: {}", e.getMessage(), e);
        }
    }
}