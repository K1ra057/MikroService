package com.example.WebDeliverySQL;

import com.example.WebDeliverySQL.controller.CustomerController;
import com.example.WebDeliverySQL.controller.OrderController;
import com.example.WebDeliverySQL.dto.CustomerDTO;
import com.example.WebDeliverySQL.dto.OrderDTO;
import com.example.WebDeliverySQL.service.CustomerService;
import com.example.WebDeliverySQL.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/*
@autrhor Дима
@project MikroService-main — копия
@class Mocking
@version 1.0.0
@sinc 17.05.2025 - 23 - 16
*/public class MockingTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CustomerController customerController;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // CustomerController Tests
    @Test
    void testGetAllCustomers() {
        // Arrange
        List<CustomerDTO> mockList = Arrays.asList(
                new CustomerDTO(1L, "John", "john@example.com", List.of()),
                new CustomerDTO(2L, "Alice", "alice@example.com", List.of())
        );
        when(customerService.getAllCustomers()).thenReturn(mockList);

        // Act
        List<CustomerDTO> result = customerController.getAllCustomers();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testCreateCustomer() {
        // Arrange
        CustomerDTO input = new CustomerDTO(null, "Test", "test@example.com", List.of());
        CustomerDTO saved = new CustomerDTO(1L, "Test", "test@example.com", List.of());
        when(customerService.createCustomer(input)).thenReturn(saved);

        // Act
        ResponseEntity<CustomerDTO> response = customerController.createCustomer(input);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Test", response.getBody().getName());
    }

    @Test
    void testGetCustomerById_Found() {
        // Arrange
        CustomerDTO mockCustomer = new CustomerDTO(1L, "John", "john@example.com", List.of());
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(mockCustomer));

        // Act
        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetCustomerById_NotFound() {
        // Arrange
        when(customerService.getCustomerById(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(99L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        CustomerDTO updateData = new CustomerDTO(2L, "Updated", "updated@example.com", List.of());
        when(customerService.updateCustomer(2L, updateData)).thenReturn(updateData);

        // Act
        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(2L, updateData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", response.getBody().getName());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        CustomerDTO updateData = new CustomerDTO(99L, "NotFound", "notfound@example.com", List.of());
        when(customerService.updateCustomer(99L, updateData)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(99L, updateData);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteCustomer() {
        // Act
        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService, times(1)).deleteCustomer(1L);
    }

    // OrderController Tests
    @Test
    void testGetAllOrders() {
        // Arrange
        List<OrderDTO> orders = Arrays.asList(
                new OrderDTO(1L, "Address 1", "Pending", 1L),
                new OrderDTO(2L, "Address 2", "Completed", 2L)
        );
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act
        List<OrderDTO> result = orderController.getAllOrders();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testCreateOrder() {
        // Arrange
        OrderDTO input = new OrderDTO(null, "New Address", "Created", 1L);
        OrderDTO saved = new OrderDTO(1L, "New Address", "Created", 1L);
        when(orderService.createOrder(input)).thenReturn(saved);

        // Act
        OrderDTO result = orderController.createOrder(input);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Created", result.getStatus());
    }

    @Test
    void testGetOrderById_Found() {
        // Arrange
        OrderDTO order = new OrderDTO(10L, "Address", "Processing", 1L);
        when(orderService.getOrderById(10L)).thenReturn(Optional.of(order));

        // Act
        ResponseEntity<OrderDTO> response = orderController.getOrderById(10L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    void testUpdateOrder_Success() {
        // Arrange
        OrderDTO updateData = new OrderDTO(3L, "Updated Address", "Delivered", 2L);
        when(orderService.updateOrder(3L, updateData)).thenReturn(updateData);

        // Act
        ResponseEntity<OrderDTO> response = orderController.updateOrder(3L, updateData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivered", response.getBody().getStatus());
    }

    @Test
    void testUpdateOrder_NotFound() {
        // Arrange
        OrderDTO updateData = new OrderDTO(99L, "Invalid", "NotFound", 99L);
        when(orderService.updateOrder(99L, updateData)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<OrderDTO> response = orderController.updateOrder(99L, updateData);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteOrder() {
        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(5L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(5L);
    }

}
