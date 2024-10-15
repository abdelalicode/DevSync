package com.devsync.controller;

import com.devsync.domain.entity.Tag;
import com.devsync.domain.entity.Task;
import com.devsync.domain.entity.Token;
import com.devsync.domain.entity.User;
import com.devsync.domain.enums.Role;
import com.devsync.repository.Implementations.TagRepository;
import com.devsync.repository.Implementations.TaskRepository;
import com.devsync.repository.Implementations.TokenRepository;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.service.TagService;
import com.devsync.service.TaskService;
import com.devsync.service.TokenService;
import com.devsync.service.UserService;
import com.devsync.util.UserUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StatsServlet", value = "/stats")
public class StatsServlet extends HttpServlet {
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

        User AuthUser = (User) session.getAttribute("user");

        List<Task> tasks = taskService.getTasks(AuthUser);
        List<Tag> tags = tagService.getTags();

        Map<Tag, Double> CompletPerTag = taskService.calculateCompletePercentageByTag(tasks, tags);
        request.setAttribute("CompletPerTag", CompletPerTag);
        request.setAttribute("tags", tags);
        request.setAttribute("user", AuthUser);
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/Stats.jsp").forward(request,response);
    }

    public void destroy() {

    }
}