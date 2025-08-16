package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DeleteQuestionServlet")
public class DeleteQuestionServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String id = request.getParameter("id");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Delete related user answers first
            String deleteAnswers = "DELETE FROM user_answers WHERE question_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(deleteAnswers);
            ps1.setInt(1, Integer.parseInt(id));
            ps1.executeUpdate();

            // Delete question
            String deleteQuestion = "DELETE FROM questions WHERE question_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(deleteQuestion);
            ps2.setInt(1, Integer.parseInt(id));
            ps2.executeUpdate();

            conn.close();
            response.sendRedirect("ViewQuestionsServlet");
        } catch(Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
