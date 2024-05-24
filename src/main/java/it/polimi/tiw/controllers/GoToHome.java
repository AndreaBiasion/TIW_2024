package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Group;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.GroupDAO;
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

/**
 * Servlet implementation class GoToHome
 * Handles requests to navigate to the home page, fetching user groups and invitations from the database.
 */
@WebServlet(name = "GoToHomeServlet", value = "/goToHome")
public class GoToHome extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     * Default constructor.
     */
    public GoToHome() {
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
     * Handles GET requests to navigate to the home page.
     * Fetches the groups and invitations for the logged-in user and displays them on the home page.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String path = "/home.html";
        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        User user = (User) session.getAttribute("user");
        GroupDAO groupDAO = new GroupDAO(connection);

        try {
            List<Group> groups = groupDAO.getGroupsByUsername(user.getUsername());
            List<Group> groupsInvited = groupDAO.getGroupsByUsernameInvited(user.getUsername());

            if (groups.isEmpty()) {
                ctx.setVariable("noGroupsMessage", "Nessun gruppo trovato");
            }

            if (groupsInvited.isEmpty()) {
                ctx.setVariable("noGroupsInvitedMessage", "Nessun gruppo trovato");
            }

            ctx.setVariable("groups", groups);
            ctx.setVariable("groupsInvited", groupsInvited);
            templateEngine.process(path, ctx, response.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles POST requests by forwarding them to the doGet method.
     *
     * @param req  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param resp the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the POST request
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
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
