package com.rishiraj.food_ordering_website.controller;

import com.rishiraj.food_ordering_website.IO.CartRequest;
import com.rishiraj.food_ordering_website.IO.CartResponse;
import com.rishiraj.food_ordering_website.services.AuthenticationFacadeImpl;
import com.rishiraj.food_ordering_website.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@RequestBody CartRequest request) {

        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food ID not found");
        }

        CartResponse cartResponse = cartService.addToCart(request);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<CartResponse> removeFromCart(@RequestBody CartRequest request) {

        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food ID not found");
        }

        CartResponse cartResponse = cartService.removeFromCart(request);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return new ResponseEntity<>("Cart deleted successfully", HttpStatus.OK);
    }


}
