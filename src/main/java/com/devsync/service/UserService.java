package com.devsync.service;


import com.devsync.domain.entity.User;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.util.DateUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.Random;


public class UserService {


    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void addUser(User user) {
        userRepository.save(user);
    }

    public boolean registerUser(String firstName, String lastName, String email, String password){
        Random random = new Random();
        int randomNumber = random.nextInt(9000);
        String username = firstName + randomNumber;
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setUsername(username);
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password){
       Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }
       return Optional.empty();

    }

    public User findUserById(int id) {
        return userRepository.findById(id);
    }


    public boolean updateUser(int id, String firstName, String lastName, String email) {

        User user = userRepository.findById(id);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);


        try {
            userRepository.update(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> getAllUsers() {
        return  userRepository.findAll();
    }





}
