package com.rishiraj.food_ordering_website.controller;

import com.razorpay.RazorpayException;
import com.rishiraj.food_ordering_website.IO.OrderRequest;
import com.rishiraj.food_ordering_website.IO.OrderResponse;
import com.rishiraj.food_ordering_website.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest orderRequest) throws RazorpayException {
        return orderService.createOrderWithPayment(orderRequest);
    }

    @PostMapping("/verify")
    public void verifyPayment(@RequestBody Map<String, String> paymentData){
        orderService.verifyPayment(paymentData, "Paid");
    }

    @GetMapping
    public List<OrderResponse> getAllUserOrder(){
        return orderService.getUserOrders();
    }


    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
    }


    //admin panel
    @GetMapping("/all")
    public List<OrderResponse> getOrdersOfAllUsers(){
        return orderService.getOrdersOfAllUsers();
    }
    //admin panel
    @PatchMapping("/status/{orderId}")
    public void updateOrderStatus(@PathVariable String orderId, @RequestParam String status){
        orderService.updateOrderStatus(orderId, status);
    }
}
