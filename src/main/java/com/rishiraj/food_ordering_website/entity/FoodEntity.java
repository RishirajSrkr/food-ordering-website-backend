package com.rishiraj.food_ordering_website.entity;

import com.rishiraj.food_ordering_website.enums.FoodCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private FoodCategory category;
    private String imageUrl;
}
