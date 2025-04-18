package com.rishiraj.food_ordering_website.services;


import com.rishiraj.food_ordering_website.IO.UserRequest;
import com.rishiraj.food_ordering_website.IO.UserResponse;
import com.rishiraj.food_ordering_website.entity.UserEntity;

public interface UserService {

   UserResponse registerUser(UserRequest userRequest);
   String findByUserId();
}
