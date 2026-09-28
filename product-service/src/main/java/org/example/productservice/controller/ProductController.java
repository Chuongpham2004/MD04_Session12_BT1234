package org.example.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.req.ProductUpdateRequest;
import org.example.productservice.entity.Product;
import org.example.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductDetail(@PathVariable("id") Long id) {
        long startTime = System.currentTimeMillis();
        Product product = productService.getProductById(id);
        long responseTime = System.currentTimeMillis() - startTime;

        System.out.println("===> Thời gian phản hồi API ID " + id + ": " + responseTime + " ms");
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody ProductUpdateRequest request) {
        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }
}