package com.lcwd.electroic.store.dtos;

import com.lcwd.electroic.store.entities.OrderItem;
import com.lcwd.electroic.store.entities.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDto {

    private String orderId;
    private String orderStatus="PENDING";
    private String paymentStatus="NOTPAID";
    private int orderAmount;
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderedDate = new Date();
    private Date deliveredDate;
    private List<OrderItemDto> orderItemList = new ArrayList<>();
}
