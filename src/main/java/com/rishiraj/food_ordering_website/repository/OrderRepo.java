package com.rishiraj.food_ordering_website.repository;

import com.rishiraj.food_ordering_website.entity.OrderEntity;
import org.apache.catalina.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends MongoRepository<OrderEntity, String> {

    List<OrderEntity> findByUserId(String userId);

    Optional<OrderEntity> findByRazorPayOrderId(String razorPayOrderId);

}
