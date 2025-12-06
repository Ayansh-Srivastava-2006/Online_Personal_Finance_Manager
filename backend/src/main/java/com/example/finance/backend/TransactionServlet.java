package com.example.finance.backend;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TransactionServlet extends HttpServlet {
    private DatabaseHelper db = new DatabaseHelper();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String userIdParam = req.getParameter("userId");
        
        if (userIdParam != null) {
            try {
                int userId = Integer.parseInt(userIdParam);
                List<DatabaseHelper.Transaction> transactions = db.getTransactions(userId);
                resp.getWriter().write(gson.toJson(transactions));
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Missing userId parameter\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            DatabaseHelper.Transaction transaction = gson.fromJson(req.getReader(), DatabaseHelper.Transaction.class);
            db.addTransaction(transaction);
            resp.getWriter().write("{\"message\": \"Transaction added\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
