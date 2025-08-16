package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ViewResultsServlet")
public class ViewResultsServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String section = request.getParameter("section");

        out.println("<html><head>");
        out.println("<title>User Results by Section</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<style>");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }");
        out.println("th { background-color: #007bff; color: white; }");
        out.println("td { background-color: white; color: black; }");
        out.println("tr:nth-child(even) td { background-color: #f9f9f9; }");
        out.println(".btn-delete { background: #dc3545; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 5px; }");
        out.println(".btn-delete:hover { background: #c82333; }");
        out.println(".btn-dashboard { background: #28a745; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 5px; }");
        out.println(".btn-dashboard:hover { background: #218838; }");
        out.println(".back-link { display: inline-block; margin-top: 20px; text-decoration: none; color: #007bff; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='page-wrapper'>");
        out.println("<h2>View Results by Section</h2>");

        out.println("<form method='get' action='ViewResultsServlet'>");
        out.println("<label for='section'>Select Section:</label>");
        out.println("<select name='section' id='section' required>");
        out.println("<option value='' disabled selected>Select Section</option>");
        out.println("<option value='Java'" + ("Java".equals(section) ? " selected" : "") + ">Java</option>");
        out.println("<option value='SQL'" + ("SQL".equals(section) ? " selected" : "") + ">SQL</option>");
        out.println("<option value='Servlet'" + ("Servlet".equals(section) ? " selected" : "") + ">Servlet</option>");
        out.println("<option value='General Knowledge'" + ("General Knowledge".equals(section) ? " selected" : "") + ">General Knowledge</option>");
        out.println("</select>");
        out.println("<button type='submit'>View Results</button>");
        out.println("</form><br>");

        if (section != null && !section.trim().isEmpty()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                String sql = "SELECT r.id, u.user_id, u.username, r.subject, r.total_questions, r.correct_answers, r.wrong_answers, r.exam_date " +
                             "FROM results r JOIN users u ON r.user_id = u.user_id WHERE r.subject = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, section);
                ResultSet rs = ps.executeQuery();

                out.println("<div class='scroll-box'>");
                out.println("<table>");
                out.println("<tr><th>ID</th><th>User</th><th>Subject</th><th>Total Questions</th><th>Correct</th><th>Wrong</th><th>Date</th><th>Actions</th></tr>");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int resultId = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");

                    out.println("<tr>");
                    out.println("<td>" + resultId + "</td>");
                    out.println("<td>" + username + "</td>");
                    out.println("<td>" + rs.getString("subject") + "</td>");
                    out.println("<td>" + rs.getInt("total_questions") + "</td>");
                    out.println("<td>" + rs.getInt("correct_answers") + "</td>");
                    out.println("<td>" + rs.getInt("wrong_answers") + "</td>");
                    out.println("<td>" + rs.getTimestamp("exam_date") + "</td>");

                    // Actions column
                    out.println("<td>");
                    // Delete button
                    out.println("<form method='post' action='DeleteResultServlet' style='display:inline;' onsubmit=\"return confirm('Delete this result?');\">");
                    out.println("<input type='hidden' name='id' value='" + resultId + "'>");
                    out.println("<button type='submit' class='btn-delete'>Delete</button>");
                    out.println("</form> ");
                    // View dashboard button
                    out.println("<form method='get' action='UserDashboardServlet' style='display:inline;'>");
                    out.println("<input type='hidden' name='user_id' value='" + userId + "'>");
                    out.println("<button type='submit' class='btn-dashboard'>View Dashboard</button>");
                    out.println("</form>");
                    out.println("</td>");

                    out.println("</tr>");
                }

                if (!found) {
                    out.println("<tr><td colspan='8'>No results found for this section.</td></tr>");
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
