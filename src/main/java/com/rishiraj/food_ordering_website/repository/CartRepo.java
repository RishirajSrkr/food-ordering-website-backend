package com.rishiraj.food_ordering_website.repository;

import com.rishiraj.food_ordering_website.entity.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends MongoRepository<CartEntity, String> {

    Optional<CartEntity>findByUserId(String userId);
    void deleteByUserId(String userId);
}
