package org.shunya.server.poc;

import org.shunya.server.poc.AgentContextListner;
import org.shunya.shared.model.Agent;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="myServlet", urlPatterns={"/slowprocess"}, asyncSupported=true)
public class MyServlet extends HttpServlet {
   
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext aCtx = request.startAsync(request, response);
        ServletContext appScope = request.getServletContext();
        Agent agent = new Agent();
        agent.setName("First");
//        agent.setId("1000");

        ((AgentContextListner)appScope.getAttribute("listner")).register(agent, aCtx);

//        ((Queue<AsyncContext>)appScope.getAttribute("slowWebServiceJobQueue")).add(aCtx);
    }
}