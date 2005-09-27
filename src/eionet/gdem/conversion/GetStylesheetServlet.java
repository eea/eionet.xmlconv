/**
 * Author: Kolundzija Dusko
 */

package eionet.gdem.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.ServletException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.util.Hashtable;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.util.Vector;

import eionet.gdem.GDEMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.xsl.Conversion;
import eionet.gdem.dcm.xsl.ConversionDto;
import eionet.gdem.Properties;

public class GetStylesheetServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

    String metaXSLFolder = Properties.metaXSLFolder;
	String tableDefURL = Properties.ddURL;
    String id = req.getParameter("id");
    String convId = req.getParameter("conv");
    
    ConversionDto conv =  Conversion.getConversionById(convId);
    
    //hardcoded for test
    String format = metaXSLFolder + conv.getStylesheet(); 
    String url = tableDefURL + "GetTableDef?id=" + id;
	
    if ( Utils.isNullStr(id) && Utils.isNullStr(convId) ){
      String err_message = "Some of the following parameters are missing: 'id' or 'conv'!";
      handleError(req,res, new GDEMException(err_message), Names.ERROR_JSP);
      return;
    }

    try {
      //do the conversion      
        convertXML(res, url, format);
    } 
    catch (GDEMException ge) {
      handleError(req,res, ge,Names.ERROR_JSP);
      return;
      //throw new ServletException("Conversion failed " + ge.toString());
    }
    

  }
  private void convertXML(HttpServletResponse res, String url, String format) throws GDEMException, IOException{
    ConversionService cnv = new ConversionService();
    
    Hashtable result=null;

        result = cnv.makeDynamicXSL(url, format,res);
        String contentType=(String)result.get("content-type");
        byte[] content = (byte[])result.get("content");
        res.setContentType(contentType);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(content);      
        int bufLen = 0;
        byte[] buf = new byte[1024];
      
        while ( (bufLen=byteIn.read( buf ))!= -1 )
          res.getOutputStream().write(buf, 0, bufLen );
          byteIn.close();
        /*}*/
  } 
  
  /**
  * handle error and direct to the correct JSP
  */
  protected void handleError(HttpServletRequest req, HttpServletResponse res, Exception err, String jspName) throws ServletException, IOException  {
      //System.out.println(errMsg);
      HttpSession sess = req.getSession(true);
      //GDEMException err= new GDEMException(errMsg);
      sess.setAttribute("gdem.exception", err);
      if (Utils.isNullStr(jspName)) jspName = Names.ERROR_JSP;
      
      //req.getRequestDispatcher(jspName).forward(req,res);
      res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + "/" + jspName));
      return;
  } 
  
  /**
  * doPost()
  */
  public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
    doGet(req, res);
  }
  
  
  

}