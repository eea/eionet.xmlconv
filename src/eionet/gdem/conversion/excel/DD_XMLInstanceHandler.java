package eionet.gdem.conversion.excel;

// Copyright (c) 2000 TietoEnator

import eionet.gdem.utils.Utils;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.lang.reflect.Method;

/**
* Handler for parsing xml instance document from datadictionary
* extening SAX helpers DefaultHandler
* @author Enriko Käsper
*/

public class DD_XMLInstanceHandler extends DefaultHandler{

  private DD_XMLInstance instance = null;

  private static final int   root_level = 0;
  private static final int   table_level = 1;
  private static final int   row_level = 2;
  private static final int   element_level = 3;
  private static final String ROW_TAG = "Row";
  
  private int                level=0;
  private String             cur_table=null;


  public DD_XMLInstanceHandler(DD_XMLInstance instance)
  {
      this.instance = instance;
  }
  public void startPrefixMapping(String prefix, String uri){
      instance.addNamespace(prefix, uri);           
  }
  public void startElement(String uri, String localName, String name, Attributes attributes){

    if (level == root_level){  //root level
      instance.setRootTag(name, localName, attributesToString(attributes));
      level=table_level;
    }
    else if (level == table_level){   //table_level
      if (localName.equalsIgnoreCase(ROW_TAG)){ //it's table schema an there is only 1 table
        cur_table=instance.getRootTagName();
        instance.setTypeTable();
        instance.addTable(instance.getRootTag());
        instance.addRowAttributes(cur_table, name, attributesToString(attributes));
        level=element_level;
      }
      else{ //it's dataset schema with several tables
        cur_table=name;
        instance.setTypeDataset();
        instance.addTable(name,localName, attributesToString(attributes));
        level=row_level;
      }
    }
    else if (level == row_level){ 
      instance.addRowAttributes(cur_table, name, attributesToString(attributes));
      level=element_level;
    }
    else if (level == element_level){   //element_level
      instance.addElement(cur_table,name,localName, attributesToString(attributes));
    }
  }

  public void characters(char[] ch,int start,int len){
  }

  public void endElement(String uri, String localName, String name){
    if (level>table_level){
      if (localName.equalsIgnoreCase(ROW_TAG)){ 
        level = table_level;
      }
    }
  }
  private String attributesToString(Attributes attributes){
      StringBuffer buf = new StringBuffer();
      for (int i=0;i<attributes.getLength();i++){
        buf.append(" ");
        buf.append(attributes.getQName(i));
        buf.append("=\"");
        buf.append(attributes.getValue(i));
        buf.append("\"");
      }
      return buf.toString();
    
  }
  public void setDocumentLocator (Locator locator)
  {
     Locator startloc = new LocatorImpl(locator);
     String encoding = getEncoding(startloc);
     if (!Utils.isNullStr(encoding))
        instance.setEncoding(encoding);
}
  private String getEncoding(Locator locator) {
    String encoding = null;
    Method getEncoding = null;
    try {
        getEncoding = locator.getClass().getMethod("getEncoding", new Class[]{});
        if(getEncoding != null) {
            encoding = (String)getEncoding.invoke(locator, null);
        }
    } catch (Exception e) {
        // either this locator object doesn't have this
        // method, or we're on an old JDK
    }
    return encoding;
  }  
}