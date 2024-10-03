package com.devsync.controller;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/allo")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] names = {"ABdelali", "TAHA", "Yow", "Smoalih"};
        request.setAttribute("message", message);
        request.setAttribute("names", names);
        this.getServletContext().getRequestDispatcher("/WEB-INF/test.jsp").forward(request,response);
    }

    public void destroy() {
    }
}