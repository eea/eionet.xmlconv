package eionet.gdem;

import java.io.IOException;

import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import java.util.Timer;
import eionet.gdem.qa.WQChecker;

/**
* Servlet started automatically when the servlet engine starts
* Runs the scheduled Workqueue checker - checks if new jobs received
*/

public class XQueryServlet extends HttpServlet {

  public void init(ServletConfig config) throws ServletException {
  
    try {
      //!! from the props file
      //(new Timer()).scheduleAtFixedRate( new WQChecker(), 0, 20000L );
			(new Timer()).scheduleAtFixedRate( new WQChecker(), 0, Utils.wqCheckInterval );
    } catch (Exception e) {
      //better error handling here!!
      throw new ServletException(e.getMessage(), e);
    }
  }

private static void _l(String s ){
  System.out.println ("=========================================");
  System.out.println (s);
  System.out.println ("=========================================");  

}


  public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

  }

}