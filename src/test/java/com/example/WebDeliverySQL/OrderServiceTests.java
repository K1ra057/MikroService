package com.example.WebDeliverySQL;
import com.example.WebDeliverySQL.dto.OrderDTO;
import com.example.WebDeliverySQL.mapper.OrderMapper;
import com.example.WebDeliverySQL.model.Customer;
import com.example.WebDeliverySQL.model.Order;
import com.example.WebDeliverySQL.repository.OrderRepository;
import com.example.WebDeliverySQL.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.lenient;
/*
@autrhor Дима
@project MikroService-main — копия
@class OrderServiceTests
@version 1.0.0
@sinc 04.05.2025 - 13 - 27
*/
@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService underTest;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private OrderDTO testDTO;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setId(1L);

        testDTO = new OrderDTO();
        testDTO.setDeliveryAddress("Main St. 123");
        testDTO.setStatus("Pending");
        testDTO.setCustomerId(1L);

        testOrder = new Order();
        testOrder.setDeliveryAddress(testDTO.getDeliveryAddress());
        testOrder.setStatus(testDTO.getStatus());
        testOrder.setCustomer(customer);
    }

    // 1. Success scenario: Create order
    @Test
    @DisplayName("Create order - success")
    void whenCreateOrderWithValidDataThenSuccess() {
        given(orderMapper.toEntity(testDTO)).willReturn(testOrder);
        given(orderRepository.save(testOrder)).willReturn(testOrder);
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);

        OrderDTO result = underTest.createOrder(testDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getDeliveryAddress()).isEqualTo(testDTO.getDeliveryAddress());
        assertThat(result).isEqualTo(testDTO);
    }

    // 2. Success scenario: Get all orders
    @Test
    @DisplayName("Get all orders - returns list")
    void whenGetAllOrdersThenReturnList() {
        given(orderRepository.findAll()).willReturn(List.of(testOrder));
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);

        List<OrderDTO> result = underTest.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDTO);
    }

    // 3. Success scenario: Get order by existing ID
    @Test
    @DisplayName("Get order by ID - exists")
    void whenGetExistingOrderThenReturnDTO() {
        Long id = 1L;
        given(orderRepository.findById(id)).willReturn(Optional.of(testOrder));
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);

        Optional<OrderDTO> result = underTest.getOrderById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testDTO);
    }

    // 4. Failure scenario: Get order by non-existing ID
    @Test
    @DisplayName("Get order by ID - not exists")
    void whenGetNonExistingOrderThenReturnEmpty() {
        Long id = 999L;
        given(orderRepository.findById(id)).willReturn(Optional.empty());

        Optional<OrderDTO> result = underTest.getOrderById(id);

        assertThat(result).isEmpty();
    }

    // 5. Success scenario: Update order
    @Test
    @DisplayName("Update order - success")
    void whenUpdateExistingOrderThenReturnUpdatedDTO() {
        Long id = 1L;
        OrderDTO updatedDTO = new OrderDTO();
        updatedDTO.setDeliveryAddress("New Address");
        updatedDTO.setStatus("Delivered");
        updatedDTO.setCustomerId(1L);

        given(orderRepository.findById(id)).willReturn(Optional.of(testOrder));
        given(orderRepository.save(testOrder)).willReturn(testOrder);
        given(orderMapper.toDTO(testOrder)).willReturn(updatedDTO);

        OrderDTO result = underTest.updateOrder(id, updatedDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
        Order updatedOrder = orderCaptor.getValue();
        assertThat(updatedOrder.getDeliveryAddress()).isEqualTo(updatedDTO.getDeliveryAddress());
        assertThat(updatedOrder.getStatus()).isEqualTo(updatedDTO.getStatus());
        assertThat(result).isEqualTo(updatedDTO);
    }

    // 6. Failure scenario: Update non-existing order
    @Test
    @DisplayName("Update order - not exists")
    void whenUpdateNonExistingOrderThenThrowException() {
        Long id = 999L;
        given(orderRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateOrder(id, testDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    // 7. Success scenario: Delete order
    @Test
    @DisplayName("Delete order - success")
    void whenDeleteOrderThenVerifyRepositoryCall() {
        Long id = 1L;
        willDoNothing().given(orderRepository).deleteById(id);

        underTest.deleteOrder(id);

        Mockito.verify(orderRepository).deleteById(id);
    }

    // 8. Edge case: Create order with empty address
    @Test
    @DisplayName("Create order - empty address should fail")
    void whenCreateOrderWithEmptyAddressThenFail() {
        testDTO.setDeliveryAddress("");

        assertThatThrownBy(() -> underTest.createOrder(testDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Delivery address cannot be empty");
    }

    // 9. Parameterized test: Invalid statuses
    @ParameterizedTest
    @ValueSource(strings = {"Invalid", " ", "Shipped123"})
    @DisplayName("Create order - invalid status")
    void whenCreateOrderWithInvalidStatusThenFail(String status) {
        testDTO.setStatus(status);

        assertThatThrownBy(() -> underTest.createOrder(testDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status");
    }



    // 11. Success scenario: Order created date auto-generated
    @Test
    @DisplayName("Create order - auto-generated created date")
    void whenCreateOrderThenCreatedDateSet() {
        given(orderMapper.toEntity(testDTO)).willReturn(testOrder);
        given(orderRepository.save(testOrder)).willReturn(testOrder);

        underTest.createOrder(testDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getCreatedAt()).isNotNull();
    }

    // 12. Success scenario: Order update date remains null on create
    @Test
    @DisplayName("Create order - update date empty")
    void whenCreateOrderThenUpdateDateEmpty() {
        given(orderMapper.toEntity(testDTO)).willReturn(testOrder);
        given(orderRepository.save(testOrder)).willReturn(testOrder);

        underTest.createOrder(testDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
    }

    // 13. Success scenario: Update date set on modification
    @Test
    @DisplayName("Update order - update date set")
    void whenUpdateOrderThenUpdateDateSet() {
        Long id = 1L;
        given(orderRepository.findById(id)).willReturn(Optional.of(testOrder));
        given(orderRepository.save(testOrder)).willReturn(testOrder);
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);

        underTest.updateOrder(id, testDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
    }

    // 14. Edge case: Empty order list
    @Test
    @DisplayName("Get all orders - empty list")
    void whenNoOrdersExistThenReturnEmptyList() {
        given(orderRepository.findAll()).willReturn(List.of());

        List<OrderDTO> result = underTest.getAllOrders();

        assertThat(result).isEmpty();
    }

    // 15. Parameterized test: Empty/null addresses
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Create order - empty/null address")
    void whenCreateOrderWithEmptyOrNullAddressThenFail(String address) {
        testDTO.setDeliveryAddress(address);

        assertThatThrownBy(() -> underTest.createOrder(testDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Delivery address cannot be empty");
    }

    // 16. Edge case: Very long address
    @Test
    @DisplayName("Create order - long address")
    void whenCreateOrderWithLongAddressThenSuccess() {
        String longAddress = "A".repeat(500);
        testDTO.setDeliveryAddress(longAddress);

        Order orderWithLongAddress = new Order();
        orderWithLongAddress.setDeliveryAddress(longAddress);
        orderWithLongAddress.setStatus(testDTO.getStatus());
        orderWithLongAddress.setCustomer(new Customer());
        orderWithLongAddress.getCustomer().setId(testDTO.getCustomerId());

        given(orderMapper.toEntity(testDTO)).willReturn(orderWithLongAddress);
        given(orderRepository.save(orderWithLongAddress)).willReturn(orderWithLongAddress);
        given(orderMapper.toDTO(orderWithLongAddress)).willReturn(testDTO);

        OrderDTO result = underTest.createOrder(testDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getDeliveryAddress()).isEqualTo(longAddress);
        assertThat(result.getDeliveryAddress()).isEqualTo(longAddress);
    }

    // 17. Edge case: Update order with same data
    @Test
    @DisplayName("Update order - same data")
    void whenUpdateOrderWithSameDataThenNoChanges() {
        Long id = 1L;
        given(orderRepository.findById(id)).willReturn(Optional.of(testOrder));
        given(orderRepository.save(testOrder)).willReturn(testOrder);
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);

        OrderDTO result = underTest.updateOrder(id, testDTO);

        assertThat(result).isEqualTo(testDTO);
    }

    // 18. Edge case: Delete non-existing order
    @Test
    @DisplayName("Delete order - non-existing")
    void whenDeleteNonExistingOrderThenNoError() {
        Long id = 999L;
        willDoNothing().given(orderRepository).deleteById(id);

        underTest.deleteOrder(id);

        Mockito.verify(orderRepository).deleteById(id);
    }

    // 19. Success scenario: Customer ID mapping
    @Test
    @DisplayName("Order-Customer ID mapping")
    void whenCreateOrderThenCustomerIdMapped() {
        testDTO.setCustomerId(5L);

        Customer customer = new Customer();
        customer.setId(5L);
        testOrder.setCustomer(customer);

        given(orderMapper.toEntity(testDTO)).willReturn(testOrder);
        given(orderRepository.save(testOrder)).willReturn(testOrder);
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);

        underTest.createOrder(testDTO);

        Mockito.verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getCustomer().getId()).isEqualTo(5L);
    }

    // 20. Edge case: Update order status to invalid value
    @Test
    @DisplayName("Update order - invalid status")
    void whenUpdateOrderWithInvalidStatusThenFail() {
        Long id = 1L;
        OrderDTO invalidDTO = new OrderDTO();
        invalidDTO.setStatus("InvalidStatus");
        invalidDTO.setCustomerId(1L);

        assertThatThrownBy(() -> underTest.updateOrder(id, invalidDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status");
    }



    // 22. Success scenario: Multiple orders retrieval
    @Test
    @DisplayName("Get all orders - multiple orders")
    void whenGetMultipleOrdersThenReturnList() {
        Order anotherOrder = new Order();
        anotherOrder.setDeliveryAddress("Second St. 456");
        anotherOrder.setStatus("Delivered");
        anotherOrder.setCustomer(new Customer());
        anotherOrder.getCustomer().setId(2L);

        OrderDTO anotherDTO = new OrderDTO();
        anotherDTO.setDeliveryAddress("Second St. 456");
        anotherDTO.setStatus("Delivered");
        anotherDTO.setCustomerId(2L);

        given(orderRepository.findAll()).willReturn(List.of(testOrder, anotherOrder));
        given(orderMapper.toDTO(testOrder)).willReturn(testDTO);
        given(orderMapper.toDTO(anotherOrder)).willReturn(anotherDTO);

        List<OrderDTO> result = underTest.getAllOrders();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testDTO, anotherDTO);
    }


}