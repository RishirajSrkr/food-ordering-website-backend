package com.rishiraj.food_ordering_website.controller;

import com.rishiraj.food_ordering_website.IO.AuthenticationRequest;
import com.rishiraj.food_ordering_website.IO.AuthenticationResponse;
import com.rishiraj.food_ordering_website.IO.UserRequest;
import com.rishiraj.food_ordering_website.IO.UserResponse;
import com.rishiraj.food_ordering_website.services.UserDetailServiceImpl;
import com.rishiraj.food_ordering_website.services.UserService;
import com.rishiraj.food_ordering_website.utils.JwtUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailServiceImpl userDetailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.registerUser(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);

            authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            //generate token
            UserDetails userDetails = userDetailService.loadUserByUsername(email);
            String jwtToken = jwtUtils.generateToken(userDetails);
            return new ResponseEntity<>(new AuthenticationResponse(email, jwtToken), HttpStatus.OK);

        } catch (
                BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
