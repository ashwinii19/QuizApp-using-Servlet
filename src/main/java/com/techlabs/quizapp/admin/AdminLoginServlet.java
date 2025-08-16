package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("admin_username");
        String password = request.getParameter("admin_password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            Map<String, String> loggedInAdmins = 
                (Map<String, String>) getServletContext().getAttribute("loggedInAdmins");

            HttpSession currentSession = request.getSession();

            if (loggedInAdmins != null && loggedInAdmins.containsKey(username)) {
                String existingSessionId = loggedInAdmins.get(username);
                if (!existingSessionId.equals(currentSession.getId())) {
                    out.println("<script>alert('This admin is already logged in from another browser/session'); location='admin_login.html';</script>");
                    conn.close();
                    return;
                }
            }

            String sql = "SELECT admin_password FROM admins WHERE admin_username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next() && password.equals(rs.getString("admin_password"))) {
                currentSession.setAttribute("admin_username", username);

                if (loggedInAdmins != null) {
                    loggedInAdmins.put(username, currentSession.getId());
                }

                response.sendRedirect("admin_dashboard.html");
            } else {
                out.println("<script>alert('Invalid Username or Password'); location='admin_login.html';</script>");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Database Connection Error: " + e.getMessage() + "</h3>");
        }
    }
}

