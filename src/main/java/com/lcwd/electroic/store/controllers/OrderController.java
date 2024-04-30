package com.lcwd.electroic.store.controllers;

import com.lcwd.electroic.store.dtos.*;
import com.lcwd.electroic.store.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request){
        OrderDto orderDto = orderService.createOrder(request);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .status(HttpStatus.OK)
                .message("Order is deleted!")
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUsers(@PathVariable String userId){
        List<OrderDto> orderDtos = orderService.getOrdersOfUsers(userId);
        return new ResponseEntity<>(orderDtos,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getOrders(@RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
                                                               @RequestParam(value ="sortBy",defaultValue = "orderedDate",required = false)String sortBy,
                                                               @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<OrderDto> orderDtos = orderService.getOrders(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(orderDtos,HttpStatus.OK);
    }
}
