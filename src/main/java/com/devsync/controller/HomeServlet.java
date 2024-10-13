package com.devsync.controller;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.devsync.domain.entity.Tag;
import com.devsync.domain.entity.Task;
import com.devsync.domain.entity.Token;
import com.devsync.domain.entity.User;
import com.devsync.repository.Implementations.TagRepository;
import com.devsync.repository.Implementations.TaskRepository;
import com.devsync.repository.Implementations.TokenRepository;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.service.TagService;
import com.devsync.service.TaskService;
import com.devsync.service.TokenService;
import com.devsync.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "HomeServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    private TaskService taskService;
    private TagService tagService;
    private UserService userService;
    private TokenService tokenService;

    public void init() {
        TaskRepository taskRepository = new TaskRepository();
        TagRepository tagRepository = new TagRepository();
        UserRepository userRepository = new UserRepository();
        TokenRepository tokenRepository = new TokenRepository();
        this.taskService = new TaskService(taskRepository,userRepository,tokenRepository);
        this.tagService = new TagService(tagRepository);
        this.userService = new UserService(userRepository, tokenRepository);
        this.tokenService = new TokenService(tokenRepository);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("auth?form=login");
            return;
        }
        User AuthUser = (User) session.getAttribute("user");
        List<Task> tasks = taskService.getTasks(AuthUser);
        List<Task> unassignedTasks = taskService.getUnassignedTasks();
        List<Tag> tags = tagService.getTags();
        List<User> allUsers = userService.getAllUsers();
        Token userToken = tokenService.getTokenByUser(AuthUser);
        Map<Task, Long> taskRemainingDaysMap = taskService.getTasksWithRemainingDays();
        request.setAttribute("taskRemainingDays", taskRemainingDaysMap);
        request.setAttribute("tasks", tasks);
        request.setAttribute("unassignedTasks", unassignedTasks);
        request.setAttribute("tags", tags);
        request.setAttribute("users", allUsers);
        request.setAttribute("userToken", userToken);
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/Home.jsp").forward(request,response);
    }

    public void destroy() {

    }
}