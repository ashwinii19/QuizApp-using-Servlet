package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ViewQuestionsServlet")
public class ViewQuestionsServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String section = request.getParameter("section");

        out.println("<html><head>");
        out.println("<title>View Questions</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<style>");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("th { padding: 12px; border: 1px solid #ddd; text-align: left; background-color: #007bff; color: white; }");
        out.println("td { padding: 12px; border: 1px solid #ddd; text-align: left; background-color: white; color: black; }");
        out.println("tr:nth-child(even) td { background-color: #f9f9f9; }"); 
        out.println(".btn-edit { background: #28a745; color: white; border: none; padding: 6px 12px; cursor: pointer; border-radius: 5px; }");
        out.println(".btn-edit:hover { background: #218838; }");
        out.println(".btn-delete { background: #dc3545; color: white; border: none; padding: 6px 12px; cursor: pointer; border-radius: 5px; }");
        out.println(".btn-delete:hover { background: #c82333; }");
        out.println(".back-link { display: inline-block; margin-top: 20px; text-decoration: none; color: #007bff; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='page-wrapper'>");

        if (section == null || section.trim().isEmpty()) {
            out.println("<h2>Select Section to View Questions</h2>");
            out.println("<form method='get' action='ViewQuestionsServlet'>");
            out.println("<label for='section'>Select Section:</label>");
            out.println("<select name='section' id='section' required>");
            out.println("<option value='' disabled selected>Select Section</option>");
            out.println("<option value='Java'>Java</option>");
            out.println("<option value='SQL'>SQL</option>");
            out.println("<option value='Servlet'>Servlet</option>");
            out.println("<option value='General Knowledge'>General Knowledge</option>");
            out.println("</select>");
            out.println("<button type='submit'>View</button>");
            out.println("</form>");
        } else {
            out.println("<h2>Questions in Section: <span style='color:#007bff'>" + section + "</span></h2>");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                String sql = "SELECT * FROM questions WHERE section = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, section);
                ResultSet rs = ps.executeQuery();

                out.println("<div class='scroll-box'>");
                out.println("<table>");
                out.println("<tr><th>ID</th><th>Question</th><th>Options</th><th>Correct</th><th colspan='2'>Action</th></tr>");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("question_id");
                    String question = rs.getString("question_text");
                    String optionA = rs.getString("option_a");
                    String optionB = rs.getString("option_b");
                    String optionC = rs.getString("option_c");
                    String optionD = rs.getString("option_d");
                    String correct = rs.getString("correct_option");

                    out.println("<tr>");
                    out.println("<td>" + id + "</td>");
                    out.println("<td>" + question + "</td>");
                    out.println("<td>");
                    out.println("A. " + optionA + "<br>");
                    out.println("B. " + optionB + "<br>");
                    out.println("C. " + optionC + "<br>");
                    out.println("D. " + optionD);
                    out.println("</td>");
                    out.println("<td>" + correct + "</td>");

                    // Delete Button
                    out.println("<td>");
                    out.println("<form method='get' action='DeleteQuestionServlet' onsubmit=\"return confirm('Delete this question?');\">");
                    out.println("<input type='hidden' name='id' value='" + id + "'>");
                    out.println("<button type='submit' class='btn-delete'>Delete</button>");
                    out.println("</form>");
                    out.println("</td>");

                    // Edit Button
                    out.println("<td>");
                    out.println("<form method='get' action='EditQuestionServlet'>");
                    out.println("<input type='hidden' name='id' value='" + id + "'>");
                    out.println("<button type='submit' class='btn-edit'>Edit</button>");
                    out.println("</form>");
                    out.println("</td>");

                    out.println("</tr>");
                }

                if (!found) {
                    out.println("<tr><td colspan='6'>No questions found in this section.</td></tr>");
                }

                out.println("</table>");
                out.println("</div>");

                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<h3>Error: " + e.getMessage() + "</h3>");
            }
        }

        out.println("<a class='back-link' href='admin_dashboard.html'>Back to Dashboard</a>");
        out.println("</div></body></html>");
    }
}
