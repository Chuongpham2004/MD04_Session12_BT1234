package org.example.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.req.PromotionRequest;
import org.example.productservice.service.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyPromotion(@RequestBody PromotionRequest request) {
        String result = promotionService.applyPromotion(request);
        return ResponseEntity.ok(result);
    }
}