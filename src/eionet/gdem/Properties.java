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
 * The Original Code is "GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 * Several settings and properties for GDEM
 */
public class Properties {

  public static String tmpFolder="/tmp";

  //public static String urlPrefix="http://conversions.eionet.eu.int/";
  
  public static String xslFolder="/xsl/";

  public static String queriesFolder="/queries/";
  
  
  public static final int CONV_SERVICE=1; //Conversion service weight
  public static final int QA_SERVICE=2; //QA service weight
  public static int services_installed=3; //by default the both services are installed

  //public static String xformsFolder="/xforms/";

  //Database settings from the properties file
  public static String dbUrl=null;
  public static String dbDriver=null;
  public static String dbUser=null;
  public static String dbPwd=null;

  //period for checking new jobs in the workqueue in milliseconds, default 20sec
  public static long wqCheckInterval=20000L;
  
   //NB Saxon is the default value, not hard-coded!
	public static String engineClass="eionet.gdem.qa.engines.SaxonImpl";

	
	  //DCM settings from the properties file
	  public static String convFile=null;
	  public static String metaXSLFolder=null;
	  public static String ddURL=null;
	  public static String gdemURL=null;
		
	  //DCM settings from the properties file of incoming services from DD
	  public static String invServUrl=null;
	  public static String invServName=null;
	
	private static ResourceBundle props;
  public static Category logger;

  static {
    if(logger == null)
      logger = Category.getInstance("gdem");
      
    if (props==null) {
      props=ResourceBundle.getBundle("gdem");
      try {
        tmpFolder=props.getString("tmp.folder");
        xslFolder=props.getString("xsl.folder");
        queriesFolder=props.getString("queries.folder");
        //xformsFolder=props.getString("xforms.folder");

        //DB connection settings
        dbDriver=props.getString("db.driver");
        dbUrl=props.getString("db.url");
        dbUser=props.getString("db.user");
        dbPwd=props.getString("db.pwd");

        engineClass=props.getString("xq.engine.implementator");
        //DCM settings 
		convFile=props.getString("dcm.conversions.file");
		metaXSLFolder=props.getString("dcm.conversions.folder");
		ddURL=props.getString("dcm.conversions.dd.url");
		gdemURL=props.getString("dcm.conversions.gdem.url");

        //settings for incoming services from DD 
		invServUrl=props.getString("inserv.dd.url");
		invServName=props.getString("inserv.dd.name");
		
		
				//period in seconds 
	      String frequency = props.getString("wq.check.interval");
		    Float f = new Float(frequency);
			  wqCheckInterval = (long)(f.floatValue() * 1000);

        try{
          services_installed = Integer.parseInt(props.getString("gdem.services"));  
        }
        catch(Exception e){ //ignore, use default
        }
        //wqCheckInterval= (Long.getLong(props.getString("wq.check.interval"))).longValue();


        //urlPrefix=props.getString("url.prefix"); //URL where the files can be downloaded
      } catch (MissingResourceException mse) {

        //no error handling? go with the default values??
      } catch (Exception e ) {
					System.out.println("error " + e.toString());
			}
    }
  }
}