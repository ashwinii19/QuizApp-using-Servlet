package com.techlabs.quizapp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            Map<Integer, HttpSession> loggedInUsers =
                    (Map<Integer, HttpSession>) getServletContext().getAttribute("loggedInUsers");

            String sql = "SELECT user_id, password FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next() && password.equals(rs.getString("password"))) {
                int userId = rs.getInt("user_id");

                if (loggedInUsers != null && loggedInUsers.containsKey(userId)) {
                    out.println("<script>alert('This user is already logged in from another browser/session'); location='index.html';</script>");
                    conn.close();
                    return;
                }

                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);
                session.setAttribute("username", username);

                if (loggedInUsers != null) {
                    loggedInUsers.put(userId, session);
                }

                response.sendRedirect("DashboardServlet");
            } else {
                out.println("<script>alert('Invalid Username or Password'); location='index.html';</script>");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
