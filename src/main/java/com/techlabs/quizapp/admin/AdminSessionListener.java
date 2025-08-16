package com.techlabs.quizapp.admin;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.Map;

@WebListener
public class AdminSessionListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Map<String, HttpSession> loggedInAdmins =
                (Map<String, HttpSession>) session.getServletContext().getAttribute("loggedInAdmins");

        if (loggedInAdmins != null) {
            String username = (String) session.getAttribute("admin_username");
            if (username != null) {
                loggedInAdmins.remove(username);
                System.out.println("Admin removed from loggedInAdmins: " + username);
            }
        }
    }

}
