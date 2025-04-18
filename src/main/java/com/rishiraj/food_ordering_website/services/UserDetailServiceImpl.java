package com.rishiraj.food_ordering_website.services;

import com.rishiraj.food_ordering_website.entity.UserEntity;
import com.rishiraj.food_ordering_website.repository.UserRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    public UserDetailServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();

    }
}
