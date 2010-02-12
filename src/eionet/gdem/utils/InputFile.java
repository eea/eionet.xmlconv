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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IHostDao;


/**
 * Several commmone class for reading files from url
 * Is able to read the host credentials from database
 * and pass the basic auth to remote server for files with limited access
 * 
 * NB! Always call close() method in finally block, otherwise the InputStream stays open 
 */
public class InputFile  {

	private String ticket = null;
	private URL url=null;
	private InputStream is = null;
	private boolean isTrustedMode = false;
	private String strFileNameNoExtension = null;
	private String strFileName = null;
	private String strHostName = null;
	private String strFolderName = null;
	boolean isClosed = false;
	//instance = strHostName + strFolderName + "/" + strFileName 
	private IHostDao hostDao = GDEMServices.getDaoService().getHostDao();
	private static LoggerIF _logger = GDEMServices.getLogger();



	/**
	 * Initializes InputUrl object and sets the URI from str_url
	 * @param str_url - the URL of source file
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public InputFile(String str_url) throws IOException, MalformedURLException{

		// Java's URL class doesn't escape certain characters with % +hexidecimal digits.
		// This is a bug in the class java.net.URL.
		// The correct way to create a URL object is to use class called java.net.URI (Java 1.4 and later).

		//this.url = new URL(str_url);
		setURL(str_url);
	}


	/*
	 * get source file from url as InputStream
	 * user basic auth, if we know the credentials
	 */
	public InputStream getSrcInputStream() throws IOException{
		fillInputStream();
		return is;
	}

	/**
	 * save the InputFile to the specified text file
	 */
	public String saveSrcFile()throws IOException {

		fillInputStream();

		FileOutputStream fos = null;
		String fileName=null;
		String tmpFileName=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";

		try{
			File file =new File(tmpFileName);
			fos=new FileOutputStream(file);

			int bufLen = 0;
			byte[] buf = new byte[1024];

			while ( (bufLen=is.read( buf ))!= -1 )
				fos.write(buf, 0, bufLen );

			fileName=tmpFileName;
		}
		finally{
			close();
			if(fos!=null){
				try{
					fos.flush(); 
					fos.close();
				}
				catch(Exception e){}
			}
		}
		

		return fileName;

	}
	/**
	 * closes inputstream of source file
	 *
	 */
	public void close(){
		try{
			if (is!=null && !isClosed){
				is.close();
				isClosed=true;
			}
		}
		catch(Exception e){
			_logger.warning("Closing inputstream in FileInput: " + e.toString());
		}

	}
	/**
	 * Sets the authentication ticket for the source file
	 * @param _ticket
	 */
	public void setAuthentication(String _ticket){
		this.ticket = _ticket;
	}
	/**
	 * Sets the boolean to use authentication ticket 
	 * for grabbing the source file or not. true - use ticket
	 * @param _ticket
	 */
	public void setTrustedMode(boolean mode){
		this.isTrustedMode=mode;
	}
	/**
	 * Extracts the file name from URL path eg: BasicQuality.xml
	 * where the full url is 
	 * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
	 */
	public String getFileName() {
		return strFileName;
	}
	/**
	 * Extracts the file name without file extension from URL path eg: BasicQuality
	 * where the full url is 
	 * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
	 */
	public String getFileNameNoExtension() {
		return strFileNameNoExtension;
	}
	/**
	 * Return source file URL as a String
	 */
	public String toString(){
		return (url==null)?null:url.toString();
	}
	/**
	 * Extracts the full host name from URL eg: http://cdrtest.eionet.europa.eu
	 * where the full url is 
	 * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
	 */
	public String getHostName(){
		return strHostName;
	}
	/**
	 * Extracts the folder from URL path eg: /al/eea/colrjhlyq/envrjhqwa
	 * where the full url is 
	 * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
	 */
	public String getFolderName(){
		return strFolderName;
	}
	/**
	 * Exscracts CDR file info from URL and returns it as a map of paramters
	 * If the source file is a file from CDR then the Map contains the following
	 * parameters: envelopeurl, envelopepath, instance, filename
	 * @return
	 */
	public HashMap getCdrParams(){
		String strEnvelopeUrl = null;
		String strInstance = null;
		HashMap h = new HashMap();
		if(getHostName()!=null && getFolderName()!=null){
			strEnvelopeUrl = getHostName().concat(getFolderName());
		}
		if(getHostName()!=null && getFolderName()!=null &&
				getFileName()!=null){
			strInstance = getHostName().concat(getFolderName()).concat(
					(getFolderName().endsWith("/")?"":"/")).concat(
							getFileName());
		}
		h.put("filename", getFileName());
		h.put("envelopeurl", strEnvelopeUrl);
		h.put("envelopepath", getFolderName());
		h.put("instance", strInstance);
		
		return h;
	}
	public URL getURL(){
		return this.url;
	}
	
	/**
	 * Get the authentication ticket for the source file, if available
	 * @param _ticket
	 */
	public String getAuthentication(){
		if (Utils.isNullStr(ticket) && isTrustedMode){
			String host=url.getHost();
			getHostCredentials(host);
		}
		return ticket;
	}

	/*
	 * PRIVATE METHODS
	 */

	/*
	 * Get Host credentials from database. There could be restriciton for accesing files in differemnt servers.
	 * Username and password are saved in the T_HOST table for these cases
	 *
	 */
	private void getHostCredentials(String host) {
		try {

			Vector v=hostDao.getHosts(host);


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
	/**
	 * Opens URLConnection and reads the source into InputStream
	 * @throws IOException
	 */
	private void fillInputStream() throws IOException{

		isClosed=false;
		URLConnection uc = url.openConnection();

		ticket = getAuthentication();
		
		if (ticket!=null){
			//String auth = Utils.getEncodedAuthentication(user,pwd);
			uc.addRequestProperty("Authorization", " Basic " + ticket);

		}

		this.is = uc.getInputStream();

	}
	/**
	 * Stores the URL
	 * @param str_url
	 * @throws MalformedURLException
	 */
	private void setURL(String str_url) throws MalformedURLException{
		try{
			URI uri = new URI(escapeSpaces(str_url));
			parseUri(uri);

			this.url = uri.toURL();

		}
		catch(URISyntaxException ue){
			throw new MalformedURLException(ue.toString());
		}
		catch(IllegalArgumentException ae){
			throw new MalformedURLException(ae.toString());			
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
	 * [scheme:][//authority][path][?query][#fragment]
	 */
	private void parseUri(URI uri){

		this.strHostName = uri.getScheme() + "://" + uri.getAuthority();
		findFileName(uri.getPath());
	}
	/*
	 * extracts filename and folder from URI's path
	 */
	private void findFileName(String str_uri){

		String fileName = null;
		String folderName = null;
		if(Utils.isNullStr(str_uri)) return;

		if(str_uri.endsWith("/"))str_uri = str_uri.substring(0,str_uri.length()-1);

		int lastSlash = str_uri.lastIndexOf("/");

		if (lastSlash > -1){
			fileName=str_uri.substring(lastSlash+1);
			folderName = str_uri.substring(0,lastSlash);
		}
		else{
			fileName=str_uri;
			folderName="";
		}

		findFileNameNoExtension(fileName);

		this.strFileName = fileName;
		this.strFolderName = folderName;
	}
	/*
	 * extracts filename without file extension from URI's path
	 */
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



	public static void main(String args[]) {
		String str_url = "http://localhost:8080/xmlconv/tmp/IrelandePERD a&ta.xml?sss";
		InputFile in =null;
		try{
			/*
			URI _uri = new URI(str_url);
			URL uri = _uri.toURL();
			System.out.println("path" + uri.getPath());
			System.out.println("host" + uri.getHost());
			System.out.println("query" + uri.getQuery());
			System.out.println("authority" + uri.getAuthority());
			System.out.println("file" + uri.getFile());
			 */		
			in = new InputFile(str_url);
			System.out.println(in.getHostName());
			System.out.println(in.getFolderName());
			System.out.println(in.getFileName());
			System.out.println(in.getFileNameNoExtension());
			System.out.println(in.getCdrParams().toString());
		}
		//catch(URISyntaxException ue){
			//System.out.println(ue.toString());
			//throw new MalformedURLException(ue.toString());
		//} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (in!=null){
				try{
					in.close();
				}
				catch(Exception e){}
			}
		}
	}
}
