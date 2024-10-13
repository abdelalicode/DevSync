package com.devsync.controller;


import com.devsync.domain.entity.Tag;
import com.devsync.domain.entity.Task;
import com.devsync.domain.entity.User;
import com.devsync.domain.enums.TaskStatus;
import com.devsync.repository.Implementations.TagRepository;
import com.devsync.repository.Implementations.TaskRepository;
import com.devsync.repository.Implementations.TokenRepository;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.service.TagService;
import com.devsync.service.TaskService;
import com.devsync.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TaskServlet", value = "/task")
public class TaskServlet extends HttpServlet {
    private TaskService taskService;
    private TagService tagService;
    private UserService userService;


    public void init() {
        TaskRepository taskRepository = new TaskRepository();
        TagRepository tagRepository = new TagRepository();
        UserRepository userRepository = new UserRepository();
        TokenRepository tokenRepository = new TokenRepository();
        this.taskService = new TaskService(taskRepository,userRepository,tokenRepository);
        this.tagService = new TagService(tagRepository);
        this.userService = new UserService(userRepository, tokenRepository);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        switch (action) {
            case "update":
                updateTaskStatus(request, response,session);
                break;
            case "delete":
                deleteTask(request, response, session);
                break;
            case "updateAssignee":
                updateTaskAssignee(request, response, session);
                break;
            case "changeTask":
                changeTask(request, response, session);
                break;
            default:
                addNewTask(request, response, session);
                break;
        }


    }

    private void addNewTask(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
        User authUser = (User) session.getAttribute("user");

        request.getSession().removeAttribute("error");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        LocalDateTime dueDate = LocalDateTime.parse(request.getParameter("dueDate"));
        String[] selectedTags = request.getParameterValues("selectedTags");


        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(authUser);
        List<Tag> tags = new ArrayList<>();

        for (String tagId : selectedTags) {
            Tag tag = tagService.getTagById(Long.parseLong(tagId));
            tags.add(tag);
        }

        task.setTags(tags);



        if(taskService.addTask(task)) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws IOException, ServletException {
        String taskIdParam = request.getParameter("id");
        String statusParam = request.getParameter("status");

            Long taskId = Long.parseLong(taskIdParam);

        Task task = taskService.getTaskById(taskId);

        TaskStatus status = TaskStatus.valueOf(statusParam.toUpperCase());
        task.setStatus(status);


        if (taskService.updateTask(task ,session)) {
            response.sendRedirect(request.getContextPath() + "/home");
            session.removeAttribute("error");
        }
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
        Long taskId = Long.parseLong(request.getParameter("taskId"));

        taskService.deleteTask(taskId,session);
        response.sendRedirect(request.getContextPath() + "/home");

    }

    private void updateTaskAssignee(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String userIdParam = request.getParameter("user_id");
        String taskIdParam = request.getParameter("task_id");

        Long taskId = Long.parseLong(taskIdParam);
        Task task = taskService.getTaskById(taskId);

        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            task.setAssignee(null);
        } else {
            Long userId = Long.parseLong(userIdParam);
            User user = userService.getUserById(userId);
            task.setAssignee(user);
        }

        if(taskService.updateTaskAssignee(task, session)){
            response.sendRedirect(request.getContextPath() + "/home");
        }



    }

    private void changeTask(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String userIdParam = request.getParameter("user_id");
        String taskIdParam = request.getParameter("task_id");
        String oldtaskIdParam = request.getParameter("oldTask_id");

        Long taskId = Long.parseLong(taskIdParam);
        Long oldTaskId = Long.parseLong(oldtaskIdParam);
        Long userId = Long.parseLong(userIdParam);

        if(taskService.changeTask(taskId,oldTaskId,userId, session)) {
            response.sendRedirect(request.getContextPath() + "/home");
        }



    }


    public void destroy() {
    }


}