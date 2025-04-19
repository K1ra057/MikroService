package com.example.WebDeliverySQL.mapper;

import com.example.WebDeliverySQL.model.Order;
import com.example.WebDeliverySQL.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "customer.id", target = "customerId")
    OrderDTO toDTO(Order order);

    @Mapping(source = "customerId", target = "customer.id")
    Order toEntity(OrderDTO orderDTO);
}
