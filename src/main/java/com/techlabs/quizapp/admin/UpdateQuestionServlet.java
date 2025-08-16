package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateQuestionServlet")
public class UpdateQuestionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String USER = "root";
    private static final String PASSWORD = "admin#123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        int id = Integer.parseInt(request.getParameter("question_id"));
        String questionText = request.getParameter("question_text");
        String optionA = request.getParameter("option_a");
        String optionB = request.getParameter("option_b");
        String optionC = request.getParameter("option_c");
        String optionD = request.getParameter("option_d");
        String correctOption = request.getParameter("correct_option");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "UPDATE questions SET question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_option=? WHERE question_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, questionText);
            ps.setString(2, optionA);
            ps.setString(3, optionB);
            ps.setString(4, optionC);
            ps.setString(5, optionD);
            ps.setString(6, correctOption);
            ps.setInt(7, id);

            int rows = ps.executeUpdate();

            out.println("<html><head>");
            out.println("<title>Update Question</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head><body>");
            out.println("<div class='page-wrapper'>");

            if (rows > 0) {
                out.println("<h2>Question Updated Successfully!</h2>");
                out.println("<p>The question has been updated in the database.</p>");
                out.println("<a href='ViewQuestionsServlet' class='back-link'>Back to Questions</a>");
            } else {
                out.println("<h2>Error!</h2>");
                out.println("<p>Failed to update the question. Please try again.</p>");
                out.println("<a href='ViewQuestionsServlet' class='back-link'>Back to Questions</a>");
            }

            out.println("</div></body></html>");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><head>");
            out.println("<title>Error</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head><body>");
            out.println("<div class='page-wrapper'>");
            out.println("<h2>Error!</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='ViewQuestionsServlet' class='back-link'>Back to Questions</a>");
            out.println("</div></body></html>");
        }
    }
}
