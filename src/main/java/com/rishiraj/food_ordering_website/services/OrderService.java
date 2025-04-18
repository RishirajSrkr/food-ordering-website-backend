package com.rishiraj.food_ordering_website.services;

import com.razorpay.RazorpayException;
import com.rishiraj.food_ordering_website.IO.OrderRequest;
import com.rishiraj.food_ordering_website.IO.OrderResponse;
import com.rishiraj.food_ordering_website.entity.OrderEntity;

import java.util.List;
import java.util.Map;

public interface OrderService {

    OrderResponse createOrderWithPayment(OrderRequest request) throws RazorpayException;

    void verifyPayment(Map<String, String> paymentData, String status);

    List<OrderResponse> getUserOrders();

    void removeOrder(String orderId);

    //for admin
    List<OrderResponse> getOrdersOfAllUsers();

    //update order status after admin has updated it (preparing, delivering, delivered etc)
    void updateOrderStatus(String orderId, String status);


}
