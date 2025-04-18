package com.rishiraj.food_ordering_website.services;

import com.rishiraj.food_ordering_website.IO.CartRequest;
import com.rishiraj.food_ordering_website.IO.CartResponse;
import com.rishiraj.food_ordering_website.entity.CartEntity;
import com.rishiraj.food_ordering_website.repository.CartRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final UserService userService;

    public CartServiceImpl(CartRepo cartRepo, UserService userService) {
        this.cartRepo = cartRepo;
        this.userService = userService;
    }

    @Override
    public CartResponse addToCart(CartRequest request) {
        String loggedInUserId = userService.findByUserId();
        Optional<CartEntity> cartOptional = cartRepo.findByUserId(loggedInUserId);
        CartEntity cart = cartOptional.orElseGet(() -> new CartEntity(loggedInUserId, new HashMap<>()));
        Map<String, Integer> cartItems = cart.getItems();
        cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(), 0) + 1);
        cart.setItems(cartItems);
        CartEntity savedEntity = cartRepo.save(cart);
        return cartEntityToCartResponse(savedEntity);
    }

    @Override
    public CartResponse removeFromCart(CartRequest cartRequest) {

        String foodId = cartRequest.getFoodId();
        String loggedInUserId = userService.findByUserId();
        CartEntity cartEntity = cartRepo.findByUserId(loggedInUserId).orElseThrow(() -> new RuntimeException("Cart is not found"));
        Map<String, Integer> cartItems = cartEntity.getItems();

        if(cartItems.containsKey(foodId)){
            int currentQty = cartItems.get(foodId);
            if(currentQty > 0){
                cartItems.put(foodId, currentQty - 1);
            }
        }
        else{
            cartItems.remove(foodId);
        }

        CartEntity updatedCartEntity = cartRepo.save(cartEntity);
        return cartEntityToCartResponse(updatedCartEntity);
    }

    @Override
    public CartResponse getCart() {
        String loggedInUserId = userService.findByUserId();
        CartEntity cartEntity = cartRepo.findByUserId(loggedInUserId).orElse(new CartEntity(null, loggedInUserId, new HashMap<>()));
        return cartEntityToCartResponse(cartEntity);
    }

    @Override
    public void clearCart() {
        String loggedInUserId = userService.findByUserId();
        cartRepo.deleteByUserId(loggedInUserId);
    }

    private CartResponse cartEntityToCartResponse(CartEntity entity){
        return CartResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(entity.getItems())
                .build();
    }
}
