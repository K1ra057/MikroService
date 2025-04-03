package com.example.WebDeliverySQL;

/*
@autrhor Дима
@project MikroService-main
@class OrderControllerTest
@version 1.0.0
@sinc 03.04.2025 - 21 - 39
*/
import com.example.WebDeliverySQL.dto.OrderDTO;
import com.example.WebDeliverySQL.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO(1L, "123 Main St", "Pending", 5L);
    }

    @Test
    void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(orderDTO));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderDTO.getId()))
                .andExpect(jsonPath("$[0].deliveryAddress").value(orderDTO.getDeliveryAddress()))
                .andExpect(jsonPath("$[0].status").value(orderDTO.getStatus()))
                .andExpect(jsonPath("$[0].customerId").value(orderDTO.getCustomerId()));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(orderDTO));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.deliveryAddress").value(orderDTO.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus()))
                .andExpect(jsonPath("$.customerId").value(orderDTO.getCustomerId()));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        String orderJson = """
            {
                "id": 1,
                "deliveryAddress": "123 Main St",
                "status": "Pending",
                "customerId": 5
            }
        """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.deliveryAddress").value(orderDTO.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus()))
                .andExpect(jsonPath("$.customerId").value(orderDTO.getCustomerId()));

        verify(orderService, times(1)).createOrder(any(OrderDTO.class));
    }

    @Test
    void testUpdateOrder() throws Exception {
        when(orderService.updateOrder(eq(1L), any(OrderDTO.class))).thenReturn(orderDTO);

        String orderJson = """
            {
                "id": 1,
                "deliveryAddress": "123 Main St",
                "status": "Pending",
                "customerId": 5
            }
        """;

        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.deliveryAddress").value(orderDTO.getDeliveryAddress()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus()))
                .andExpect(jsonPath("$.customerId").value(orderDTO.getCustomerId()));

        verify(orderService, times(1)).updateOrder(eq(1L), any(OrderDTO.class));
    }

    @Test
    void testDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }
}