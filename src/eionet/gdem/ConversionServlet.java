package eionet.gdem;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.util.Vector;


public class ConversionServlet extends HttpServlet {

  public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

    String url = req.getParameter("url");
    String format = req.getParameter("format");

    String list = req.getParameter("list");

    if ( Utils.isNullStr(list) && ( Utils.isNullStr(url)&& Utils.isNullStr(format))   )
      throw new ServletException("Parameter 'list' or parameters 'format' and 'url' are missing");

    try {
      ConversionService cnv = new ConversionService();
      Vector conversions;
      Hashtable xslD;


      //do the conversion
      if (Utils.isNullStr(list)) {

      // For testing 
     //System.out.println("Start: " + Long.toString(System.currentTimeMillis()));

        String save = req.getParameter("save");
        boolean save_src =false;

        if (save!=null)
            save_src=true;
        Hashtable result=null;
        if (!save_src){
           System.out.println("Response ");
          result = cnv.convert(url, format, res);
          
        }
        else{
         System.out.println("File ");
          result = cnv.convert(url, format);

          String contentType=(String)result.get("content-type");

          byte[] content = (byte[])result.get("content");

          res.setContentType(contentType);
      
          ByteArrayInputStream byteIn = new ByteArrayInputStream(content);
      
          int bufLen = 0;
          byte[] buf = new byte[1024];
      
          while ( (bufLen=byteIn.read( buf ))!= -1 )
            res.getOutputStream().write(buf, 0, bufLen );
      
          byteIn.close();
        }
        //For testing
        //System.out.println("End: " + Long.toString(System.currentTimeMillis()));
      }
      else {
        conversions = cnv.listConversions(list);
        res.setContentType("text/html");
        if (conversions.size() == 0)
          res.getWriter().write("<h1>No conversions available for schema: " + list + "</h1>");
        else {        
          res.getWriter().write("<h1>Available formats for schema: " + list + "</h1>");
          res.getWriter().write("<table border='1'>");
          res.getWriter().write("<tr><th>Format ID</th><th>Format description</th></tr>");
          for (int i =0; i< conversions.size(); i++) {
            xslD = (Hashtable)conversions.elementAt(i);
            res.getWriter().write("<tr><td>" + (String)xslD.get("xsl") + "</td><td>" + (String)xslD.get("description")  +  "</td></tr>");
          }
         res.getWriter().write("</table>");
        }
      }
        
      
    } catch (GDEMException ge) {
      throw new ServletException("Conversion failed " + ge.toString());
    }
    

  }

}