package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Group;
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
import java.util.List;

@WebServlet(name = "createGroupServlet", value = "/createGroup")
public class CreateGroup extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    private String message;

    /**
     * Default constructor.
     */
    public CreateGroup(){
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
        String path = getServletContext().getContextPath() + "/goToHome";
        response.sendRedirect(path);
    }

    /**
     * Handles HTTP POST requests for registration.
     * @throws ServletException if an error occurs while processing the request.
     * @throws IOException      if an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String title = request.getParameter("title");
        String durataStr = request.getParameter("durata_att");
        String minPartStr = request.getParameter("min_part");
        String maxPartStr = request.getParameter("max_part");

        String path;

        if (title == null || title.isEmpty()) {
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Titolo non valido");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        int min_part;
        int max_part;
        int durata;
        try {
            min_part = Integer.parseInt(minPartStr);
            max_part = Integer.parseInt(maxPartStr);
            durata = Integer.parseInt(durataStr);
        } catch (NumberFormatException e) {
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: parametri non validi");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO(connection);
        User user = (User) request.getSession().getAttribute("user");
        String username = user.getUsername();
        List<User> users;

        try {
           users = userDAO.getAllUsers(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(min_part -1 > users.size()){
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Hai inserito un numero minimo troppo alto");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        if(min_part < 1 || max_part < 1){
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Numero di partecipanti non valido");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }
        if (durata < 1) {
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Durata non valida");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        if (min_part > max_part) {
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: minimo non puo' essere maggiore del massimo");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        path = getServletContext().getContextPath() + "/goToAnag?title=" + title +
                "&durata=" + durata +
                "&min_part=" + min_part +
                "&max_part=" + max_part;
        response.sendRedirect(path);

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
