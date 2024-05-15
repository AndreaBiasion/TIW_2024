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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

        int min_part = session.getAttribute("min_x")==null?0:Integer.parseInt(session.getAttribute("min_x").toString());
        int max_part = session.getAttribute("max_x")==null?0:Integer.parseInt(session.getAttribute("max_x").toString());

        String path = "/anagrafica.html";
        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        // User user = (User) session.getAttribute("user");
        UserDAO userDAO = new UserDAO(connection);

        try {

            List<User> users = userDAO.getAllUsers();

            if(users.isEmpty()) {
                ctx.setVariable("noUsersMessage", "Nessun utente trovato");
            } else {
                ctx.setVariable("users", users);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            ctx.setVariable("anagTableTitle", "Puoi invitare fino a " + min_part + " utenti");
        } catch (NumberFormatException e) {
            path = "/anagrafica.html";
            ctx.setVariable("errorMessage", "Errore: Numero di partecipanti non valido");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
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
