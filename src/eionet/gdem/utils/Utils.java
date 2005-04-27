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
 * Original Code: Kaido Laine (TietoEnator)
 */
package eionet.gdem.utils;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import java.net.URL;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Several common methods for file handling etc
 */
public class Utils {

 
	private static Hashtable xmlEscapes = null;
  /*
  public static String tmpFolder="/tmp";

  //public static String urlPrefix="http://conversions.eionet.eu.int/";
  
  public static String xslFolder="/xsl/";

  //Database settings from the properties file
  public static String dbUrl=null;
  public static String dbDriver=null;
  public static String dbUser=null;
  public static String dbPwd=null;

  //period for checking new jobs in the workqueue in milliseconds, default 20sec
  public static long wqCheckInterval=20000L;
  
   //NB Saxon is the default value, not hard-coded!
	public static String engineClass="eionet.gdem.qa.engines.SaxonImpl";
	
	private static ResourceBundle props;
  private static Category logger;
  */
/*
  static {
    if(logger == null)
        logger = Category.getInstance("gdem");
      
    if (props==null) {
      props=ResourceBundle.getBundle("gdem");
      try {
        tmpFolder=props.getString("tmp.folder");
        xslFolder=props.getString("xsl.folder");

        //DB connection settings
        dbDriver=props.getString("db.driver");
        dbUrl=props.getString("db.url");
        dbUser=props.getString("db.user");
        dbPwd=props.getString("db.pwd");

        engineClass=props.getString("xq.engine.implementator");
        
				//period in seconds 
	      String frequency = props.getString("wq.check.interval");
		    Float f = new Float(frequency);
			  wqCheckInterval = (long)(f.floatValue() * 1000);

        //wqCheckInterval= (Long.getLong(props.getString("wq.check.interval"))).longValue();


        //urlPrefix=props.getString("url.prefix"); //URL where the files can be downloaded
      } catch (MissingResourceException mse) {

        //no error handling? go with the default values??
      } catch (Exception e ) {
					System.out.println("error " + e.toString());
			}
    }
  }
  */
  /**
  * saving an URL stream to the specified text file
  */
  public static String saveSrcFile(String srcUrl)throws IOException {

     URL url = new URL(srcUrl);      
     InputStream is = url.openStream();

     String fileName=null;
     String tmpFileName=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";

     File file =new File(tmpFileName);
     FileOutputStream fos=new FileOutputStream(file);
      
      int bufLen = 0;
      byte[] buf = new byte[1024];
      
      while ( (bufLen=is.read( buf ))!= -1 )
        fos.write(buf, 0, bufLen );
     
      fileName=tmpFileName;
      is.close();
      fos.flush(); fos.close();

      return fileName;

  }

  public static String saveStrToFile(String str, String extension) throws IOException {
    return saveStrToFile(null, str, extension);
  }
   /**
  * Stores a String in a text file 
  * @param String fileName: 
  * @param String str: text to be stored
  * @param String ext: file extension
  */
  public static String saveStrToFile(String fileName, String str, String extension) throws IOException {

    if (fileName==null)
      fileName=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + "." + extension;
    else{
      if (extension!=null)
        fileName=fileName+"."+extension;
    }
      
    FileWriter fos = new FileWriter(new File(fileName));
    fos.write(str);
    fos.flush(); fos.close();
    return fileName;
  }

  public static String readStrFromFile(String fileName) throws java.io.IOException {

    BufferedReader fis = new BufferedReader(new FileReader(fileName)); 
    StringBuffer s = new StringBuffer();
    String line=null;
    while ((line = fis.readLine()) != null) 
      s.append(line + "\n");
     
      fis.close();
      return s.toString();    
  }


  public static void deleteFile(String fName) {
    File f = new File(fName);
    f.delete();

  }

  static void log(Object msg) {
    Properties.logger.info(msg);
  }

  public static boolean isNullStr(String s ) {
    if (s==null || s.trim().equals(""))
      return true;
    else
      return false;
  } 
  public static boolean isNullVector(Vector v ) {
    if (v==null)
      return true;
    else
      if (v.size()==0) return true;
    
    return false;
  } 
  public static boolean isNullHashtable(Hashtable h ) {
    if (h==null)
      return true;
    else
      if (h.isEmpty()) return true;
    
    return false;
  } 
  /**
  * Checks if the given string is a well-formed URL
  */
  public static boolean isURL(String s){
      try {
          URL url = new URL(s);
      }
      catch (MalformedURLException e){
          return false;
      }
        
      return true;
  }
  /**
  * Checks if the given string is number
  */
  public static boolean isNum(String s){
      try {
          int i = Integer.parseInt(s);
      }
      catch (Exception e){
          return false;
      }
        
      return true;
  }
    /**
     * A method for replacing substrings in string
     */
    public static String Replace(String str, String oldStr, String replace) {
        str = (str != null ? str : "");

        StringBuffer buf = new StringBuffer();
        int found = 0;
        int last=0;

        while ((found = str.indexOf(oldStr, last)) >= 0) {
            buf.append(str.substring(last, found));
            buf.append(replace);
            last = found+oldStr.length();
        }
        buf.append(str.substring(last));
        return buf.toString();
    }
	  /**
     * A method for decoding the BASIC auth from request header
     */
    public static String getEncodedUsername(String str)  throws java.io.IOException {
      byte[] b_decoded = new BASE64Decoder().decodeBuffer(str);
      String str_decoded = new String(b_decoded);
      int sep =str_decoded.indexOf(":");
      if (sep>0)
        return str_decoded.substring(0,sep);
      else
        return null;
    }
	  /**
     * A method for decoding the BASIC auth from request header
     */
    public static String getEncodedPwd(String str)  throws java.io.IOException {
      byte[] b_decoded = new BASE64Decoder().decodeBuffer(str);
      String str_decoded = new String(b_decoded);
      int sep =str_decoded.indexOf(":");
      if (sep>0)
        return str_decoded.substring(sep +1);
      else
        return null;
    }   
	  /**
     * A method for encoding the BASIC auth for request header
     */
    public static String getEncodedAuthentication(String user, String pwd)  throws java.io.IOException {
      String auth = user + ":" + pwd;
      return new BASE64Encoder().encode(auth.getBytes());
    }   
	  /**
     * A method for escaping apostrophes
     */
    public static String strLiteral(String in) {
    in = (in != null ? in : "");
    StringBuffer ret = new StringBuffer("'");

    for (int i = 0; i < in.length(); i++) {
      char c = in.charAt(i);
      if (c == '\'')
        ret.append("''");
      else
        ret.append(c);
    }
    ret.append('\'');

    return ret.toString();
  }
	public static String escapeXML(String text){
		
		if (text==null) return null;
		if (text.length()==0) return text;
		
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<text.length(); i++)
			buf.append(escapeXML(i, text));
		
		return buf.toString();
	}
	
	public static String escapeXML(int pos, String text){
		
		if (xmlEscapes==null) setXmlEscapes();
		Character c = new Character(text.charAt(pos));
		for (Enumeration e=xmlEscapes.elements(); e.hasMoreElements(); ){
			String esc = (String)e.nextElement();
			if (pos+esc.length() < text.length()){
				String sub = text.substring(pos, pos+esc.length());
				if (sub.equals(esc))
					return c.toString();
			}
		}
		
		if (pos+1 < text.length() && text.charAt(pos+1)=='#'){
			int semicolonPos = text.indexOf(';', pos+1);
			if (semicolonPos!=-1){
				String sub = text.substring(pos+2, semicolonPos);
				if (sub!=null){
					try{
						// if the string between # and ; is a number then return true,
						// because it is most probably an escape sequence
						if (Integer.parseInt(sub)>=0)
							return c.toString();
					}
					catch (NumberFormatException nfe){}
				}
			}
		}
		
		String esc = (String)xmlEscapes.get(c);
		if (esc!=null)
			return esc;
		else
			return c.toString();
	}
	
	private static void setXmlEscapes(){
		xmlEscapes = new Hashtable();
		xmlEscapes.put(new Character('&'), "&amp;");
		xmlEscapes.put(new Character('<'), "&lt;");
		xmlEscapes.put(new Character('>'), "&gt;");
		xmlEscapes.put(new Character('"'), "&quot;");
		xmlEscapes.put(new Character('\''), "&apos;");
	}
	   /** 
	  * reads temporary file from dis and returs as a bytearray
	  */
	  public static byte[] fileToBytes(String fileName) throws GDEMException {

	    ByteArrayOutputStream baos = null;
	    try {

	      //log("========= open fis " + fileName);
	      FileInputStream fis = new     FileInputStream(fileName);
	      //log("========= fis opened");
	      
	      baos = new ByteArrayOutputStream();
	    
	      int bufLen = 0;
	      byte[] buf = new byte[1024];

	  
	     while ( (bufLen=fis.read( buf ))!= -1 )
	          baos.write(buf, 0, bufLen );

	      fis.close();
	      
	    } catch (FileNotFoundException fne) {
	      throw new GDEMException("File not found " + fileName, fne);
	    } catch (Exception e) {
	      throw new GDEMException("Exception " + e.toString(), e);
	    }    
	      return baos.toByteArray();    
	  }
	  public static boolean containsKeyIgnoreCase(Hashtable hash, String val){
		Enumeration keys = hash.keys();
        while (keys.hasMoreElements()){
            String key = keys.nextElement().toString();
            if (key.equalsIgnoreCase(val))return true;
        }
        return false;
      }
}
  