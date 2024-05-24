package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionManager;
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
 * Servlet implementation class CheckLogin
 * Handles login requests by validating user credentials and redirecting to the home page on success.
 */
@WebServlet(name = "checkLoginServlet", value = "/login")
public class CheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     * Default constructor.
     */
    public CheckLogin() {
        super();
    }

    /**
     * Initializes the servlet, setting up the database connection and the Thymeleaf template engine.
     *
     * @throws ServletException if an initialization error occurs
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
     * Handles GET requests by responding with a simple message.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * Handles POST requests to validate user login credentials.
     * If credentials are valid, redirects to the home page. Otherwise, reloads the login page with an error message.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the POST request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        String path;

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            path = "login.html";
            ctx.setVariable("errorMessage", "Parametri mancanti");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        UserDAO userDAO = new UserDAO(connection);

        try {
            // Checking validity of credentials
            User user = userDAO.checkCredentials(email, password);

            if (user == null) {
                path = "/login.html";
                ctx.setVariable("errorMessage", "Email o password errati");
                templateEngine.process(path, ctx, response.getWriter());
            } else {
                path = request.getContextPath() + "/goToHome";
                request.getSession().setMaxInactiveInterval(300);
                request.getSession().setAttribute("user", user);
                response.sendRedirect(path);
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile validare le credenziali");
        }
    }

    /**
     * Closes the database connection when the servlet is destroyed.
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
