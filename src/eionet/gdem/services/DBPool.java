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

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * Database pool implementation for non-servlets
 * Date:    21.03.02
 * Updates: <BR>
 *
 * @implements DBPoolIF 
 * @author  Kaido Laine
 * @version 1.0
 */

public class DBPool {
   private static final int MySQL = 1;
   private static final int Oracle = 2;
   private static final int Access = 3;

   private int mode = MySQL;

   private String url;
   private String drv;
   private String dbUser;
   private String dbPwd;


  /**
  * 
  */ 
   DBPool( String url, String driver, String user, String pwd ) {
    this.url = url;
    this.drv = driver;
    this.dbUser = user;
    this.dbPwd = pwd;
   }

   private Connection _getConnection(String user, String pass)  {
      switch (mode) {
         case MySQL:
            return _getMySQLConnection(user, pass);
         //case Oracle:
         //   return _getOracleConnection(user, pass);
         //case Access:
         //   return _getAccessConnection();
         default:
            return null;
      }
   }

   
   private Connection _getMySQLConnection(String user, String pass)  {
      /*if (user == null) {
      } */


      try {
         Class.forName( this.drv );

         Connection conn = DriverManager.getConnection( this.url, user, pass ) ;
         return conn;
      } catch (Throwable t) {
         t.printStackTrace();
      }
      return null;
   }


/**
 * Retrieves a java.sql.Connection.
 *
 * @return Connection
 */
   public Connection getConnection() {
      try {
         return _getConnection(null, null);
      } catch (Throwable t) {
         t.printStackTrace();
      }
      return null;
   }
/**
 * returns connection to database
 * null, if not successful
 */
   public Connection getConnection(String user, String password) {
      try {
         return _getConnection(user, password);
      } catch (Throwable t) {
         t.printStackTrace();
      }
      return null;
   }

   private static void _log(String s ){
      System.out.println(s);
   }
   
}
