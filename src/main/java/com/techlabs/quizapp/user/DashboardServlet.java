package com.techlabs.quizapp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect("index.html");
            return;
        }
        int userId = (Integer) session.getAttribute("user_id");
        String username = (String) session.getAttribute("username");

        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>User Dashboard</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("<style>");
            out.println("table { color: black; background-color: white; border-collapse: collapse; width: 100%; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align:center; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<header><div class='logo'><h1>Quiz App</h1></div>");
            out.println("<nav><a class='active' href='UserDashboardServlet'>Dashboard</a>");
            out.println("<a href='LogoutServlet'>Logout</a></nav></header>");

            out.println("<div class='container'>");
            out.println("<div class='welcome-section'>Welcome, <b>" + username + "</b>!</div>");

            out.println("<div class='test-section'>");
            out.println("<h3>Start a Test</h3>");
            out.println("<form method='post' action='StartQuizServlet'>");
            out.println("<select name='section' required>");
            out.println("<option value='' disabled selected>Select Section</option>");
            out.println("<option value='Java'>Java</option>");
            out.println("<option value='SQL'>SQL</option>");
            out.println("<option value='Servlet'>Servlet</option>");
            out.println("<option value='General Knowledge'>General Knowledge</option>");
            out.println("</select><br>");
            out.println("<button class='start-btn' type='submit'>Start</button>");
            out.println("</form>");
            out.println("</div>");

            out.println("<div class='results-section'>");
            out.println("<p style=\"color: black;\">Your Past Attempts</p>");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "SELECT id, subject, total_questions, correct_answers, wrong_answers, exam_date " +
                                 "FROM results WHERE user_id = ? ORDER BY exam_date DESC";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, userId);
                        try (ResultSet rs = ps.executeQuery()) {

                            out.println("<table>");
                            out.println("<thead><tr style='background-color:#4CAF50; color:white;'>");
                            out.println("<th>ID</th><th>Section</th><th>Total</th><th>Correct</th><th>Wrong</th><th>Date</th>");
                            out.println("</tr></thead>");
                            out.println("<tbody>");

                            boolean any = false;
                            while (rs.next()) {
                                any = true;
                                out.println("<tr style='color:black;'>");
                                out.println("<td>" + rs.getInt("id") + "</td>");
                                out.println("<td>" + rs.getString("subject") + "</td>");
                                out.println("<td>" + rs.getInt("total_questions") + "</td>");
                                out.println("<td>" + rs.getInt("correct_answers") + "</td>");
                                out.println("<td>" + rs.getInt("wrong_answers") + "</td>");
                                out.println("<td>" + rs.getTimestamp("exam_date") + "</td>");
                                out.println("</tr>");
                            }
                            if (!any) {
                                out.println("<tr><td colspan='6' style='color:black;'>No attempts yet. Start your first test!</td></tr>");
                            }
                            out.println("</tbody></table>");
                        }
                    }
                }
            } catch (Exception e) {
                out.println("<p style='color: black;'>Error loading results: " + e.getMessage() + "</p>");
            }
            out.println("</div>"); 

            out.println("</div>"); 
            out.println("</body></html>");
        }
    }
}
