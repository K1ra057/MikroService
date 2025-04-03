package com.example.WebDeliverySQL;

/*
@autrhor Дима
@project MikroService-main
@class CustomerControllerTest
@version 1.0.0
@sinc 03.04.2025 - 21 - 25

*/
import com.example.WebDeliverySQL.dto.CustomerDTO;
import com.example.WebDeliverySQL.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void getAllCustomers_ShouldReturnList() throws Exception {
        Mockito.when(customerService.getAllCustomers()).thenReturn(
                Arrays.asList(new CustomerDTO(1L, "John Doe", "johndoe@example.com", null))
        );

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() throws Exception {
        Mockito.when(customerService.getCustomerById(1L)).thenReturn(
                Optional.of(new CustomerDTO(1L, "John Doe", "johndoe@example.com", null))
        );

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John Doe", "johndoe@example.com", null);
        Mockito.when(customerService.createCustomer(any())).thenReturn(customerDTO);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"email\": \"johndoe@example.com\", \"orderIds\": null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO(1L, "John Doe", "johndoe@example.com", null);
        Mockito.when(customerService.updateCustomer(eq(1L), any())).thenReturn(customerDTO);

        mockMvc.perform(put("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"email\": \"johndoe@example.com\", \"orderIds\": null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());
    }
}