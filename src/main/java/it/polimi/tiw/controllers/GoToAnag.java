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

@WebServlet(name = "anagraficaServelt", value = "/goToAnag")
public class GoToAnag extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public GoToAnag() {
        super();
    }

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
        if (errorMessage != null) {
            ctx.setVariable("errorMessage", errorMessage);
        }

        try {

            List<User> users = userDAO.getAllUsers(user.getUsername());

            if(users.isEmpty()) {
                ctx.setVariable("noUsersMessage", "Nessun utente trovato");
            } else {
                ctx.setVariable("users", users);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            ctx.setVariable("anagTableTitle", "Puoi invitare fino a " + g.getMax_parts() + " utenti");
        } catch (NumberFormatException e) {
            path = "/anagrafica.html";
            ctx.setVariable("errorMessage", "Errore: Numero di partecipanti non valido");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        templateEngine.process(path, ctx, response.getWriter());
    }

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
                // System.out.println(userId);
                usernames.add(username);
            }
        }


        System.out.println("Selected: " + selectedCount);

        while (errorCount < 2) {

            if(selectedCount < g.getMin_parts()) {
                errorCount++;
                System.out.println(errorCount);
                int delta = g.getMin_parts() - selectedCount;
                request.getSession().setAttribute("errorMessage", "Troppi pochi utenti selezionati, aggiungerne almeno " + delta);
            }

            if(selectedCount > g.getMax_parts()) {
                errorCount++;
                System.out.println(errorCount);
                int delta = selectedCount - g.getMax_parts();
                request.getSession().setAttribute("errorMessage", "Troppi utenti selezionati, eliminarne almeno " + delta);
            }

            if(selectedCount >= g.getMin_parts() && selectedCount <= g.getMax_parts()) {
                System.out.println("Gruppo in fase di creazione");
                GroupDAO groupDAO = new GroupDAO(connection);
                try {
                    usernames.add(user.getUsername());
                    groupDAO.createGroup(usernames, g, user.getUsername());
                    System.out.println("Gruppo creato con successo");
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
            templateEngine.process(path, ctx, response.getWriter());
        }

        if(selectedCount >= g.getMin_parts() && selectedCount <= g.getMax_parts()) {
            System.out.println("Gruppo in fase di creazione");
            GroupDAO groupDAO = new GroupDAO(connection);
            try {
                usernames.add(user.getUsername());
                groupDAO.createGroup(usernames, g, user.getUsername());
                System.out.println("Gruppo creato con successo");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            path = request.getContextPath() + "/goToHome";
            response.sendRedirect(path);
            return;
        }

        // You can now use the selectedCount for further processing
        request.setAttribute("selectedCount", selectedCount);

    }


    @Override
    public void destroy() {
        try{
            ConnectionManager.closeConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
