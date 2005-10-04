/*
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
 * The Original Code is Web Dashboards Service
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED) 
*/

package com.eurodyn.web.util.common;

import java.util.*;
import java.io.*;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;


/**
 * <p>Description: This utility gives to any application capablility to take its properties
 * defined in appropriate propery files, without need to have knowledge of directory location in file system.<br>
 * Higher APIs may take Property or Resource Boundle objects according to desired action they want to perform.<br>
 * Utility creates boundle for internal use by getting application_home.properties file.<br>
 * That property file will be created at installation time of application, when user chooses desired application home directory.<br>
 * Instantiation of the EurodynConfiguration class is restricted to one object - Singleton pattern.</p>
 * @version 1.0
 */
public class AppConfigurator {

   private static AppConfigurator instance = null;
   private static Map boundlesCache;
   private static Map proportiesCache;
   private String applicationHome;


   /**
    * Protected constructor suppresses default public constructor.
    * @throws ConfiguratorException
    */
   protected AppConfigurator() throws ConfiguratorException {
      try {
         InitialContext ic = new InitialContext();
         Context env = (Context)ic.lookup("java:comp/env");
         applicationHome = (String) env.lookup("APPLICATION_HOME");
         System.out.println(applicationHome);
         boundlesCache = Collections.synchronizedMap(new HashMap());
         proportiesCache = Collections.synchronizedMap(new HashMap());
      } catch (Exception e) {
         throw new ConfiguratorException("Can not load default  boundle ! " + e.getMessage());
      }
   }


   /**
    * Always gets sinlge same instance of AppConfigurator class if it has benn created before,
    * if not it creates instance but only on first call by first client.
    * @throws ConfiguratorException
    * @return AppConfigurator Instance of AppConfigurator class.
    */
   public static AppConfigurator getInstance() throws ConfiguratorException {
      if (null == instance) {
         synchronized (AppConfigurator.class) {
            if (null == instance) {
               try {
                  instance = new AppConfigurator();
               } catch (ConfiguratorException ce) {
                  throw ce;
               }
            }
         }
      }
      return instance;
   }

   public String getApplicationHome() {
      return applicationHome;
   }


   /**
    * Retreives Properties by using specified property file name.
    * @param name Property file name without extension.
    * @throws ConfiguratorException
    * @return Properties
    */
   public Properties getProperties(String name) throws ConfiguratorException {
      String fullPropertyName = applicationHome + File.separator + name + ".properties";
      Properties current = (Properties) proportiesCache.get(name);
      if (current == null) {
         try {
            current=new Properties();
            current.load(new BufferedInputStream(new FileInputStream(fullPropertyName)));
            proportiesCache.put(name,current);
         } catch (IOException ex) {
            throw new ConfiguratorException(ex.getMessage());
         }
      }
      return (Properties)current;
   }


   /**
    * Retreives ResourceBundle according to specified property file name.
    * @param name Property file name without extension.
    * @throws ConfiguratorException
    * @return ResourceBundle
    */
   public ResourceBundle getBoundle(String name) throws ConfiguratorException {
      ResourceBundle current = (ResourceBundle) boundlesCache.get(name);
      if (current == null) {
         String fullPropertyName = applicationHome + File.separator + name +  ".properties";
         PropertyResourceBundle tempVar = null;
         try {
            tempVar = new PropertyResourceBundle(new BufferedInputStream(new FileInputStream(fullPropertyName)));
            current = (ResourceBundle) tempVar;
            boundlesCache.put(name, current);
         } catch (Exception ex) {
            throw new ConfiguratorException(ex.getMessage());
         }
      }
      return current;
   }

}
