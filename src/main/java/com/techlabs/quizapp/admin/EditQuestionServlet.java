package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EditQuestionServlet")
public class EditQuestionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String USER = "root";
    private static final String PASSWORD = "admin#123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String idStr = request.getParameter("id");
        if (idStr == null) {
            out.println("<h3>Error: Question ID is missing!</h3>");
            return;
        }

        int id = Integer.parseInt(idStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "SELECT * FROM questions WHERE question_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<html><head><title>Edit Question</title>");
                out.println("<style>");
                out.println("body { font-family: Arial; background: linear-gradient(135deg, #6a11cb, #2575fc); padding: 20px; }"); 
                out.println(".container { width: 500px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 4px 10px rgba(0,0,0,0.1);} ");
                out.println("input, select, textarea { width: 100%; margin: 8px 0; padding: 10px; border-radius: 5px; border: 1px solid #ccc; }");
                out.println("button { background: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; }");
                out.println("button:hover { background: #0056b3; }");
                out.println("</style></head><body>");

                out.println("<div class='container'>");
                out.println("<h2>Edit Question</h2>");
                out.println("<form method='post' action='UpdateQuestionServlet'>");

                out.println("<input type='hidden' name='question_id' value='" + rs.getInt("question_id") + "'/>");

                out.println("Question: <textarea name='question_text'>" + rs.getString("question_text") + "</textarea><br>");
                out.println("Option A: <input type='text' name='option_a' value='" + rs.getString("option_a") + "'/><br>");
                out.println("Option B: <input type='text' name='option_b' value='" + rs.getString("option_b") + "'/><br>");
                out.println("Option C: <input type='text' name='option_c' value='" + rs.getString("option_c") + "'/><br>");
                out.println("Option D: <input type='text' name='option_d' value='" + rs.getString("option_d") + "'/><br>");

                out.println("Correct Option: <select name='correct_option'>");
                String correct = rs.getString("correct_option");
                out.println("<option value='A' " + (correct.equals("A") ? "selected" : "") + ">A</option>");
                out.println("<option value='B' " + (correct.equals("B") ? "selected" : "") + ">B</option>");
                out.println("<option value='C' " + (correct.equals("C") ? "selected" : "") + ">C</option>");
                out.println("<option value='D' " + (correct.equals("D") ? "selected" : "") + ">D</option>");
                out.println("</select><br>");

                out.println("<button type='submit'>Update Question</button>");
                out.println("</form>");
                out.println("</div></body></html>");
            } else {
                out.println("<h3>No question found with ID: " + id + "</h3>");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
