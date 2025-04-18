package com.rishiraj.food_ordering_website.services;

import com.rishiraj.food_ordering_website.IO.FoodRequest;
import com.rishiraj.food_ordering_website.IO.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    //uploads the file in s3 and returns public url of the file
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest foodRequest, MultipartFile file);

    List<FoodResponse> getAllFoods();

    FoodResponse getFood(String id);

    boolean deleteFile(String fileName);

    void deleteFood(String id);
}
