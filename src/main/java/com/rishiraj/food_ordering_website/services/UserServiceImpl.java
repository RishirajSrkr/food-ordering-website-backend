package com.rishiraj.food_ordering_website.services;

import com.rishiraj.food_ordering_website.IO.UserRequest;
import com.rishiraj.food_ordering_website.IO.UserResponse;
import com.rishiraj.food_ordering_website.entity.UserEntity;
import com.rishiraj.food_ordering_website.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationFacadeImpl authenticationFacade;

    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, AuthenticationFacadeImpl authenticationFacade){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        UserEntity user = userRequestToUserEntity(userRequest);
        user.setPassword(user.getPassword());
        userRepo.save(user);
        return userEntityToUserResponse(user);
    }

    @Override
    public String findByUserId() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String email = authentication.getName();
        UserEntity user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getId();
    }

    private UserEntity userRequestToUserEntity(UserRequest userRequest) {
        return UserEntity.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
    }

    private UserResponse userEntityToUserResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }
}
