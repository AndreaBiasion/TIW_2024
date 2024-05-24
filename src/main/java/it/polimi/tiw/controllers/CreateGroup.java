package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Group;
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

        String path = "/group.html";
        ServletContext context = getServletContext();
        final WebContext ctx = new WebContext(request,response,context,request.getLocale());

        templateEngine.process(path, ctx, response.getWriter());

    }

    /**
     * Handles HTTP POST requests for registration.
     * @throws ServletException if an error occurs while processing the request.
     * @throws IOException      if an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String title = request.getParameter("title");
        String durataStr = request.getParameter("durata_att");
        String minPartStr = request.getParameter("min_part");
        String maxPartStr = request.getParameter("max_part");

        String path;
        ServletContext context = getServletContext();
        final WebContext ctx = new WebContext(request, response, context, request.getLocale());




        if (title == null || title.isEmpty()) {
            path = "/group.html";
            ctx.setVariable("errorMessage", "Errore: Titolo mancante");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        int min_part;
        int max_part;

        try {
            min_part = Integer.parseInt(minPartStr);
            max_part = Integer.parseInt(maxPartStr);
        } catch (NumberFormatException e) {
            path = "/group.html";
            ctx.setVariable("errorMessage", "Errore: Numero di partecipanti non valido");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        int durata = Integer.parseInt(durataStr);

        if(durata <= 0) {
            path = "/group.html";
            ctx.setVariable("errorMessage", "Errore: Numero di giorni errato");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        if (min_part > max_part) {
            path = "/group.html";
            ctx.setVariable("errorMessage", "Errore: Numero di partecipanti errato");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        if (min_part < 1) {
            path = "/group.html";
            ctx.setVariable("errorMessage", "Errore: Almeno 2 partecipanti devono esserci");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        path = getServletContext().getContextPath() + "/goToAnag";
        Group group = new Group();
        group.setTitle(title);
        group.setActivity_duration(durata);
        group.setMin_parts(min_part);
        group.setMax_parts(max_part);
        request.getSession().setAttribute("group", group);
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
