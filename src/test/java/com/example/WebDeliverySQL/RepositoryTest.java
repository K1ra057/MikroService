package com.example.WebDeliverySQL;
import com.example.WebDeliverySQL.model.Customer;
import com.example.WebDeliverySQL.model.Order;
import com.example.WebDeliverySQL.repository.CustomerRepository;
import com.example.WebDeliverySQL.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
@autrhor Дима
@project MikroService-main
@class RepositoryTest
@version 1.0.0
@sinc 24.04.2025 - 22 - 32
*/

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Customer john, jane;

    @BeforeEach
    void setUp() {
        john = new Customer(null, "John Doe", "john@example.com", List.of());
        jane = new Customer(null, "Jane Smith", "jane@example.com", List.of());
        john = customerRepository.save(john);
        jane = customerRepository.save(jane);

        Order order1 = new Order(null, "Kyiv, Main St", "Pending", LocalDateTime.now(), john);
        Order order2 = new Order(null, "Lviv, Center St", "Shipped", LocalDateTime.now(), jane);

        orderRepository.saveAll(List.of(order1, order2));
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testCustomerSetShouldContain_2_Records() {
        List<Customer> customers = customerRepository.findAll();
        assertEquals(2, customers.size());
    }

    @Test
    void testOrderSetShouldContain_2_Records() {
        List<Order> orders = orderRepository.findAll();
        assertEquals(2, orders.size());
    }

    @Test
    void shouldAssignIdToNewCustomer() {
        Customer testCustomer = new Customer(null, "Alice", "alice@example.com", List.of());
        Customer saved = customerRepository.save(testCustomer);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void shouldAssignIdToNewOrder() {
        Order testOrder = new Order(null, "Odesa", "Delivered", LocalDateTime.now(), john);
        Order saved = orderRepository.save(testOrder);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void whenCustomerHasIdThenItCanBeSaved() {
        // Создаем и сохраняем нового кастомера (без ID)
        Customer customer = new Customer(null, "Static ID", "static@example.com", new ArrayList<>());
        Customer savedCustomer = customerRepository.save(customer);

        // Обновляем существующую запись
        Customer updatedCustomer = new Customer(savedCustomer.getId(), "Updated Name", "updated@example.com", new ArrayList<>());
        Customer result = customerRepository.save(updatedCustomer);

        assertEquals(savedCustomer.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
    }

    @Test
    void whenOrderHasIdThenItCanBeSaved() {
        // Создаем и сохраняем новый заказ (без ID)
        Order order = new Order(null, "Dnipro", "Pending", LocalDateTime.now(), john);
        Order savedOrder = orderRepository.save(order);

        // Обновляем существующую запись
        Order updatedOrder = new Order(savedOrder.getId(), "Updated Address", "Shipped", LocalDateTime.now(), john);
        Order result = orderRepository.save(updatedOrder);

        assertEquals(savedOrder.getId(), result.getId());
        assertEquals("Shipped", result.getStatus());
    }
}
