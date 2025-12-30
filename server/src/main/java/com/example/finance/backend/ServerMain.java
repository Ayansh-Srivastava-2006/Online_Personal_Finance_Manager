package com.example.finance.backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add servlets
        context.addServlet(new ServletHolder(new RegisterServlet()), "/register");
        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new AccountServlet()), "/accounts/*");
        context.addServlet(new ServletHolder(new BudgetServlet()), "/budgets/*");
        context.addServlet(new ServletHolder(new TransactionServlet()), "/transactions");

        server.start();
        System.out.println("Server started on port 8080");
        server.join();
    }
}
