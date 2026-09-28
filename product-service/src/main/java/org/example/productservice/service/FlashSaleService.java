package org.example.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleService {

    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;

    public String buyProduct(Long productId, String user) {
        String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Chờ lấy khóa tối đa 3 giây, tự động giải phóng khóa sau 5 giây nếu gặp sự cố
            boolean isLocked = lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (!isLocked) {
                return user + ": Hệ thống đang có quá nhiều lượt mua, vui lòng thử lại sau!";
            }

            try {
                // 1. Kiểm tra tồn kho từ Database
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

                if (product.getStock() <= 0) {
                    return user + ": Sản phẩm đã hết hàng";
                }

                // 2. Giả lập độ trễ xử lý đơn hàng & thanh toán (30ms - 50ms)
                Thread.sleep(50);

                // 3. Trừ tồn kho và lưu vào DB
                product.setStock(product.getStock() - 1);
                productRepository.save(product);

                log.info("==> {} mua hàng THÀNH CÔNG! Tồn kho còn lại: {}", user, product.getStock());
                return user + ": Đặt mua thành công! Tồn kho còn: " + product.getStock();

            } finally {
                // Đảm bảo chỉ thread đang nắm giữ lock mới được quyền unlock
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return user + ": Giao dịch bị gián đoạn!";
        }
    }
}