package com.lcwd.electroic.store.repositories;

import com.lcwd.electroic.store.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}
