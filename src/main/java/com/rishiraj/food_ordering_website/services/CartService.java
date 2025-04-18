package com.rishiraj.food_ordering_website.services;

import com.rishiraj.food_ordering_website.IO.CartRequest;
import com.rishiraj.food_ordering_website.IO.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest cartRequest);

    CartResponse removeFromCart(CartRequest cartRequest);

    CartResponse getCart();

    void clearCart();


}
