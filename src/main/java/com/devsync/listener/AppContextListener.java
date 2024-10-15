package com.devsync.listener;

import com.devsync.TaskScheduler;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private TaskScheduler taskScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        taskScheduler = new TaskScheduler();
        taskScheduler.start();
        System.out.println("Task Scheduler started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (taskScheduler != null) {
            taskScheduler.shutdown();
        }
        System.out.println("Task Scheduler stopped.");
    }
}
