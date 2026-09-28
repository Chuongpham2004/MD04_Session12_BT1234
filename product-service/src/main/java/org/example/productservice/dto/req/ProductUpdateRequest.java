package org.example.productservice.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {
    private String productName;
    private String description;
    private String imageUrl;
    private Double price;
    private Integer stock;
}
