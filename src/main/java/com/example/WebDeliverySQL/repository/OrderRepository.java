package com.example.WebDeliverySQL.repository;

import com.example.WebDeliverySQL.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // ✅ Правильна анотація
public interface OrderRepository extends JpaRepository<Order, Long> {
}
