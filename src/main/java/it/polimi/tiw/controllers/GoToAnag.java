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

        String title = request.getParameter("title");

        int durata;
        int min_part;
        int max_part;

        String path;


        try {
            durata = Integer.parseInt(request.getParameter("durata"));
            min_part = Integer.parseInt(request.getParameter("min_part"));
            max_part = Integer.parseInt(request.getParameter("max_part"));
        } catch (NumberFormatException e) {
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Parametri scorretti");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        if(min_part <=1 || min_part > max_part || durata < 0 || title.isEmpty() || title == null) {
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Parametri scorretti");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }
        User user = (User) session.getAttribute("user");
        UserDAO userDAO = new UserDAO(connection);

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


        Group g = new Group();
        g.setTitle(title);
        g.setActivity_duration(durata);
        g.setMin_parts(min_part);
        g.setMax_parts(max_part);

        min_part--;
        max_part--;



        path = "/anagrafica.html";
        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());



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

        if (users.isEmpty()) {
            ctx.setVariable("noUsersMessage", "Nessun utente trovato");
        } else {
            ctx.setVariable("users", users);
        }

        try {
            if (g.getMin_parts() == g.getMax_parts()) {
                ctx.setVariable("anagTableTitle", "Devi invitare " + max_part + " utenti");
            } else {
                ctx.setVariable("anagTableTitle", "Puoi invitare da " + min_part + " a " + max_part + " utenti");
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

        int durata;
        int min_part;
        int max_part;

        String title = request.getParameter("title");

        String path;

        try {
            durata = Integer.parseInt(request.getParameter("durata"));
            min_part = Integer.parseInt(request.getParameter("min_part"));
            max_part = Integer.parseInt(request.getParameter("max_part"));
        }catch (NumberFormatException | NullPointerException e){
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Parametri scorretti");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        if(title == null || title.isEmpty()){
            path = "/goToHome";
            request.setAttribute("errorMessage", "Errore: Titolo nullo");
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        Group g = new Group();
        g.setTitle(title);
        g.setActivity_duration(durata);
        g.setMin_parts(min_part);
        g.setMax_parts(max_part);

        // escludo il creatore del gruppo
        min_part--;
        max_part--;

        Integer errorCount = (Integer) session.getAttribute("errorCount");
        if (errorCount == null) {
            errorCount = 0;
        }

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
            if (selectedCount < min_part) {
                errorCount++;
                int delta = min_part - selectedCount;
                request.getSession().setAttribute("errorMessage", "Troppi pochi utenti selezionati, aggiungerne almeno " + delta);
                request.getSession().setAttribute("selectedUsers", usernames);
            }

            if (selectedCount > max_part) {
                errorCount++;
                int delta = selectedCount - max_part;
                request.getSession().setAttribute("errorMessage", "Troppi utenti selezionati, eliminarne almeno " + delta);
                request.getSession().setAttribute("selectedUsers", usernames);
            }

            if (selectedCount >= min_part && selectedCount <= max_part) {
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

            path = getServletContext().getContextPath() + "/goToAnag?title=" + title +
                    "&durata=" + durata +
                    "&min_part=" + g.getMin_parts() +
                    "&max_part=" + g.getMax_parts();
            request.getSession().setAttribute("errorCount", errorCount);
            response.sendRedirect(path);
            return;
        }

        if (selectedCount < min_part || selectedCount > max_part) {
            path = "/cancellazione.html";
            request.getSession().removeAttribute("errorCount");
            request.getSession().removeAttribute("errorMessage");
            request.getSession().removeAttribute("selectedUsers");
            templateEngine.process(path, ctx, response.getWriter());
        }

        if (selectedCount >= min_part && selectedCount <= max_part) {
            GroupDAO groupDAO = new GroupDAO(connection);
            try {
                usernames.add(user.getUsername());
                groupDAO.createGroup(usernames, g, user.getUsername());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            request.getSession().removeAttribute("errorCount");
            request.getSession().removeAttribute("errorMessage");
            request.getSession().removeAttribute("selectedUsers");
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
