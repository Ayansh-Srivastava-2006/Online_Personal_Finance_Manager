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

    // Request DTO
    static class LoginRequest {
        String username;
        String password;
        String email; // For register
        String action; // "login" or "register"
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            LoginRequest request = gson.fromJson(req.getReader(), LoginRequest.class);
            
            if ("login".equalsIgnoreCase(request.action)) {
                DatabaseHelper.User user = db.login(request.username, request.password);
                if (user != null) {
                    resp.getWriter().write(gson.toJson(user));
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
