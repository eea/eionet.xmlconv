/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.conversion;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.ServletException;
import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.util.Vector;

import eionet.gdem.GDEMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.conversion.ssr.Names;

public class ConversionServlet extends HttpServlet {

  public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

    String url = req.getParameter("url");
    String format = req.getParameter("format");
    String save = req.getParameter("save");

    String list = req.getParameter("list");

    if ( Utils.isNullStr(list) && ( Utils.isNullStr(url) || Utils.isNullStr(format))   ){
      String err_message = "Some of the following parameters are missing: 'list' or 'format' or 'file url'!";
      handleError(req,res, new GDEMException(err_message), Names.ERROR_JSP);
      return;
    }

    try {
      //do the conversion
      if (Utils.isNullStr(list)) {
      // For testing 
     //System.out.println("Start: " + Long.toString(System.currentTimeMillis()));
        if (format.equalsIgnoreCase(Names.EXCEL2XML_CONV_PARAM)){
          convertExcel2XML(res, url, format, save);
        }
        else {
          convertXML(res, url, format, save);
        }
        //For testing
        //System.out.println("End: " + Long.toString(System.currentTimeMillis()));
      }
      else {
          listConversions(res, list);
      }
        
      
    } 
    catch (GDEMException ge) {
      handleError(req,res, ge,Names.ERROR_JSP);
      return;
      //throw new ServletException("Conversion failed " + ge.toString());
    }
    

  }
  private void convertXML(HttpServletResponse res, String url, String format, String save) throws GDEMException, IOException{
    ConversionService cnv = new ConversionService();
    boolean save_src =false;

      if (save!=null)
        save_src=true;
      Hashtable result=null;
      if (!save_src){
        //System.out.println("Response ");
          result = cnv.convert(url, format, res);
      }
      else{
        //System.out.println("File ");
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
  }  
  private void convertExcel2XML(HttpServletResponse res, String url, String format, String save) throws GDEMException{
    ConversionService cnv = new ConversionService();
    boolean save_src =true;
    Vector result=null;

    if (save!=null)
        save_src=true;
    if (!save_src){
      //System.out.println("Response ");
      result = cnv.convertDD_XML(url, res);
    }
    else{
      //System.out.println("File ");
      result = cnv.convertDD_XML(url);
      String contentType="text/xml";
      String result_code = (String)result.get(0);
      
      if (result_code==null) result_code="";
      
      if (result_code.equals("0")){
        byte[] content = (byte[])result.get(1);
      
        try{
          res.setContentType(contentType);
          ByteArrayInputStream byteIn = new ByteArrayInputStream(content);      
          int bufLen = 0;
          byte[] buf = new byte[1024];
      
          while ( (bufLen=byteIn.read( buf ))!= -1 )
            res.getOutputStream().write(buf, 0, bufLen );
          byteIn.close();
        }
        catch(IOException e){
          throw new GDEMException(e.toString(), e);
        }
      }
      else{
        if (result.size()>1){
          String err_mess = (String)result.get(1);
          throw new GDEMException(err_mess);
        }
      }
    }
  }  
    private void listConversions(HttpServletResponse res, String list) throws GDEMException, IOException{
      ConversionService cnv = new ConversionService();
      Vector conversions=null;
      Hashtable xslD=null;

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
  }  /**
  * doPost()
  */
  public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
    doGet(req, res);
  }

}