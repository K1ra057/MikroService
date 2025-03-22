package com.example.WebDeliverySQL;

import com.example.WebDeliverySQL.dto.CustomerDTO;
import com.example.WebDeliverySQL.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    // Конструктор для внедрения зависимостей
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    // Получить всех клиентов с преобразованием в DTO
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)  // Преобразуем сущности в DTO
                .collect(Collectors.toList());
    }

    // Получить клиента по ID с преобразованием в DTO
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDTO);  // Преобразуем сущность в DTO
    }

    // Создать нового клиента из DTO
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);  // Преобразуем DTO в сущность
        Customer savedCustomer = customerRepository.save(customer);  // Сохраняем сущность
        return customerMapper.toDTO(savedCustomer);  // Возвращаем DTO
    }

    // Обновить клиента по ID с преобразованием DTO в сущность
    public CustomerDTO updateCustomer(Long id, CustomerDTO updatedCustomerDTO) {
        return customerRepository.findById(id)
                .map(customer -> {
                    // Обновляем свойства сущности на основе DTO
                    customer.setName(updatedCustomerDTO.getName());
                    customer.setEmail(updatedCustomerDTO.getEmail());
                    customer.setOrders(customerMapper.orderIdsToOrders(updatedCustomerDTO.getOrderIds()));  // Обновляем заказы

                    // Сохраняем обновленную сущность
                    Customer updatedCustomer = customerRepository.save(customer);
                    return customerMapper.toDTO(updatedCustomer);  // Возвращаем обновленный DTO
                })
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // Удалить клиента по ID
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);  // Удаляем клиента по ID
    }
}
