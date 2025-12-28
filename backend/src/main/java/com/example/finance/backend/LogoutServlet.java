package com.example.finance.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class LogoutServlet extends HttpServlet {
    private DatabaseHelper db = new DatabaseHelper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionId = req.getHeader("Authorization");
        if (sessionId != null && sessionId.startsWith("Bearer ")) {
            sessionId = sessionId.substring(7);
        }

        if (sessionId != null) {
            try {
                db.deleteSession(sessionId);
                resp.getWriter().write("{\"message\": \"Logout successful\"}");
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Missing session ID\"}");
        }
    }
}
