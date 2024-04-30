package com.example.tiw_2024.controllers;

import com.example.tiw_2024.utils.ConnectionManager;
import org.thymeleaf.TemplateEngine;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;

public class Register extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public Register(){
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionManager.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();

    }
}
