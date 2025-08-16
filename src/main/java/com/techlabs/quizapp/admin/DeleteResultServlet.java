package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DeleteResultServlet")
public class DeleteResultServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("ViewResultsServlet");
            return;
        }

        int id = Integer.parseInt(idStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "DELETE FROM results WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            conn.close();

            response.sendRedirect("ViewResultsServlet");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3>Error deleting result: " + e.getMessage() + "</h3>");
        }
    }
}
