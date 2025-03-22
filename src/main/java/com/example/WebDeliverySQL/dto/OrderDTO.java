package com.example.WebDeliverySQL.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO {
    private Long id;

    @NotBlank(message = "Delivery address cannot be empty")
    private String deliveryAddress;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    private Long customerId;  // Передаємо ID клієнта замість об'єкта
}
