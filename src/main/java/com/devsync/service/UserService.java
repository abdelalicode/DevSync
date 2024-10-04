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

    public User findUserById(int id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }





}
