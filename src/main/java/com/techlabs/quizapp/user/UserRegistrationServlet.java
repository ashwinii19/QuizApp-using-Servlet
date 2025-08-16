package com.techlabs.quizapp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/UserRegistrationServlet")
public class UserRegistrationServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password"); 

        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String chk = "SELECT 1 FROM users WHERE username=? OR email=?";
                    try (PreparedStatement ps = conn.prepareStatement(chk)) {
                        ps.setString(1, username);
                        ps.setString(2, email);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                out.println("<script>alert('Username or Email already exists');location='register.html';</script>");
                                return;
                            }
                        }
                    }
                    String ins = "INSERT INTO users (username, email, password) VALUES (?,?,?)";
                    try (PreparedStatement ps = conn.prepareStatement(ins)) {
                        ps.setString(1, username);
                        ps.setString(2, email);
                        ps.setString(3, password);
                        ps.executeUpdate();
                    }
                    out.println("<script>alert('Registration successful. Please login.');location='index.html';</script>");
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<h3>Error: " + e.getMessage() + "</h3>");
            }
        }
    }
}
