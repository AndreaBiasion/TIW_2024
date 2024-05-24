package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Group;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.GroupDAO;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class GoToAnag
 * Handles requests to navigate to the anagraphic page, where users can invite other users to groups.
 */
@WebServlet(name = "anagraficaServelt", value = "/goToAnag")
public class GoToAnag extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     * Default constructor.
     */
    public GoToAnag() {
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
     * Handles GET requests to navigate to the anagraphic page.
     * Fetches users for the logged-in user and displays them on the anagraphic page.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Group g = (Group) session.getAttribute("group");
        String path = "/anagrafica.html";
        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        User user = (User) session.getAttribute("user");
        UserDAO userDAO = new UserDAO(connection);

        String errorMessage = (String) session.getAttribute("errorMessage");

        // Retrieve previous selection from the session
        List<String> selectedUsers = (List<String>) session.getAttribute("selectedUsers");
        System.out.println("Selected users: " + selectedUsers);

        if (selectedUsers != null) {
            ctx.setVariable("selectedUsers", selectedUsers);
        }

        if (errorMessage != null) {
            ctx.setVariable("errorMessage", errorMessage);
        }

        try {
            List<User> users = userDAO.getAllUsers(user.getUsername());

            if (users.isEmpty()) {
                ctx.setVariable("noUsersMessage", "Nessun utente trovato");
            } else {
                ctx.setVariable("users", users);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (g.getMin_parts() == g.getMax_parts()) {
                ctx.setVariable("anagTableTitle", "Devi invitare " + g.getMax_parts() + " utenti");
            } else {
                ctx.setVariable("anagTableTitle", "Puoi invitare fino a " + g.getMax_parts() + " utenti");
            }
        } catch (NumberFormatException e) {
            path = "/anagrafica.html";
            ctx.setVariable("errorMessage", "Errore: Numero di partecipanti non valido");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        templateEngine.process(path, ctx, response.getWriter());
    }

    /**
     * Handles POST requests for selecting users to invite to a group.
     * Validates the number of selected users and processes the group creation.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the POST request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        Group g = (Group) session.getAttribute("group");

        Integer errorCount = (Integer) session.getAttribute("errorCount");
        if (errorCount == null) {
            errorCount = 0;
        }

        String path = "/anagrafica.html";
        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        String[] selectedUsers = request.getParameterValues("selectedUsers");
        List<String> usernames = new ArrayList<>();
        int selectedCount = 0;
        if (selectedUsers != null) {
            selectedCount = selectedUsers.length;
            for (String username : selectedUsers) {
                usernames.add(username);
            }
        }

        while (errorCount < 2) {
            if (selectedCount < g.getMin_parts()) {
                errorCount++;
                int delta = g.getMin_parts() - selectedCount;
                request.getSession().setAttribute("errorMessage", "Troppi pochi utenti selezionati, aggiungerne almeno " + delta);
                request.getSession().setAttribute("selectedUsers", usernames);
            }

            if (selectedCount > g.getMax_parts()) {
                errorCount++;
                int delta = selectedCount - g.getMax_parts();
                request.getSession().setAttribute("errorMessage", "Troppi utenti selezionati, eliminarne almeno " + delta);
                request.getSession().setAttribute("selectedUsers", usernames);
            }

            if (selectedCount >= g.getMin_parts() && selectedCount <= g.getMax_parts()) {
                GroupDAO groupDAO = new GroupDAO(connection);
                try {
                    usernames.add(user.getUsername());
                    groupDAO.createGroup(usernames, g, user.getUsername());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                path = request.getContextPath() + "/goToHome";
                response.sendRedirect(path);
                return;
            }

            path = request.getContextPath() + "/goToAnag";
            request.getSession().setAttribute("errorCount", errorCount);
            response.sendRedirect(path);
            return;
        }

        if (selectedCount < g.getMin_parts() || selectedCount > g.getMax_parts()) {
            path = "/cancellazione.html";
            request.getSession().setAttribute("errorCount", 0);
            request.getSession().setAttribute("errorMessage", null);
            request.getSession().setAttribute("selectedUsers", null);
            templateEngine.process(path, ctx, response.getWriter());
        }

        if (selectedCount >= g.getMin_parts() && selectedCount <= g.getMax_parts()) {
            GroupDAO groupDAO = new GroupDAO(connection);
            try {
                usernames.add(user.getUsername());
                groupDAO.createGroup(usernames, g, user.getUsername());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            request.getSession().setAttribute("errorCount", 0);
            request.getSession().setAttribute("errorMessage", null);
            request.getSession().setAttribute("selectedUsers", null);
            path = request.getContextPath() + "/goToHome";
            response.sendRedirect(path);
            return;
        }

        request.setAttribute("selectedCount", selectedCount);
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
