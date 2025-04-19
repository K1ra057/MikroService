package com.example.WebDeliverySQL.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer") // Уникнення рекурсії в toString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryAddress;
    private String status;  // "Pending", "Shipped", "Delivered"

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne  // Кожне замовлення належить одному клієнту
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
