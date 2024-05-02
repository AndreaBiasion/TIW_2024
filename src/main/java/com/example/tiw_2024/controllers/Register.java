package com.example.tiw_2024.controllers;

import com.example.tiw_2024.dao.UserDAO;
import com.example.tiw_2024.utils.ConnectionManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This servlet class handles the registration process.
 * It provides methods for handling HTTP GET and POST requests related to registration.
 */
@WebServlet("/Register")
public class Register extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    /**
     * Default constructor.
     */
    public Register(){
        super();
    }

    /**
     * Initializes the servlet by establishing a database connection and configuring the template engine.
     * @throws ServletException if an error occurs during servlet initialization.
     */
    @Override
    public void init() throws ServletException {
        connection = ConnectionManager.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    /**
     * Handles HTTP GET requests for registration page.
     * @param req  the HttpServletRequest object containing the request parameters.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if an error occurs while processing the request.
     * @throws IOException      if an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = "/WEB-INF/register.html";
        resp.setContentType("text/html");
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        templateEngine.process(path, webContext, resp.getWriter());
    }

    /**
     * Handles HTTP POST requests for registration.
     * @param req  the HttpServletRequest object containing the request parameters.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if an error occurs while processing the request.
     * @throws IOException      if an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Implementation for handling registration POST requests goes here
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String repeatPassword = req.getParameter("repeatPassword");

        // check if password are matching
        if (!password.equals(repeatPassword)) {
            resp.sendError(400, "Passwords do not match");
        }

        String path;
        if(name == null || surname == null || email == null){
            // invalid fields
            ServletContext servletContext = getServletContext();
            final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
            webContext.setVariable("nameReceived", (name != null ? name : ""));
            webContext.setVariable("surnameReceived", ( surname != null ? surname : ""));
            webContext.setVariable("emailReceived", (email != null ? email : ""));
            webContext.setVariable("passwordReceived", password);
            webContext.setVariable("repeatPassword", (repeatPassword != null ? repeatPassword : ""));
            webContext.setVariable("errorMessageReceived", "Some fields are missing");
            path = "/WEB-INF/register.html";
            templateEngine.process(path, webContext, resp.getWriter());
        } else {

            // create new user
            UserDAO userDAO = new UserDAO(connection);

            try {
                userDAO.addUser(name, surname, email, password);
            } catch (SQLException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached");
            }

            path = getServletContext().getContextPath() + "/login.html";
            resp.sendRedirect(path);
        }
    }

    /**
     * Cleans up resources used by the servlet.
     */
    @Override
    public void destroy() {
        try {
            ConnectionManager.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
