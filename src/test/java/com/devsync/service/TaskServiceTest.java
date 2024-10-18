package com.devsync.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.devsync.domain.entity.*;
import com.devsync.domain.enums.Role;
import com.devsync.domain.enums.TaskStatus;
import com.devsync.repository.Implementations.*;
import com.devsync.service.TaskService;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TaskRequestRepository taskRequestRepository;
    @Mock
    private HttpSession httpSession;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository, userRepository, tokenRepository, taskRequestRepository);
    }

    @Test
    void testAddTask() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(true);

        boolean result = taskService.addTask(task);

        assertTrue(result);
        verify(taskRepository).save(task);
    }

    @Test
    void testGetTasks_IndividualUser() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.INDIVIDUAL);

        Task task1 = new Task();
        task1.setAssignee(user);
        Task task2 = new Task();
        task2.setCreatedBy(user);
        Task task3 = new Task();

        List<Task> allTasks = Arrays.asList(task1, task2, task3);
        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> result = taskService.getTasks(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
    }



    @Test
    void testUpdateTaskAssignee_Success() {
        Task task = new Task();
        task.setDueDate(LocalDateTime.now().plusDays(5));

        boolean result = taskService.updateTaskAssignee(task, httpSession);

        assertTrue(result);
        verify(taskRepository).update(task);
    }

    @Test
    void testUpdateTaskAssignee_Failure() {
        Task task = new Task();
        task.setDueDate(LocalDateTime.now().plusDays(2));

        boolean result = taskService.updateTaskAssignee(task, httpSession);

        assertFalse(result);
        verify(httpSession).setAttribute("error", "Cannot plan a task less than 3 days from now.");
        verify(taskRepository, never()).update(task);
    }

    @Test
    void testUpdateTask_Success() {
        Task task = new Task();
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setStatus(TaskStatus.COMPLETED);

        boolean result = taskService.updateTask(task, httpSession);

        assertTrue(result);
        verify(taskRepository).update(task);
    }

    @Test
    void testUpdateTask_Failure() {
        Task task = new Task();
        task.setDueDate(LocalDateTime.now().minusDays(1));
        task.setStatus(TaskStatus.COMPLETED);

        boolean result = taskService.updateTask(task, httpSession);

        assertFalse(result);
        verify(httpSession).setAttribute("error", "Can't mark a task as completed if it's overdue.");
        verify(taskRepository, never()).update(task);
    }

    @Test
    void testDeleteTask_CreatedByUser() {
        Long taskId = 1L;
        User user = new User();
        user.setId(1L);
        Task task = new Task();
        task.setCreatedBy(user);

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(task);
        when(taskRepository.delete(taskId)).thenReturn(true);

        boolean result = taskService.deleteTask(taskId, httpSession);

        assertTrue(result);
        verify(taskRepository).delete(taskId);
    }

    @Test
    void testDeleteTask_WithTokens() {
        Long taskId = 1L;
        User user = new User();
        user.setId(1L);
        Task task = new Task();
        task.setCreatedBy(new User());  // Different user
        Token token = new Token();
        token.setMonthlyDeletionTokens(1);

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(task);
        when(tokenRepository.findByUser(user)).thenReturn(token);
        when(taskRepository.delete(taskId)).thenReturn(true);

        boolean result = taskService.deleteTask(taskId, httpSession);

        assertTrue(result);
        verify(taskRepository).delete(taskId);
        verify(tokenRepository).update(token);
        assertEquals(0, token.getMonthlyDeletionTokens());
    }
}