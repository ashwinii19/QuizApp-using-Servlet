package com.techlabs.quizapp.user;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/StartQuizServlet")
public class StartQuizServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin#123";

    private static final int QUIZ_DURATION_SECONDS = 600;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect("index.html");
            return;
        }

        String section = req.getParameter("section");
        if (section == null || section.trim().isEmpty()) {
            resp.sendRedirect("UserDashboardServlet");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT question_id FROM questions WHERE section = ? ORDER BY RAND()";
                List<Integer> qids = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, section);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) qids.add(rs.getInt(1));
                    }
                }

                if (qids.isEmpty()) {
                    resp.setContentType("text/html");
                    resp.getWriter().println("<script>alert('No questions available in " + section + "');location='UserDashboardServlet';</script>");
                    return;
                }

                session.setAttribute("quiz_section", section);
                session.setAttribute("quiz_question_ids", qids);
                session.setAttribute("quiz_index", 0);
                session.setAttribute("quiz_answers", new HashMap<Integer, String>());

                long endTimeMillis = System.currentTimeMillis() + (QUIZ_DURATION_SECONDS * 1000L);
                session.setAttribute("quiz_end_time", endTimeMillis);

                resp.sendRedirect("QuizServlet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setContentType("text/html");
            resp.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
