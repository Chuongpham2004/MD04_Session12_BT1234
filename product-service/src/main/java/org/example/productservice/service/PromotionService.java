package org.example.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.dto.res.PromotionMessage;
import org.example.productservice.dto.req.PromotionRequest;
import org.example.productservice.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public String applyPromotion(PromotionRequest request) {
        // 1. Cập nhật giá mới vào DB
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        product.setPrice(request.getDiscountPrice());
        productRepository.save(product);
        log.info("==> [DB UPDATE] Đã cập nhật giá mới cho sản phẩm ID {}: {} VND", product.getId(), product.getPrice());

        // 2. Publish tin nhắn vào Redis Channel "promotion-updates"
        try {
            PromotionMessage message = new PromotionMessage(
                    product.getId(),
                    request.getDiscountPrice(),
                    request.getDescription()
            );
            String jsonPayload = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend("promotion-updates", jsonPayload);
            log.info("==> [PUB/SUB] Đã gửi thông điệp khuyến mãi vào channel: promotion-updates");
        } catch (Exception e) {
            log.error("Lỗi khi publish promotion event: {}", e.getMessage());
        }

        return "Áp dụng khuyến mãi thành công và đã thông báo tới toàn hệ thống!";
    }
}