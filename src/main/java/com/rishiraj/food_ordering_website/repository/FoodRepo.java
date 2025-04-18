package com.rishiraj.food_ordering_website.repository;

import com.rishiraj.food_ordering_website.entity.FoodEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepo extends MongoRepository<FoodEntity, String> {
}
