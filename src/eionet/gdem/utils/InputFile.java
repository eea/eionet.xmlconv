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

package eionet.gdem.utils;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;

import java.io.*;


/**
 * Several commmone class for reading files from url
 * Is able to read the host credentials from database
 * and pass the basic auth to remote server for files with limited access
 */
public class InputFile  {

  private static LoggerIF _logger;
  private String ticket = null;
  private URL url=null;
  private InputStream is = null;
  private boolean isTrustedMode = false;
  private String strFileName = null;
  private String strFileNameNoExtension = null;

  public InputFile(String str_url) throws IOException, MalformedURLException{
      _logger = GDEMServices.getLogger();

      // Java's URL class doesn't escape certain characters with % +hexidecimal digits.
      // This is a bug in the class java.net.URL.
      // The correct way to create a URL object is to use class called java.net.URI (Java 1.4 and later).

      //this.url = new URL(str_url);
      	setURL(str_url);
  }
  /*
   * Get Host credentials from database. There could be restriciton for accesing files in differemnt servers.
   * Username and password are saved in the T_HOST table for these cases
   *
   */
  private void getHostCredentials(String host) {
    try {
      DbModuleIF db = GDEMServices.getDbModule();

      Vector v=db.getHosts(host);

      if (v==null) return;
      if (v.size()>0){
        Hashtable h = (Hashtable)v.get(0);
        String user = (String)h.get("user_name");
        String pwd = (String)h.get("pwd");
        this.ticket =Utils.getEncodedAuthentication(user,pwd);

      }
    } catch (Exception e ) {
      _logger.error("Error getting host data from the DB " + e.toString());
      _logger.error("Conversion proceeded");
    }


  }
  /*
   * get source file from url as InputStream
   * user basic auth, if we know the credentials
   */
  public InputStream getSrcInputStream() throws IOException{
    fillInputStream();
    return is;
  }
  private void fillInputStream() throws IOException{

    URLConnection uc = url.openConnection();

	if (ticket==null && isTrustedMode){
		String host=url.getHost();
		getHostCredentials(host);
	}

    if (ticket!=null){
      //String auth = Utils.getEncodedAuthentication(user,pwd);
      uc.addRequestProperty("Authorization", " Basic " + ticket);

    }

    this.is = uc.getInputStream();

  }
  /**
  * save the InputFile to the specified text file
  */
  public String saveSrcFile()throws IOException {

      fillInputStream();

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
  public void close(){
    try{
      if (is!=null)
        is.close();
    }
    catch(Exception e){
      _logger.error("Closing inputstream in FileInput: " + e.toString());
    }

  }
  public void setAuthentication(String _ticket){
  	this.ticket = _ticket;
  }
  public void setTrustedMode(boolean mode){
  	this.isTrustedMode=mode;
  }

	private void setURL(String str_url) throws MalformedURLException{
	      try{
	    	  URI uri = new URI(escapeSpaces(str_url));
	    	  findFileName(uri.getPath());

	    	  this.url = uri.toURL();

	      }
	      catch(URISyntaxException ue){
	    	  throw new MalformedURLException(ue.toString());
	      }

	}
	/*
	 * escape reserved characters in source URI
	 */
	private String escapeSpaces(String str_uri){
		return Utils.Replace(str_uri, " ", "%20");
	}
	/*
	 * extracts filename from URI's path
	 */
	private void findFileName(String str_uri){

		String name = null;
		if(Utils.isNullStr(str_uri)) return;

		if(str_uri.endsWith("/"))str_uri = str_uri.substring(0,str_uri.length()-1);

		int lastSlash = str_uri.lastIndexOf("/");

		if (lastSlash > -1)
			name=str_uri.substring(lastSlash+1);
		else
			name=str_uri;

		findFileNameNoExtension(name);

		this.strFileName = name;
	}
	private void findFileNameNoExtension(String strFileName) {

		String name = null;
		if(Utils.isNullStr(strFileName)) return;

		int lastDot = strFileName.lastIndexOf(".");

		if (lastDot > -1)
			name=strFileName.substring(0,lastDot);
		else
			name=strFileName;

		this.strFileNameNoExtension = name;
	}


	public String getFileName() {
		return strFileName;
	}
	public String getFileNameNoExtension() {
		return strFileNameNoExtension;
	}


	public static void main(String args[]) {
		String str_url = "http://localhost:8080/xmlconv/tmp/IrelandePERD'a&ta.xml?sss";
		try{

			URI uri = new URI(str_url);
			System.out.println(uri.getPath());
		}
	      catch(URISyntaxException ue){
	    	  System.out.println(ue.toString());
	    	  //throw new MalformedURLException(ue.toString());
	      }
	}
}
