package com.example.WebDeliverySQL.service;

import com.example.WebDeliverySQL.dto.OrderDTO;
import com.example.WebDeliverySQL.mapper.OrderMapper;
import com.example.WebDeliverySQL.model.Order;
import com.example.WebDeliverySQL.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDTO)  // ✅ Використання маппера
                .collect(Collectors.toList());
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toDTO); // ✅ Мапимо в DTO
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {

        if (orderDTO.getDeliveryAddress() == null || orderDTO.getDeliveryAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery address cannot be empty");
        }


        if (!List.of("Pending", "Shipped", "Delivered").contains(orderDTO.getStatus())) {
            throw new IllegalArgumentException("Invalid status");
        }

        Order order = orderMapper.toEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    public OrderDTO updateOrder(Long id, OrderDTO updatedOrderDTO) {
        // Добавляем валидацию статуса
        List<String> allowedStatuses = List.of("Pending", "Shipped", "Delivered");
        if (!allowedStatuses.contains(updatedOrderDTO.getStatus())) {
            throw new IllegalArgumentException("Invalid status");
        }

        return orderRepository.findById(id)
                .map(order -> {
                    order.setDeliveryAddress(updatedOrderDTO.getDeliveryAddress());
                    order.setStatus(updatedOrderDTO.getStatus());
                    order.getCustomer().setId(updatedOrderDTO.getCustomerId());
                    Order updated = orderRepository.save(order);
                    return orderMapper.toDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }


    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
