package com.techlabs.quizapp.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UserDashboardServlet")
public class UserDashboardServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("user_id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.sendRedirect("ViewResultsServlet");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<title>User Dashboard</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<style>");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }");
        out.println("th { background-color: #007bff; color: white; }");
        out.println("td { background-color: white; color: black; }");
        out.println("tr:nth-child(even) td { background-color: #f9f9f9; }");
        out.println(".back-link { display: inline-block; margin-top: 20px; text-decoration: none; color: #007bff; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='page-wrapper'>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String userSql = "SELECT username, email, created_at FROM users WHERE user_id = ?";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setInt(1, userId);
            ResultSet userRs = userPs.executeQuery();

            if (userRs.next()) {
                out.println("<h2>User Dashboard: " + userRs.getString("username") + "</h2>");
                out.println("<p><strong>Email:</strong> " + userRs.getString("email") + "</p>");
                out.println("<p><strong>Created At:</strong> " + userRs.getTimestamp("created_at") + "</p>");
            } else {
                out.println("<h3>User not found.</h3>");
                conn.close();
                out.println("<a class='back-link' href='ViewResultsServlet'>Back to Results</a>");
                out.println("</div></body></html>");
                return;
            }

            String resultsSql = "SELECT id, subject, total_questions, correct_answers, wrong_answers, exam_date " +
                                "FROM results WHERE user_id = ?";
            PreparedStatement resultsPs = conn.prepareStatement(resultsSql);
            resultsPs.setInt(1, userId);
            ResultSet resultsRs = resultsPs.executeQuery();

            out.println("<h3>User Results</h3>");
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Subject</th><th>Total Questions</th><th>Correct</th><th>Wrong</th><th>Date</th></tr>");

            boolean hasResults = false;
            while (resultsRs.next()) {
                hasResults = true;
                out.println("<tr>");
                out.println("<td>" + resultsRs.getInt("id") + "</td>");
                out.println("<td>" + resultsRs.getString("subject") + "</td>");
                out.println("<td>" + resultsRs.getInt("total_questions") + "</td>");
                out.println("<td>" + resultsRs.getInt("correct_answers") + "</td>");
                out.println("<td>" + resultsRs.getInt("wrong_answers") + "</td>");
                out.println("<td>" + resultsRs.getTimestamp("exam_date") + "</td>");
                out.println("</tr>");
            }

            if (!hasResults) {
                out.println("<tr><td colspan='6'>No results found.</td></tr>");
            }

            out.println("</table>");
            out.println("<a class='back-link' href='ViewResultsServlet'>Back to Results</a>");
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }

        out.println("</div></body></html>");
    }
}
