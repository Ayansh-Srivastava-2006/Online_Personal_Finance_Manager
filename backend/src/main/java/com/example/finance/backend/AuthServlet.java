package com.example.finance.backend;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AuthServlet extends HttpServlet {
    private DatabaseHelper db = new DatabaseHelper();
    private Gson gson = new Gson();

    // Request DTOs
    static class AuthRequest {
        String username;
        String password;
        String email; // For register
        String action; // "login" or "register"
    }

    // Response DTO
    static class LoginResponse {
        String sessionId;
        DatabaseHelper.User user;
        LoginResponse(String s, DatabaseHelper.User u) { this.sessionId=s; this.user=u; }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            AuthRequest request = gson.fromJson(req.getReader(), AuthRequest.class);
            
            if ("login".equalsIgnoreCase(request.action)) {
                DatabaseHelper.User user = db.login(request.username, request.password);
                if (user != null) {
                    String sessionId = db.createSession(user.getId());
                    resp.getWriter().write(gson.toJson(new LoginResponse(sessionId, user)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"error\": \"Invalid credentials\"}");
                }
            } else if ("register".equalsIgnoreCase(request.action)) {
                db.register(request.username, request.email, request.password);
                resp.getWriter().write("{\"message\": \"Registration successful\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid action\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
