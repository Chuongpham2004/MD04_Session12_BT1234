package org.example.productservice.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionRequest {
    private Long productId;
    private Double discountPrice;
    private String description;
}