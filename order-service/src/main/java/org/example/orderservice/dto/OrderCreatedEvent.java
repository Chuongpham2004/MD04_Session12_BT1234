package org.example.orderservice.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent implements Serializable {
    private String orderId;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private String customerEmail;
    private String createdAt;
}
