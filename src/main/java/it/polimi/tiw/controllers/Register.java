package it.polimi.tiw.controllers;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This servlet class handles the registration process.
 * It provides methods for handling HTTP GET and POST requests related to registration.
 */
@WebServlet(name = "registerServlet", value = "/register")
public class Register extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    private String message;

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
        message = "working!";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    /**
     * Handles HTTP POST requests for registration.
     * @throws ServletException if an error occurs while processing the request.
     * @throws IOException      if an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String repassword = request.getParameter("repassword");

        String path;
        ServletContext context = getServletContext();
        final WebContext ctx = new WebContext(request, response, context, request.getLocale());

        // controllo password
        if(!password.equals(repassword)) {
            path = "/register.html";
            ctx.setVariable("errorMessage", "Errore: Password non coincidono");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }


        // controllo field
        if(email == null || password == null || name == null || surname == null || email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            path = "/register.html";
            ctx.setVariable("errorMsg", "Errore: Credenziali mancanti o nulle");
            templateEngine.process(path, ctx, response.getWriter());
        } else {

            UserDAO userDAO = new UserDAO(connection);

            boolean isDuplicate = false;
            try {
                isDuplicate = userDAO.checkRegister(email, username);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL error: impossibile controllare unicità dell'email");
            }


            if(isDuplicate) {
                ctx.setVariable("errorMessage", "L'email/username gia' in uso!");
            } else {
                try {
                    userDAO.addUser(username, name, surname, email, password);
                } catch (SQLException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile registrare l'utente");
                    return;
                }

                path = getServletContext().getContextPath() + "/login.html";
                response.sendRedirect(path);
            }


        }

        // error handling
        path = "/register.html";
        templateEngine.process(path, ctx, response.getWriter());


    }

    /**
     * Cleans up resources used by the servlet.
     */
    @Override
    public void destroy() {
        try{
            ConnectionManager.closeConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
