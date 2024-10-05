package utils;

import constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    // Retrieves the username from the session
    public static String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    // Retrieves user role from the session, if available
    public static String getUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USER_ROLE) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    // Retrieves engine-specific attributes from the session
    public static String getEngineState(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.ENGINE_STATE) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    // Clears the session completely (both user and engine data)
    public static void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // Clears specific session attributes (e.g., username, engine state)
    public static void clearSpecificAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.USERNAME);
            session.removeAttribute(Constants.ENGINE_STATE);
            // Add other attributes you want to clear as needed
        }
    }
}
