package com.lcwd.electroic.store.services.impl;

import com.lcwd.electroic.store.dtos.CreateOrderRequest;
import com.lcwd.electroic.store.dtos.OrderDto;
import com.lcwd.electroic.store.dtos.PageableResponse;
import com.lcwd.electroic.store.entities.*;
import com.lcwd.electroic.store.exceptions.BadApiRequest;
import com.lcwd.electroic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electroic.store.helper.Helper;
import com.lcwd.electroic.store.repositories.CartRepository;
import com.lcwd.electroic.store.repositories.OrderRepository;
import com.lcwd.electroic.store.repositories.UserRepository;
import com.lcwd.electroic.store.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public OrderDto createOrder(CreateOrderRequest orderDto) {
       User user = userRepository.findById(orderDto.getUserId()).orElseThrow(()->new ResourceNotFoundException("User with given id not found in database"));
       Cart cart = cartRepository.findById(orderDto.getCartId()).orElseThrow(()->new ResourceNotFoundException("Cart with given id not found in database"));
       List<CartItem> cartItems = cart.getItems();

       if(cartItems.size() <= 0){
            throw new BadApiRequest("Invalid number of items in cart");
       }

       //other checks
        Order order = Order.builder()
                .billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .billingAddress(orderDto.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(orderDto.getPaymentStatus())
                .orderStatus(orderDto.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();
       //orderItems, amount
        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem-> {
            //CartItem->OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice((int) (cartItem.getQuantity()*cartItem.getProduct().getDiscountedPrice()))
                    .order(order)
                    .build();
            orderAmount.set(orderAmount.get()+orderItem.getTotalPrice());
            return  orderItem;
        }).collect(Collectors.toList());
        order.setOrderItemList(orderItems);
        order.setOrderAmount(orderAmount.get());
        cart.getItems().clear();
        cartRepository.save(cart) ;
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public void removeOrder(String orderId) {
       Order order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order is not found"));
       orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getOrdersOfUsers(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User with given id not found in database"));
        List<Order> orderList = orderRepository.findByUser(user);
        List<OrderDto> orderDtos = orderList.stream().map(order->modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());

        return orderDtos;
    }

    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Order> page = orderRepository.findAll(pageable);
        return Helper.getPageableResponse(page, OrderDto.class);
    }
}
