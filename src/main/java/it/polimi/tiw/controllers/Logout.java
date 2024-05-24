package it.polimi.tiw.controllers;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet implementation class Logout
 * Handles user logout by invalidating the session and redirecting to the login page.
 */
@WebServlet(name = "logoutServlet", value = "/logout")
public class Logout extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     * Default constructor.
     */
    public Logout() {
        super();
    }

    /**
     * Handles GET requests for user logout.
     * Invalidates the current session if it exists and redirects to the login page.
     *
     * @param req  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param resp the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws IOException if an input or output error is detected when the servlet handles the GET request
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        String path = getServletContext().getContextPath() + "/login.html";
        resp.sendRedirect(path);
    }

    /**
     * Handles POST requests by forwarding them to the doGet method.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws IOException if an input or output error is detected when the servlet handles the POST request
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
