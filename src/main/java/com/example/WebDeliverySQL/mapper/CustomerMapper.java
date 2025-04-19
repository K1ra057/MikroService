package com.example.WebDeliverySQL.mapper;


import com.example.WebDeliverySQL.model.Customer;
import com.example.WebDeliverySQL.model.Order;
import com.example.WebDeliverySQL.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(source = "orders", target = "orderIds", qualifiedByName = "ordersToOrderIds")
    CustomerDTO toDTO(Customer customer);

    // Преобразование CustomerDTO в Customer
    @Mapping(source = "orderIds", target = "orders", qualifiedByName = "orderIdsToOrders")
    Customer toEntity(CustomerDTO customerDTO);

    // Преобразование списка заказов в список ID заказов
    @Named("orderIdsToOrders")
    default List<Order> orderIdsToOrders(List<Long> orderIds) {
        if (orderIds == null) {
            return null;
        }
        List<Order> orders = new ArrayList<>();
        for (Long id : orderIds) {
            Order order = new Order();
            order.setId(id);  // Только устанавливаем ID
            orders.add(order);
        }
        return orders;
    }

    // Преобразование списка заказов в список ID
    @Named("ordersToOrderIds")
    default List<Long> ordersToOrderIds(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        List<Long> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        return orderIds;
    }
}
