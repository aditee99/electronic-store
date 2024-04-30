package com.lcwd.electroic.store.services.impl;

import com.lcwd.electroic.store.dtos.AddItemToCardRequest;
import com.lcwd.electroic.store.dtos.CartDto;
import com.lcwd.electroic.store.entities.Cart;
import com.lcwd.electroic.store.entities.CartItem;
import com.lcwd.electroic.store.entities.Product;
import com.lcwd.electroic.store.entities.User;
import com.lcwd.electroic.store.exceptions.BadApiRequest;
import com.lcwd.electroic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electroic.store.repositories.CartItemRepository;
import com.lcwd.electroic.store.repositories.CartRepository;
import com.lcwd.electroic.store.repositories.ProductRepository;
import com.lcwd.electroic.store.repositories.UserRepository;
import com.lcwd.electroic.store.services.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Override
    public CartDto addItemToCart(String userId, AddItemToCardRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if(quantity<=0){
            throw new BadApiRequest("Requested quantity is not valid");
        }
        //fetch the product
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found in database"));
        //fetch user from db
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found in database"));
        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
        }
        catch(NoSuchElementException e){
            cart = new Cart();
            cart.setCreatedAt(new Date());
            cart.setCartId(UUID.randomUUID().toString());
        }
       //perform cart operations
        //if cart items already present then update
        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItem> items = cart.getItems();
        items =items.stream().map(item-> {
                     if (item.getProduct().getProductId().equals(productId)) {
                         //items already present in cart
                         item.setQuantity(quantity);
                         item.setTotalPrice((int) (quantity*product.getDiscountedPrice()));
                         updated.set(true);
                     }
                     return item;
                 }).collect(Collectors.toList());
        //cart.setItems(updatedItems);
        //create items
        if(!updated.get()) {
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice((int) (quantity * product.getDiscountedPrice()))
                    .cart(cart)
                    .product(product)
                    .build();
            //add to list
            cart.getItems().add(cartItem);
        }
        //set user
        cart.setUser(user);
        Cart updatedCart = cartRepository.save(cart);
        return mapper.map(updatedCart, CartDto.class);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItemId) {
        //conditions

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new ResourceNotFoundException("Cart Item not found in database"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found in database"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(()->new ResourceNotFoundException("Cart of given user not found in database"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found in database"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(()->new ResourceNotFoundException("Cart of given user not found in database"));
        return mapper.map(cart , CartDto.class);
    }
}
