package org.shunya.server;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@WebServlet(name="myBidServlet", urlPatterns={"/auctionservice"}, asyncSupported=true)
public class MyBidServlet extends HttpServlet {
   
   // track bid prices
   public void doGet(HttpServletRequest request, HttpServletResponse response) {
      AsyncContext aCtx = request.startAsync(request, response);
      // This could be a cluser-wide cache.
      ServletContext appScope = request.getServletContext();
      Map<String, List<AsyncContext>> aucWatchers = (Map<String, List<AsyncContext>>)appScope.getAttribute("aucWatchers");
      List<AsyncContext> watchers = (List<AsyncContext>)aucWatchers.get(request.getParameter("auctionId"));
      watchers.add(aCtx); // register a watcher
   }
 
   // place a bid
   public void doPost(HttpServletRequest request, HttpServletResponse response) {
      // run in a transactional context 
      // save a new bid
      AsyncContext aCtx = request.startAsync(request, response); 
      ServletContext appScope = request.getServletContext(); 
      Queue<Bid> aucBids = (Queue<Bid>)appScope.getAttribute("aucBids");
      aucBids.add((Bid)request.getAttribute("bid"));  // a new bid event is placed queued.  
   }
}
