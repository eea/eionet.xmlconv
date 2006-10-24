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
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.services;


import eionet.gdem.services.db.dao.DCMDaoFactory;

/**
 * Container class for GDEM Services e.g. DBService, Logger etc
 */
public class GDEMServices {
  
  private static LoggerIF _logger=null;
  
 
  public static DCMDaoFactory getDaoService(){
	  return DCMDaoFactory.getDaoFactory(DCMDaoFactory.MYSQL_DB);
  }
  
  public static LoggerIF getLogger()   {
    if (_logger==null)
      try {
        _logger=new LoggerModule();
      } catch (Exception e ) {
        //whatever exception occurs here we still have to log the messages somewhere, 
        //so we use a dummy logger instead that logs messages to System.err
        _logger=new DummyLogger();
      }
      
    return _logger;
  }
  
  static class DummyLogger implements LoggerIF {

    public boolean enable(int level) {      return true;    }

    public void debug(Object msg) { fatal(msg, null); }
    public void debug(Object msg, Throwable t) { fatal(msg); }

    public void info(Object msg) { fatal(msg, null);}
    public void info(Object msg, Throwable t) { fatal(msg);}
    
    public void warning(Object msg) {fatal(msg, null);}
    public void warning(Object msg, Throwable t) {fatal(msg);}    
    
    public void error(Object msg) {fatal(msg, null); }
    public void error(Object msg, Throwable t) {fatal(msg);}
    
    public void fatal(Object msg) {  fatal(msg,null); }
    public void fatal(Object msg, Throwable t) {
      System.err.println("===================================================");
      System.err.println("Error " + msg );
      System.err.println("===================================================");      
      if (t!= null)
        t.printStackTrace(System.err);
    }
       
  }
}