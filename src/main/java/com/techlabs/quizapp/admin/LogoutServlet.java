package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {

            Map<Integer, HttpSession> loggedInUsers =
                (Map<Integer, HttpSession>) getServletContext().getAttribute("loggedInUsers");
            Integer userId = (Integer) session.getAttribute("user_id");
            if (userId != null && loggedInUsers != null) {
                loggedInUsers.remove(userId);
            }

            Map<String, HttpSession> loggedInAdmins =
                (Map<String, HttpSession>) getServletContext().getAttribute("loggedInAdmins");
            String adminUsername = (String) session.getAttribute("admin_username");
            if (adminUsername != null && loggedInAdmins != null) {
                loggedInAdmins.remove(adminUsername);
            }

            session.invalidate();
        }

        response.sendRedirect("index.html"); 
    }
}
