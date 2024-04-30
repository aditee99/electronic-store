package com.lcwd.electroic.store.services;

import com.lcwd.electroic.store.dtos.AddItemToCardRequest;
import com.lcwd.electroic.store.dtos.CartDto;

public interface CartService {

    //add items to cart:
    //case 1: cart for user is not available: we will create the cart and then add the items
    //case 2 : cart is available : Add the items to cart

    CartDto addItemToCart(String userId, AddItemToCardRequest request);

    //remove item from cart:
    void removeItemFromCart(String userId,int cartItem);

    //remove all items from cart
    void clearCart(String userId);

    CartDto getCartByUser(String userId);
}
