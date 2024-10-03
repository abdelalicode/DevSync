package com.devsync.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.service.UserService;

import java.io.IOException;

@WebServlet(name = "UserServlet", value = "/users")
public class UserServlet extends HttpServlet {
    private UserService userService;


    public void init() {
        UserRepository userRepository = new UserRepository();
        this.userService = new UserService(userRepository);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String id = request.getParameter("id");

        if(id != null) {

        }
        userService.getAllUsers();
        this.getServletContext().getRequestDispatcher("/WEB-INF/users/allUsers.jsp").forward(request,response);

    }

    public void destroy() {
    }


}