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

package eionet.gdem.conversion.excel;

import eionet.gdem.utils.Utils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.io.File;
import java.io.OutputStreamWriter;

public class DD_XMLInstance  {

  private static final String DST_TYPE = "DST";
  private static final String TBL_TYPE = "TBL";
  private static final String DEFAULT_ENCODING = "UTF-8";
	
  protected String lineTerminator = "\n";
	private OutputStreamWriter writer = null;
  
  private String type = TBL_TYPE;//by default it's table
  private Hashtable root_tag=new Hashtable();
  private Vector tables = new Vector();
  private Hashtable row_attrs= new Hashtable();
  private Hashtable elements = new Hashtable();
  private Vector content =  new Vector();
  private StringBuffer namespaces = new StringBuffer();
  private Hashtable leads = null;

  private String cur_row_name = "";
  private String cur_row_attrs = "";
  private String encoding = null;
  
  public DD_XMLInstance() {
    this.lineTerminator = File.separator.equals("/") ? "\r\n" : "\n";
  }
  /*
   * inserts the root_tag name and attributes into Hashtable
   */
  public void setRootTag(String name, String localName, String attributes){
    root_tag.put("name",name);
    root_tag.put("localName", localName);
    root_tag.put("attributes", attributes);
  }
  /*
   * creates the table name, localName and attributes into Hashtable and inserts it into Vector
   */
  public void addTable(String name, String localName, String attributes){
    Hashtable table = new Hashtable();
    table.put("name", name);
    table.put("localName", localName);
    table.put("attributes", attributes);
    tables.add(table);
  }
  public void addTable(Hashtable table){
    tables.add(table);
  }
/*
 * inserts row attributes into Hashtable, where keys are table names
 */
  public void addRowAttributes(String tbl_name, String row_name, String attributes){
    if (tbl_name==null) return;
    Hashtable attribute = new Hashtable();
    attribute.put("row_name", row_name);
    attribute.put("row_attrs", attributes);

    row_attrs.put(tbl_name, attribute);
    
  }
/*
 * inserts element names, localNames and attributes into Hashtable, where keys are table names
 */
  public void addElement(String tbl_name, String name, String localName, String attributes){
    if (tbl_name==null) return;
    Hashtable element = new Hashtable();
    element.put("name", name);
    element.put("localName", localName);
    element.put("attributes", attributes);

    if (elements.containsKey(tbl_name)){
      Vector tbl_elements = (Vector)elements.get(tbl_name);
      if (tbl_elements==null) tbl_elements = new Vector();
      tbl_elements.add(element);
    }
    else{
      Vector tbl_elements = new Vector();
      tbl_elements.add(element);
      elements.put(tbl_name, tbl_elements);
    }
  }
  public void addNamespace(String prefix, String uri){
    namespaces.append(" xmlns:");
    namespaces.append(prefix);
    namespaces.append("=\"");
    namespaces.append(uri);
    namespaces.append("\"");
  }
  public void setTypeDataset(){
  	leads = new Hashtable();
	
    leads.put("tbl", "\t");
	  leads.put("row", "\t\t");
		leads.put("elm", "\t\t\t");
    this.type = DST_TYPE;
  }
  public void setTypeTable(){
	
  	leads = new Hashtable();
	
    leads.put("tbl", "");
	  leads.put("row", "\t");
		leads.put("elm", "\t\t");
	
    this.type = TBL_TYPE;
  }
  public void setEncoding(String encoding){
    this.encoding = encoding;
  }
  public String getEncoding(){
    return this.encoding;
  }
  public Hashtable getRootTag(){
    return this.root_tag;
  }
  public String getRootTagName(){
    return (String)root_tag.get("name");
  }
  public String getRootTagAttributes(){
    return (String)root_tag.get("attributes");
  }
  public Vector getTables(){
    return this.tables;
  }
  public Vector getTblElements(String tbl_name){
    return (Vector)this.elements.get(tbl_name);
  }

	/**
	* Flush the written content into the output stream.
	*/
	public void flush(OutputStream outStream) throws Exception{
    
    try{		
      this.writer = new OutputStreamWriter(outStream, getEncoding());
      writeHeader();
      startRootElement();
      // write content
      for (int i=0; i<content.size(); i++){
  			writer.write((String)content.get(i));
    	} 
  		endRootElement();
      writer.flush();
    }
	  finally{
	      try{
    				if (writer != null) writer.close();
       }
       catch(Exception e){}
   }
	}
  public void writeElement(String elem_name, String attributes, String data){
		addString(getLead("elm") + "<" + elem_name + attributes + ">");
    addString(Utils.escapeXML(data));
    addString("</" + elem_name + ">");
    newLine();
    
  }
  public void writeRowStart(){
		addString(getLead("row") + "<" + cur_row_name + cur_row_attrs + ">");
    newLine();    
  }
  public void writeRowEnd(){
		addString(getLead("row") + "</" + cur_row_name + ">");    
    newLine();
  }
  public void writeTableStart(String tbl_name, String attributes){
    if (type.equals(DST_TYPE)){
  		addString(getLead("tbl") + "<" + tbl_name + attributes + ">");
      newLine();
    }    
  }
  public void writeTableEnd(String tbl_name){
    if (type.equals(DST_TYPE)){
      addString(getLead("tbl") + "</" + tbl_name + ">");    
      newLine();
    }
  }
  public void setCurRow(String tbl_name){
    Hashtable row = (Hashtable)row_attrs.get(tbl_name);
    cur_row_name = (String)row.get("row_name");
    cur_row_attrs = (String)row.get("row_attrs");
  }
	protected void addString(String s){
		content.add(s);
	}
	protected void newLine(){
		content.add(lineTerminator);
	}
	private void writeHeader() throws IOException{
		//writer.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
    String enc = (Utils.isNullStr(getEncoding())) ? DEFAULT_ENCODING : encoding;
		writer.write("<?xml version=\"1.0\" encoding=\"" + enc + "\"?>");
		writer.write(lineTerminator);
	}
  private void startRootElement() throws IOException{
    String root_attributes = getRootTagAttributes();
    if (root_attributes == null) root_attributes="";
    String rootTagOut = getRootTagName();
    
    if (root_attributes.indexOf("xmlns:xsi")>-1)
      rootTagOut +=  root_attributes;
    else
      rootTagOut +=  namespaces.toString() + root_attributes;
		writer.write("<" + rootTagOut + ">");
    writer.write(lineTerminator);
  }
  private void endRootElement() throws IOException{
		writer.write("</" + getRootTagName() + ">");
  }
	protected String escape(String s){
        
		if (s == null) return null;
        
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<s.length(); i++){
			char c = s.charAt(i);
			if (c == '<')
				buf.append("&lt;");
			else if (c == '>')
				buf.append("&gt;");
			else if (c == '&')
				buf.append("&amp;");
			else
				buf.append(c);
		}
        
		return buf.toString();
	}
	protected String getLead(String leadName){
		
		if (leads==null || leads.size()==0){
			setTypeTable();
		}
		
		String lead = (String)leads.get(leadName);
		if (lead==null)
			lead = "";
		
		return lead;
	}
}