package com.devsync.controller;


import com.devsync.domain.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.service.UserService;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
           /* User sessionUser = (User) request.getSession(false).getAttribute("user");
            int userId =  sessionUser.getId();
            User user = userService.findUserById(userId);
            request.setAttribute("user", user);*/
            this.getServletContext().getRequestDispatcher("/WEB-INF/views/user.jsp").forward(request, response);
        }
        else {
            List<User> allUsers = userService.getAllUsers();
            request.setAttribute("users", allUsers);
            this.getServletContext().getRequestDispatcher("/WEB-INF/views/allUsers.jsp").forward(request,response);
        }


    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        if ("updateUser".equals(action)) {
            String userId = request.getParameter("userId");
            int id = Integer.parseInt(userId);
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");

            userService.updateUser(id, firstName, lastName, email);
            response.sendRedirect(request.getContextPath() + "/users");

        }
    }

    public void destroy() {
    }


}