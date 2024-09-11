package com.project.G1_T3.player.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.G1_T3.player.repository.UserRepository;
import com.project.G1_T3.player.model.User;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; 

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

}
