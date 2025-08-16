package com.techlabs.quizapp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ShowResultServlet")
public class ShowResultServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect("index.html");
            return;
        }

        int userId = (Integer) session.getAttribute("user_id");
        String selectedSection = req.getParameter("section");

        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            out.println("<title>Your Quiz Results</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("<style>");
            out.println("table { width:100%; border-collapse: collapse; margin-top:20px; }");
            out.println("th, td { border:1px solid #ddd; padding:10px; text-align:center; }");
            out.println("th { background-color:#2575fc; color:white; }");
            out.println(".correct { color:green; font-weight:bold; }");
            out.println(".wrong { color:red; font-weight:bold; }");
            out.println(".btn { padding:8px 16px; font-size:14px; margin-top:10px; margin-right:10px; cursor:pointer; }");
            out.println("</style></head><body>");
            out.println("<header><div class='logo'><h1>Quiz App</h1></div>");
            out.println("<nav><a href='DashboardServlet'>Dashboard</a><a href='LogoutServlet'>Logout</a></nav></header>");
            out.println("<div class='container'>");

            if (selectedSection == null || selectedSection.isEmpty()) {
                out.println("<h2>Select Section to View Results</h2>");
                out.println("<form method='get' action='ShowResultServlet'>");

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "SELECT DISTINCT subject FROM results WHERE user_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, userId);
                            try (ResultSet rs = ps.executeQuery()) {
                                out.println("<select name='section' required>");
                                out.println("<option value=''>-- Select Section --</option>");
                                while (rs.next()) {
                                    String section = rs.getString("subject");
                                    out.println("<option value='" + section + "'>" + section + "</option>");
                                }
                                out.println("</select>");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
                }

                out.println("<br><button class='btn' type='submit'>Show Results</button>");
                out.println("</form>");

            } else {
                out.println("<h2>Results for Section: " + selectedSection + "</h2>");
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "SELECT total_questions, correct_answers, wrong_answers, score, exam_date " +
                                     "FROM results WHERE user_id=? AND subject=? ORDER BY exam_date DESC";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, userId);
                            ps.setString(2, selectedSection);
                            try (ResultSet rs = ps.executeQuery()) {
                                out.println("<table>");
                                out.println("<tr><th>Total</th><th>Correct</th><th>Wrong</th><th>Score (%)</th><th>Date</th></tr>");
                                while (rs.next()) {
                                    int total = rs.getInt("total_questions");
                                    int correct = rs.getInt("correct_answers");
                                    int wrong = rs.getInt("wrong_answers");
                                    int score = rs.getInt("score");
                                    Timestamp date = rs.getTimestamp("exam_date");

                                    out.println("<tr>");
                                    out.println("<td>" + total + "</td>");
                                    out.println("<td class='correct'>" + correct + "</td>");
                                    out.println("<td class='wrong'>" + wrong + "</td>");
                                    out.println("<td>" + score + "%</td>");
                                    out.println("<td>" + date + "</td>");
                                    out.println("</tr>");
                                }
                                out.println("</table>");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
                }
                out.println("<br><a href='ShowResultServlet' class='btn'>Back to Section Selection</a>");
            }

            out.println("<br><a href='DashboardServlet' class='btn'>Back to Dashboard</a>");
            out.println("</div></body></html>");
        }
    }
}
