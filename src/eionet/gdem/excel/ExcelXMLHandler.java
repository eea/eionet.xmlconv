package eionet.gdem.excel;

// Copyright (c) 2000 TietoEnator

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import javax.xml.parsers.*;
import java.util.*;


/**
* Handler for parsing xml document
* extening SAX BaseHandler
* This class is calling different ExcelConversionhandler methods, which is actuially creating Excel file
* @author Enriko Käsper
*/

public class ExcelXMLHandler extends DefaultHandler implements ExcelXMLTags{

   protected ExcelConversionHandler excel;

  private StringBuffer fieldData = new StringBuffer(); // buffer for collecting characters

  private HashMap cell=null;
  private Vector cells=new Vector();//null;

  private String cell_value=null;
  private String cell_type=null;
  private String cell_style=null;

  private ExcelStyle style=null;


   private static final int   root_level = 0;
   private static final int   sheet_level = 1;
   private static final int   row_level = 2;
   private static final int   cell_level = 3;

   private int                level;
 
  private boolean             bOK=false;

  public ExcelXMLHandler(ExcelConversionHandler excel)
  {
      this.excel = excel;
  }

  public void startElement(String uri, String localName, String name, Attributes attributes){

      if (name.equals(SHEET_TAG)){
          excel.addWorksheets(attributes.getValue(SHEET_NAME_ATTR));
          level=sheet_level;
      }
      else if (name.equals(COLUMN_TAG)){
          String s_repeated = attributes.getValue(COLUMN_REPEATED_ATTR);
          String def_cell_style = attributes.getValue(DEF_CELL_STYLE_NAME_ATTR);
          String def_cell_type = attributes.getValue(DEF_CELL_VALUE_TYPE_ATTR);

          int i_repeated=1;
          if (s_repeated!=null){
            try{
              i_repeated = Integer.parseInt(s_repeated);
            }
            catch(Exception e){}
          }     
          excel.addColumns(def_cell_style,def_cell_type, i_repeated);
      }
      else if (name.equals(ROW_TAG)){
          String s_repeated = attributes.getValue(ROW_REPEATED_ATTR);
          String def_cell_style = attributes.getValue(DEF_CELL_STYLE_NAME_ATTR);
          String def_cell_type = attributes.getValue(DEF_CELL_VALUE_TYPE_ATTR);
          //System.out.println("rida--------");
          int i_repeated=1;
          if (s_repeated!=null){
            try{
              i_repeated = Integer.parseInt(s_repeated);
            }
            catch(Exception e){}
          }     
          excel.addRows(def_cell_style, def_cell_type, i_repeated);
          level=row_level;
      }
      else if (name.equals(CELL_TAG)){
          String s_repeated = attributes.getValue(COLUMN_REPEATED_ATTR);
          cell_style = attributes.getValue(TABLE_STYLE_NAME_ATTR);
          cell_type = attributes.getValue(VALUE_TYPE_ATTR);
          int i_repeated=1;
          if (s_repeated!=null){
            try{
              i_repeated = Integer.parseInt(s_repeated);
            }
            catch(Exception e){}
          }     
          excel.addCells(cell_type, cell_style, i_repeated);

          level=cell_level;
          cell_value=null;
      }
      else if (name.equals(STYLE_TAG)){
          String style_name = attributes.getValue(STYLE_NAME_ATTR);
          String style_family = attributes.getValue(STYLE_FAMILY_ATTR);

          style = new ExcelStyle(style_name, style_family);
      }          
      else if (name.equals(STYLE_PROP_TAG)){
          style.setFontWeight(attributes.getValue(FONT_WEIGHT_ATTR));
          style.setFontName(attributes.getValue(FONT_FAMILY_ATTR));
          style.setFontSize(attributes.getValue(FONT_SIZE_ATTR));
          style.setItalic(attributes.getValue(FONT_STYLE_ATTR));
          style.setTextAlgin(attributes.getValue(TEXT_ALIGN_ATTR));
      }          
      if (name.equals(DATA_TAG)){
          bOK = true;
      }
  }

  public void characters(char[] ch,int start,int len){
    if (bOK==true){
      fieldData.append(ch, start, len);
    }
  }

  public void endElement(String uri, String localName, String name){

    if (name.equals(CELL_TAG)){
      //cells.add(cell);
      excel.addCell(cell_type, cell_value,cell_style);
      
    }
    else if (name.equals(STYLE_TAG)){
      excel.addStyle(style);
      
    }
    else{
      if (bOK){
        if (level==cell_level)  
          cell_value=fieldData.toString().trim();
        bOK=false;
      }
      fieldData =  new StringBuffer();
    }
  }

}