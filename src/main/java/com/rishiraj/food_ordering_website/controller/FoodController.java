package com.rishiraj.food_ordering_website.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rishiraj.food_ordering_website.IO.FoodRequest;
import com.rishiraj.food_ordering_website.IO.FoodResponse;
import com.rishiraj.food_ordering_website.services.FoodServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodServiceImpl foodService;

    @PostMapping
    public FoodResponse addFood(
            @RequestPart("food") String foodJsonString,
            @RequestPart("file") MultipartFile file
    ) {

        //converting the incoming request
        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest foodRequest = null;

        try{
             foodRequest = objectMapper.readValue(foodJsonString, FoodRequest.class);
        }
        catch (JsonProcessingException e){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON Format");
        }

        FoodResponse foodResponse = foodService.addFood(foodRequest, file);
        return foodResponse;
    }


    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAllFoods(){
        List<FoodResponse> allFoods = foodService.getAllFoods();
        return new ResponseEntity<>(allFoods, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFood(@PathVariable String id){
        FoodResponse food = foodService.getFood(id);
        return new ResponseEntity<>(food, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable String id){
        foodService.deleteFood(id);
    }
}
