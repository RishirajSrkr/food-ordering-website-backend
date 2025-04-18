package com.rishiraj.food_ordering_website.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rishiraj.food_ordering_website.IO.OrderRequest;
import com.rishiraj.food_ordering_website.IO.OrderResponse;
import com.rishiraj.food_ordering_website.entity.OrderEntity;
import com.rishiraj.food_ordering_website.repository.CartRepo;
import com.rishiraj.food_ordering_website.repository.OrderRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UserService userService;

    @Value("${razorpay.key}")
    private String RAZORPAY_KEY;

    @Value("${razorpay.secret}")
    private String RAZORPAY_SECRET;

    public OrderServiceImpl(OrderRepo orderRepo, CartRepo cartRepo, UserService userService) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.userService = userService;
    }


    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) throws RazorpayException {
        OrderEntity orderEntity = convertOrderRequestToOrderEntity(request);
        orderEntity = orderRepo.save(orderEntity);

        // create razorpay payment order
        RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", (int)orderEntity.getAmount() * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        orderEntity.setRazorPayOrderId(razorpayOrderId);
        String loggedInUserId = userService.findByUserId();
        orderEntity.setUserId(loggedInUserId);

        orderEntity = orderRepo.save(orderEntity);

        return convertOrderEntityToOrderResponse(orderEntity);
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        String razorpaySignature = paymentData.get("razorpay_signature");
        String razorpayPaymentId = paymentData.get("razorpay_payment_id");

        //we have the razorpay order ID, now use it to get the order detail from repo.
        OrderEntity existingOrder = orderRepo.findByRazorPayOrderId(razorpayOrderId).orElseThrow(() -> new RuntimeException("Order not found!"));

        existingOrder.setPaymentStatus(status);
        existingOrder.setRazorPaySignature(razorpaySignature);
        existingOrder.setRazorPaySignature(razorpayPaymentId);

        orderRepo.save(existingOrder);

        //deleting the cart after successful payment
        if("Paid".equalsIgnoreCase(status)){
            cartRepo.deleteByUserId(existingOrder.getUserId());
        }
        
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
        List<OrderEntity> orderEntities  = orderRepo.findByUserId(loggedInUserId);
        return orderEntities.stream().map(orderEntity ->  convertOrderEntityToOrderResponse(orderEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepo.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getOrdersOfAllUsers() {
        List<OrderEntity> orderEntities  = orderRepo.findAll();
        return orderEntities.stream().map(orderEntity ->  convertOrderEntityToOrderResponse(orderEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setOrderStatus(status);
        orderRepo.save(order);
    }


    private OrderResponse convertOrderEntityToOrderResponse(OrderEntity order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userAddress(order.getUserAddress())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .amount(order.getAmount())
                .paymentStatus(order.getPaymentStatus())
                .razorpayOrderId(order.getRazorPayOrderId())
                .orderStatus(order.getOrderStatus())
                .orderItems(order.getOrderedItems())
                .orderDateAndTime(LocalDateTime.now())
                .build();
    }


    private OrderEntity convertOrderRequestToOrderEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .amount(request.getAmount())
                .orderedItems(request.getOrderedItems())
                .orderStatus(request.getOrderStatus())
                .build();
    }

}
