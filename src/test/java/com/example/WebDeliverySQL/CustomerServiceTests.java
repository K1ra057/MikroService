package com.example.WebDeliverySQL;

import com.example.WebDeliverySQL.dto.CustomerDTO;
import com.example.WebDeliverySQL.mapper.CustomerMapper;
import com.example.WebDeliverySQL.model.Customer;
import com.example.WebDeliverySQL.model.Order;
import com.example.WebDeliverySQL.repository.CustomerRepository;
import com.example.WebDeliverySQL.service.CustomerService;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

/*
@autrhor Дима
@project MikroService-main — копия
@class CustomerServiceTests
@version 1.0.0
@sinc 04.05.2025 - 13 - 25
*/
@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService underTest;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    private CustomerDTO testDTO;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testDTO = new CustomerDTO();
        testDTO.setName("John Doe");
        testDTO.setEmail("john@example.com");

        testCustomer = new Customer();
        testCustomer.setName(testDTO.getName());
        testCustomer.setEmail(testDTO.getEmail());
    }

    @Test
    @DisplayName("Create customer - success")
    void whenCreateCustomerWithValidDataThenSuccess() {
        // Given
        given(customerMapper.toEntity(testDTO)).willReturn(testCustomer);
        given(customerRepository.save(testCustomer)).willReturn(testCustomer);
        given(customerMapper.toDTO(testCustomer)).willReturn(testDTO);

        // When
        CustomerDTO result = underTest.createCustomer(testDTO);

        // Then
        then(customerRepository).should().save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertThat(savedCustomer.getName()).isEqualTo(testDTO.getName());
        assertThat(savedCustomer.getEmail()).isEqualTo(testDTO.getEmail());
        assertThat(result).isEqualTo(testDTO);

        then(customerMapper).should().toEntity(testDTO);
        then(customerRepository).should().save(testCustomer);
        then(customerMapper).should().toDTO(testCustomer);
    }

    @Test
    @DisplayName("Get customer by ID - exists")
    void whenGetExistingCustomerThenReturnDTO() {
        // Given
        Long id = 1L;
        given(customerRepository.findById(id)).willReturn(Optional.of(testCustomer));
        given(customerMapper.toDTO(testCustomer)).willReturn(testDTO);

        // When
        Optional<CustomerDTO> result = underTest.getCustomerById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testDTO);
        then(customerRepository).should().findById(id);
    }

    @Test
    @DisplayName("Update customer - success")
    void whenUpdateExistingCustomerThenReturnUpdatedDTO() {
        // Given
        Long id = 1L;
        CustomerDTO updatedDTO = new CustomerDTO();
        updatedDTO.setName("Updated Name");
        updatedDTO.setEmail("updated@example.com");

        given(customerRepository.findById(id)).willReturn(Optional.of(testCustomer));
        given(customerRepository.save(testCustomer)).willReturn(testCustomer);
        given(customerMapper.toDTO(testCustomer)).willReturn(updatedDTO);

        // When
        CustomerDTO result = underTest.updateCustomer(id, updatedDTO);

        // Then
        then(customerRepository).should().save(customerCaptor.capture());
        Customer updatedCustomer = customerCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(updatedDTO.getName());
        assertThat(updatedCustomer.getEmail()).isEqualTo(updatedDTO.getEmail());
        assertThat(result).isEqualTo(updatedDTO);

        then(customerRepository).should(times(1)).findById(id);
        then(customerRepository).should(times(1)).save(testCustomer);
    }

    @Test
    @DisplayName("Delete customer - verify call")
    void whenDeleteCustomerThenRepositoryMethodCalled() {
        // Given
        Long id = 1L;

        // When
        underTest.deleteCustomer(id);

        // Then
        then(customerRepository).should().deleteById(id);
        then(customerRepository).should(times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Get all customers - returns list")
    void whenGetAllCustomersThenReturnDTOList() {
        // Given
        List<Customer> customers = List.of(testCustomer);
        given(customerRepository.findAll()).willReturn(customers);
        given(customerMapper.toDTO(testCustomer)).willReturn(testDTO);

        // When
        List<CustomerDTO> result = underTest.getAllCustomers();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDTO);
        then(customerRepository).should().findAll();
    }
    // Тест 6: Невалидный email
    @Test
    @DisplayName("Create customer - invalid email")
    void whenCreateCustomerWithInvalidEmailThenFail() {
        // Given
        testDTO.setEmail("invalid-email");

        // When & Then
        assertThatThrownBy(() -> underTest.createCustomer(testDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");

        // Проверяем что маппер и репозиторий не вызывались
        Mockito.verify(customerMapper, never()).toEntity(any());
        Mockito.verify(customerRepository, never()).save(any());
    }

    // Тест 7: Обновление несуществующего клиента
    @Test
    @DisplayName("Update customer - not exists")
    void whenUpdateNonExistingCustomerThenThrowException() {
        // Given
        Long id = 999L;
        given(customerRepository.findById(id)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, testDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found");
    }

    // Тест 8: Валидация пустого имени
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Create customer - empty name")
    void whenCreateCustomerWithEmptyNameThenFail(String name) {
        // Given
        testDTO.setName(name);
        lenient().when(customerMapper.toEntity(testDTO)).thenReturn(testCustomer);

        // When & Then
        assertThatThrownBy(() -> underTest.createCustomer(testDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be empty");
    }



    // Тест 9: Проверка маппинга заказов
    @Test
    @DisplayName("Customer-Order mapping")
    void whenCreateCustomerWithOrdersThenCorrectMapping() {
        // Given
        testDTO.setOrderIds(List.of(1L, 2L));
        Customer customerWithOrders = new Customer();
        customerWithOrders.setOrders(List.of(new Order(), new Order()));

        given(customerMapper.toEntity(testDTO)).willReturn(customerWithOrders);
        given(customerRepository.save(customerWithOrders)).willReturn(customerWithOrders);

        // When
        underTest.createCustomer(testDTO);

        // Then
        then(customerRepository).should().save(customerCaptor.capture());
        assertThat(customerCaptor.getValue().getOrders()).hasSize(2);
    }

    // Тест 10: Удаление несуществующего клиента
    @Test
    @DisplayName("Delete non-existing customer")
    void whenDeleteNonExistingCustomerThenNoError() {
        // Given
        Long id = 999L;
        willDoNothing().given(customerRepository).deleteById(id);

        // When
        underTest.deleteCustomer(id);

        // Then
        then(customerRepository).should().deleteById(id);
    }
}
