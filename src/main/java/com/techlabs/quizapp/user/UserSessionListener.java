package com.techlabs.quizapp.user;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.Map;

@WebListener
public class UserSessionListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Map<Integer, HttpSession> loggedInUsers =
                (Map<Integer, HttpSession>) session.getServletContext().getAttribute("loggedInUsers");

        if (loggedInUsers != null) {
            Integer userId = (Integer) session.getAttribute("user_id");
            if (userId != null) {
                loggedInUsers.remove(userId);
                System.out.println("User removed from loggedInUsers: " + userId);
            }
        }
    }

}























