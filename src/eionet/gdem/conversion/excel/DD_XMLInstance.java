package eionet.gdem.conversion.excel;

import eionet.gdem.utils.Utils;
import java.util.Hashtable;
import java.util.Vector;
import java.io.File;
import java.io.PrintWriter;

public class DD_XMLInstance  {

  private static final String DST_TYPE = "DST";
  private static final String TBL_TYPE = "TBL";
	
  protected String lineTerminator = "\n";
	private PrintWriter writer = null;
  
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
  
  public DD_XMLInstance(PrintWriter writer) {
    this.writer = writer;
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
	public void flush() throws Exception{
		
		writeHeader();
		startRootElement();
        
		// write content
		for (int i=0; i<content.size(); i++){
			writer.print((String)content.get(i));
		} 
		endRootElement();
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
	private void writeHeader(){
		//writer.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.print(lineTerminator);
	}
  private void startRootElement(){
		writer.print("<" + getRootTagName() + namespaces.toString() + getRootTagAttributes() + ">");
    writer.print(lineTerminator);
  }
  private void endRootElement(){
		writer.print("</" + getRootTagName() + ">");
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