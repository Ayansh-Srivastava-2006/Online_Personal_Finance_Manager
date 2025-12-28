package com.example.finance.backend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        // Port 8080
        Server server = new Server(8080);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);

        // Map Servlets
        handler.addServlet(new ServletHolder(new AuthServlet()), "/auth");
        handler.addServlet(new ServletHolder(new TransactionServlet()), "/transactions");
        handler.addServlet(new ServletHolder(new LogoutServlet()), "/logout");

        System.out.println("Starting server on port 8080...");
        server.start();
        server.join();
    }
}
