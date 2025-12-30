package com.example.finance.backend;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        User credentials = gson.fromJson(req.getReader(), User.class);

        FinanceDatabase financeDatabase = new FinanceDatabase();
        try {
            User user = financeDatabase.login(credentials.getEmail(), credentials.getPassword());

            if (user != null) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                out.print(gson.toJson(user));
                out.flush();
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
            }
        } catch (FinanceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
