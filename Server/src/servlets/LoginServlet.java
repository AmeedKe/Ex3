package servlets;


import java.io.IOException;

import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;

import utils.SessionUtils;



public class LoginServlet extends HttpServlet {

    private final String DASHBOARD_URL = "/dashboard/dashboard.html";
    private final String SIGN_UP_URL = "/signup/signup.html";
    private final String LOGIN_ERROR_URL = "/error/login_attempt_after_error.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Get username from session
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) {
            // User not logged in
            String usernameFromParameter = request.getParameter(Constants.USERNAME);

            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                // No username in session or parameter
                response.sendRedirect(SIGN_UP_URL);
            } else {
                usernameFromParameter = usernameFromParameter.trim();

                // Synchronized block to avoid race conditions
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please try a different username.";
                        request.setAttribute("USER_NAME_ERROR", errorMessage);
                        getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                    } else {
                        // Add the new user and store in session
                        userManager.addUser(usernameFromParameter);
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);

                        // Redirect to dashboard or engine page
                        response.sendRedirect(DASHBOARD_URL);
                    }
                }
            }
        } else {
            // User already logged in, redirect to dashboard
            response.sendRedirect(DASHBOARD_URL);
        }
    }

    // Override doGet method to handle GET requests
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Override doPost method to handle POST requests
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Login servlet for user authentication";
    }
}
