package com.techlabs.quizapp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/QuizServlet")
public class QuizServlet extends HttpServlet {
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

        Long endTime = (Long) session.getAttribute("quiz_end_time");
        if (endTime == null) { 
            resp.sendRedirect("DashboardServlet");
            return;
        }

        long now = System.currentTimeMillis();
        if (now >= endTime) {
            resp.sendRedirect("ResultServlet"); 
            return;
        }

        List<Integer> qids = (List<Integer>) session.getAttribute("quiz_question_ids");
        Integer index = (Integer) session.getAttribute("quiz_index");
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute("quiz_answers");
        String section = (String) session.getAttribute("quiz_section");

        if (qids == null || qids.isEmpty() || index == null) {
            resp.sendRedirect("DashboardServlet");
            return;
        }

        if (index < 0) index = 0;
        if (index >= qids.size()) {
            resp.sendRedirect("ResultServlet");
            return;
        }

        int qid = qids.get(index);

        String qtext = null, a = null, b = null, c = null, d = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT question_text, option_a, option_b, option_c, option_d FROM questions WHERE question_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, qid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            qtext = rs.getString(1);
                            a = rs.getString(2);
                            b = rs.getString(3);
                            c = rs.getString(4);
                            d = rs.getString(5);
                        }
                    }
                }
            }
        } catch (Exception e) {
            resp.setContentType("text/html");
            resp.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
            return;
        }

        String selected = answers.getOrDefault(qid, "");
        long remainingSec = Math.max(0, (endTime - now) / 1000);

        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            out.println("<title>Quiz - " + section + "</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");

            out.println("<script>");
            out.println("let remaining=" + remainingSec + ";");
            out.println("function tick(){ if(remaining<=0){ window.location='ResultServlet'; return; } remaining--; document.getElementById('timer').innerText = format(remaining); setTimeout(tick,1000);} ");
            out.println("function format(s){const m=Math.floor(s/60), ss=s%60; return (m<10?'0':'')+m+':'+(ss<10?'0':'')+ss;} ");
            out.println("window.onload = tick;");
            out.println("</script>");
            out.println("</head><body>");

            out.println("<header><div class='logo'><h1>Quiz App</h1></div>");
            out.println("<nav><a href='DashboardServlet'>Dashboard</a><a href='LogoutServlet'>Logout</a></nav></header>");

            out.println("<div class='container'>");
            out.println("<h2>" + section + " Test</h2>");
            out.println("<div style='margin:10px 0;font-weight:bold;'>Time Left: <span id='timer'></span></div>");
            out.println("<div class='test-section'>");

            out.println("<h3>Question " + (index + 1) + " of " + qids.size() + "</h3>");
            out.println("<p>" + qtext + "</p>");
            out.println("<form method='post' action='QuizServlet'>");
            out.println("<input type='hidden' name='qid' value='" + qid + "'>");
            out.println("<label><input type='radio' name='answer' value='A' " + ("A".equals(selected)?"checked":"") + "> A. " + a + "</label><br>");
            out.println("<label><input type='radio' name='answer' value='B' " + ("B".equals(selected)?"checked":"") + "> B. " + b + "</label><br>");
            out.println("<label><input type='radio' name='answer' value='C' " + ("C".equals(selected)?"checked":"") + "> C. " + c + "</label><br>");
            out.println("<label><input type='radio' name='answer' value='D' " + ("D".equals(selected)?"checked":"") + "> D. " + d + "</label><br><br>");

            out.println("<button name='action' value='skip' type='submit'>Skip</button> ");
            out.println("<button name='action' value='next' type='submit'>Next</button> ");
            if (index > 0) {
                out.println("<button name='action' value='back' type='submit'>Back</button> ");
            }
            out.println("<button name='action' value='finish' type='submit'>Finish</button> ");

            out.println("</form>");
            out.println("</div></div>");
            out.println("</body></html>");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect("index.html");
            return;
        }

        Long endTime = (Long) session.getAttribute("quiz_end_time");
        if (endTime == null || System.currentTimeMillis() >= endTime) {
            resp.sendRedirect("ResultServlet");
            return;
        }

        List<Integer> qids = (List<Integer>) session.getAttribute("quiz_question_ids");
        Integer index = (Integer) session.getAttribute("quiz_index");
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute("quiz_answers");
        if (qids == null || index == null || answers == null) {
            resp.sendRedirect("DashboardServlet");
            return;
        }

        String action = req.getParameter("action");
        String ans = req.getParameter("answer");
        String qidStr = req.getParameter("qid");
        int qid = (qidStr != null && !qidStr.isEmpty()) ? Integer.parseInt(qidStr) : qids.get(index);

        if (ans != null && ans.matches("[ABCD]")) answers.put(qid, ans);

        if ("next".equals(action) || "skip".equals(action)) {
            if (index < qids.size() - 1) index++;
        } else if ("back".equals(action)) {
            if (index > 0) index--;
        } else if ("finish".equals(action)) {
            session.setAttribute("quiz_answers", answers);
            resp.sendRedirect("ResultServlet");
            return;
        }

        session.setAttribute("quiz_index", index);
        session.setAttribute("quiz_answers", answers);
        resp.sendRedirect("QuizServlet");
    }
}
