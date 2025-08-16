package com.techlabs.quizapp.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<Integer, String> loggedInUsers = new HashMap<>();      
        Map<String, String> loggedInAdmins = new HashMap<>();    

        ServletContext context = sce.getServletContext();
        context.setAttribute("loggedInUsers", loggedInUsers);
        context.setAttribute("loggedInAdmins", loggedInAdmins);

        System.out.println("Global loggedInUsers and loggedInAdmins maps initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute("loggedInUsers");
        context.removeAttribute("loggedInAdmins");
        System.out.println("Global loggedInUsers and loggedInAdmins maps removed.");
    }
}
