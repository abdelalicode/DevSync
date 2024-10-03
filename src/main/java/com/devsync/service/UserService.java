package com.devsync.service;


import com.devsync.domain.entity.User;
import com.devsync.repository.Implementations.UserRepository;

import java.util.List;


public class UserService {


    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void addUser(User user) {

        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers;
    }





}
