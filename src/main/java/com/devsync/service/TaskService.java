package com.devsync.service;


import com.devsync.domain.entity.Tag;
import com.devsync.domain.entity.Task;
import com.devsync.domain.entity.Token;
import com.devsync.domain.entity.User;
import com.devsync.domain.enums.Role;
import com.devsync.domain.enums.TaskStatus;
import com.devsync.repository.Implementations.TagRepository;
import com.devsync.repository.Implementations.TaskRepository;
import com.devsync.repository.Implementations.TokenRepository;
import com.devsync.repository.Implementations.UserRepository;
import com.devsync.util.DateUtils;
import com.devsync.util.UserUtils;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class TaskService {


    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TokenRepository tokenRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public boolean addTask(Task task) {
        if(taskRepository.save(task)){
            return true;
        }
        return false;
    }

    public List<Task> getTasks(User user) {
        List<Task> allTasks =  taskRepository.findAll();

        if(user.getRole() == Role.INDIVIDUAL) {
            return allTasks.stream()
                .filter(t -> (t.getAssignee() != null && Objects.equals(t.getAssignee().getId(), user.getId()))
                    || Objects.equals(t.getCreatedBy().getId(), user.getId()))
                .collect(Collectors.toList());

        }
        else {
            return allTasks;
        }


    }

    public List<Task> getAllTasks () {
        return  taskRepository.findAll();
    }

    public List<Task> getUnassignedTasks() {
        List<Task> allTasks =  taskRepository.findAll();
        List<Task> unassignedTasks = allTasks.stream()
            .filter(t -> t.getAssignee() == null)
            .collect(Collectors.toList());

        return unassignedTasks;
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public boolean updateTaskAssignee(Task task, HttpSession session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = task.getDueDate();

        long daysBetween = ChronoUnit.DAYS.between(now, dueDate);
        if (daysBetween < 3) {
            session.setAttribute("error", "Cannot plan a task less than 3 days from now.");
            return false;
        }
        try {
            taskRepository.update(task);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTask(Task task, HttpSession session) {
        try {
            if (task.getDueDate().isBefore(LocalDateTime.now()) && task.getStatus() == TaskStatus.COMPLETED) {
                session.setAttribute("error", "Can't mark a task as completed if it's overdue.");
                return false;
            }

            taskRepository.update(task);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTask(Long taskId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Task task = taskRepository.findById(taskId);

        Token userToken = tokenRepository.findByUser(user);

        System.out.println(userToken);

        if(Objects.equals(task.getCreatedBy().getId(), user.getId())) {
/*
            System.out.println(task);
*/
            return taskRepository.delete(taskId);
        }
        else if(userToken.getMonthlyDeletionTokens() > 0) {
/*
            System.out.println(3);
*/
            taskRepository.delete(taskId);
            userToken.setMonthlyDeletionTokens(userToken.getMonthlyDeletionTokens() - 1);
            tokenRepository.update(userToken);
            return true;
        } else {
            session.setAttribute("error", "Not enough Tokens to Delete.");
            return false;

        }


    }

    public boolean changeTask(Long taskId,Long oldTaskId,Long userId, HttpSession session) {
        Task task = taskRepository.findById(taskId);
        Task oldTask = taskRepository.findById(oldTaskId);

        User user = userRepository.findById(userId);

        Token userToken = tokenRepository.findByUser(user);

        if(userToken.getDailyModificationTokens() > 0)
        {
            task.setAssignee(user);
            oldTask.setAssignee(null);
            oldTask.setRefused(true);
            oldTask.setChangeDate(LocalDateTime.now());

            taskRepository.update(task);
            taskRepository.update(oldTask);

            userToken.setDailyModificationTokens(userToken.getDailyModificationTokens() - 1);

            Token updatedToken = tokenRepository.update(userToken);

            return true;
        }
        else {
            session.setAttribute("error", "No Modification Tokens Left.");
            return false;
        }


    }

    public Map<Task, Long> getTasksWithRemainingDays() {
        List<Task> tasks = taskRepository.findAll();
        Map<Task, Long> taskRemainingDaysMap = new HashMap<>();

        for (Task task : tasks) {
            long remainingDays = DateUtils.calculateRemainingDays(task);
            taskRemainingDaysMap.put(task, remainingDays);
        }

        return taskRemainingDaysMap;
    }



    public Map<Tag, Double> calculateCompletePercentageByTag(List<Task> tasks, List<Tag> tags) {
       Map<Tag, Double> completePercentageMap = new HashMap<>();
        for(Tag tag : tags) {


        List<Task> tasksWithTag = tasks.stream()
            .filter(task -> task.getTags().contains(tag))
            .collect(Collectors.toList());

        long completedTasks = tasksWithTag.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();

       double percentage=  tasksWithTag.isEmpty() ? 0 : (double) completedTasks / tasksWithTag.size() * 100;

       completePercentageMap.put(tag, percentage);

        }

        return completePercentageMap;
    }









}
