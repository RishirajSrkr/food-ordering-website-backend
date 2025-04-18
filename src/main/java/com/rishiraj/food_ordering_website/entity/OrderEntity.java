package com.rishiraj.food_ordering_website.entity;

import com.rishiraj.food_ordering_website.IO.OrderItem;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "orders")
public class OrderEntity {
    @Id
    private String id;
    private String userId;
    private String userAddress;
    private String phoneNumber;
    private String email;
    private List<OrderItem> orderedItems;
    private double amount;
    private String razorpayPaymentId;
    private String paymentStatus;
    private String razorPayOrderId;
    private String razorPaySignature;
    private String orderStatus;
    private LocalDateTime orderDateAndTime;
}
