package com.devsync.controller;


import com.devsync.domain.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.service.UserService;

import java.io.IOException;
import java.util.List;

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
            int userId = Integer.parseInt(id);
            User user = userService.findUserById(userId);
            request.setAttribute("user", user);
            this.getServletContext().getRequestDispatcher("/WEB-INF/views/user.jsp").forward(request, response);
        }
        else {
            List allUsers = userService.getAllUsers();
            request.setAttribute("users", allUsers);
            this.getServletContext().getRequestDispatcher("/WEB-INF/views/allUsers.jsp").forward(request,response);
        }


    }

    public void destroy() {
    }


}