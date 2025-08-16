package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AddQuestionServlet")
public class AddQuestionServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String qText = request.getParameter("question_text");
        String optA = request.getParameter("option_a");
        String optB = request.getParameter("option_b");
        String optC = request.getParameter("option_c");
        String optD = request.getParameter("option_d");
        String correct = request.getParameter("correct_option").toUpperCase();
        String section = request.getParameter("section"); 

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, section) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, qText);
            ps.setString(2, optA);
            ps.setString(3, optB);
            ps.setString(4, optC);
            ps.setString(5, optD);
            ps.setString(6, correct);
            ps.setString(7, section);  

            int rows = ps.executeUpdate();
            if (rows > 0) {
                out.println("<script>alert('Question Added Successfully'); window.location='admin_dashboard.html';</script>");
            } else {
                out.println("<script>alert('Failed to Add Question'); window.location='add_question.html';</script>");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
