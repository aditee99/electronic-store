package com.lcwd.electroic.store.repositories;

import com.lcwd.electroic.store.entities.Order;
import com.lcwd.electroic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByUser(User user);
}
