package eionet.gdem;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.saxon.Query;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;


public class XQueryServlet extends HttpServlet {

  public void service(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException     {        String source = req.getParameter("source");

    res.setContentType("text/html");
    //res.getWriter().write("<html><body>hei!</body></html>");
    
    String xQuery = req.getParameter("q");
    String inFileName = "C:\\TEMP\\qwerty.xq";
    //File file = new File(inFileName);
    FileWriter fos = new FileWriter(new File(inFileName));

    fos.write(xQuery);

    //dos.flush(); dos.close();
    fos.flush(); fos.close();
   
    String outFileName = "C:\\TEMP\\qwerty.html";

   String[] args = {"-o", outFileName, inFileName};
 
    try {
      Query.main(args);
    } catch (Exception e ) {
      res.getWriter().write("<error>" + e.toString() + "</error>");
    }

  //read file from FileInputsream and write by buffers to the ServletOutputstream
      FileInputStream fis = new FileInputStream( outFileName );
      
      int bufLen = 0;
      byte[] buf = new byte[1024];
      
      while ( (bufLen=fis.read( buf ))!= -1 )
        res.getOutputStream().write(buf, 0, bufLen );
     
      fis.close();    

  }


}