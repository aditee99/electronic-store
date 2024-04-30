package com.lcwd.electroic.store.services;

import com.lcwd.electroic.store.dtos.CreateOrderRequest;
import com.lcwd.electroic.store.dtos.OrderDto;
import com.lcwd.electroic.store.dtos.PageableResponse;

import java.util.List;

public interface OrderService {
    //create order
    OrderDto createOrder(CreateOrderRequest orderDto);
    //remove order
    void removeOrder(String orderId);
    //get order of user
    List<OrderDto> getOrdersOfUsers(String userId);
    //get orders
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir );
    //other methods
}
