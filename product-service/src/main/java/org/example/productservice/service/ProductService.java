package org.example.productservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.req.ProductUpdateRequest;
import org.example.productservice.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Cache-aside: Lưu cache theo key products::{id}
     * Lần đầu: gọi DB (~200ms)
     * Lần sau: lấy từ Redis (<10ms)
     */
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        System.out.println("--> [DATABASE QUERY] Đọc thông tin sản phẩm từ Database cho ID: " + id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));
    }

    /**
     * Tự động xóa cache khi sản phẩm được cập nhật
     */
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));

        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());

        Product saved = productRepository.save(product);
        System.out.println("--> [CACHE EVICT] Đã cập nhật DB và XÓA key products::" + id + " trong Redis!");
        return saved;
    }
}