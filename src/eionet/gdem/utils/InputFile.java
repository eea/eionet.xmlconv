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

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
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
  private String user = null;
  private String pwd = null;
  private URL url=null;
  private InputStream is = null;
  
  public InputFile(String str_url) throws IOException, MalformedURLException{
      _logger = GDEMServices.getLogger();
      
      this.url = new URL(str_url);
      String host=url.getHost();
      getHostCredentials(host);
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
        this.user = (String)h.get("user_name");
        this.pwd = (String)h.get("pwd");
        
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
    
    if (user!=null){
      String auth = Utils.getEncodedAuthentication(user,pwd);
      uc.addRequestProperty("Authorization", " Basic " + auth);
      
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
}