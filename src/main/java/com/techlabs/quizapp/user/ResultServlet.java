package com.techlabs.quizapp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect("index.html");
            return;
        }

        int userId = (Integer) session.getAttribute("user_id");
        String section = (String) session.getAttribute("quiz_section");
        List<Integer> qids = (List<Integer>) session.getAttribute("quiz_question_ids");
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute("quiz_answers");

        if (section == null || qids == null || qids.isEmpty()) {
            resp.sendRedirect("DashboardServlet");
            return;
        }
        if (answers == null) answers = new HashMap<>();

        int total = qids.size();
        int correct = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                String inClause = qids.toString().replace("[", "(").replace("]", ")");
                String sql = "SELECT question_id, correct_option FROM questions WHERE question_id IN " + inClause;
                Map<Integer, String> correctMap = new HashMap<>();
                try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                    while (rs.next()) {
                        correctMap.put(rs.getInt("question_id"), rs.getString("correct_option"));
                    }
                }

                for (Integer qid : qids) {
                    String userAns = answers.get(qid);
                    String crt = correctMap.get(qid);
                    if (userAns != null && crt != null && userAns.equalsIgnoreCase(crt)) correct++;
                }

                int wrong = total - correct;
                int score = (int) ((correct * 100.0) / total); 

                String insertResult = "INSERT INTO results (user_id, subject, total_questions, correct_answers, wrong_answers, score) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertResult)) {
                    ps.setInt(1, userId);
                    ps.setString(2, section);
                    ps.setInt(3, total);
                    ps.setInt(4, correct);
                    ps.setInt(5, wrong);
                    ps.setInt(6, score);
                    ps.executeUpdate();
                }

                String insertUA = "INSERT INTO user_answers (user_id, question_id, answer) VALUES (?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insertUA)) {
                    for (Map.Entry<Integer, String> e : answers.entrySet()) {
                        ps.setInt(1, userId);
                        ps.setInt(2, e.getKey());
                        ps.setString(3, e.getValue());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                session.removeAttribute("quiz_section");
                session.removeAttribute("quiz_question_ids");
                session.removeAttribute("quiz_index");
                session.removeAttribute("quiz_answers");
                session.removeAttribute("quiz_end_time");

                resp.setContentType("text/html; charset=UTF-8");
                try (PrintWriter out = resp.getWriter()) {
                    out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Result</title>");
                    out.println("<link rel='stylesheet' href='css/style.css'>");
                    out.println("<style>");
                    out.println(".btn { padding: 8px 16px; font-size: 14px; margin-right:10px; cursor:pointer; }");
                    out.println(".correct { color: green; font-weight: bold; }");
                    out.println(".wrong { color: red; font-weight: bold; }");
                    out.println("</style></head><body>");

                    out.println("<header><div class='logo'><h1>Quiz App</h1></div>");
                    out.println("<nav><a href='DashboardServlet'>Dashboard</a><a href='LogoutServlet'>Logout</a></nav></header>");
                    out.println("<div class='container'>");
                    out.println("<h2>Test Completed - Section: " + section + "</h2>");
                    out.println("<div class='results-section'>");
                    out.println("<p>Total: " + total + ", Correct: <span class='correct'>" + correct + "</span>, Wrong: <span class='wrong'>" + wrong + "</span>, Score: " + score + "%</p>");

                    out.println("<div style='margin-top:20px; display:flex; gap:10px; justify-content:center;'>");

                    // Retest button
                    out.println("<form method='post' action='StartQuizServlet'>");
                    out.println("<input type='hidden' name='section' value='" + section + "'>");
                    out.println("<button class='btn' type='submit'>Retest</button>");
                    out.println("</form>");

                    // Show Result button
                    out.println("<form method='get' action='ShowResultServlet'>");
                    out.println("<input type='hidden' name='user_id' value='" + userId + "'>");
                    out.println("<input type='hidden' name='section' value='" + section + "'>");
                    out.println("<button class='btn' type='submit'>Show Result</button>");
                    out.println("</form>");

                    // Back to Dashboard 
                    out.println("<a href='DashboardServlet' class='btn'>Back to Dashboard</a>");

                    out.println("</div>");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setContentType("text/html");
            resp.getWriter().println("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
        }
    }
}
