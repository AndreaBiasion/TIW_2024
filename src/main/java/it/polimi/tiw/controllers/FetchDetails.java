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
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Servlet implementation class FetchDetails
 * Handles requests to fetch details of a specific group and displays them on a details page.
 */
@WebServlet(name = "FetchDetails", value = "/fetchDetails")
public class FetchDetails extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     * Default constructor.
     */
    public FetchDetails() {
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
     * Handles GET requests to fetch details of a specific group.
     * Validates the group ID, retrieves group details and participants, and displays them on the details page.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String path;
        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        int IDGroup;

        try {
            IDGroup = Integer.parseInt(request.getParameter("IDGroup"));
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "IDGroup mancante o vuoto");
            return;
        }

        GroupDAO groupDAO = new GroupDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        try {
            // Retrieves group by ID
            Group group = groupDAO.getGroupById(IDGroup);

            // Retrieves user from the session
            User user = (User) session.getAttribute("user");

            // List of participants
            List<User> usersList = userDAO.getUsersFromGroup(IDGroup);

            // If the specified group isn't found, redirects to the homepage
            if (group == null) {
                path = request.getContextPath() + "/goToHome";
                response.sendRedirect(path);
                return;
            }

            // Define the desired date format (year, month, day)
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            // Format the java.sql.Date object
            String formattedDate = formatter.format(group.getDate_creation());

            ctx.setVariable("groupTitle", group.getTitle());
            ctx.setVariable("creationDate", "Creato il " + formattedDate);
            ctx.setVariable("durataAtt", "Durata attivita': " + group.getActivity_duration() + " giorni");
            ctx.setVariable("users", usersList);
            path = "/dettagli.html";
            templateEngine.process(path, ctx, response.getWriter());

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL Error: Impossibile ottenere i dettagli");
        }
    }

    /**
     * Handles POST requests by delegating to the doGet method.
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
