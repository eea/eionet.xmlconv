package eionet.gdem.conversion.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.Date;
import java.util.HashMap;

import eionet.gdem.GDEMException;

/**
* The main class, which is calling POI HSSF methods for creating Excel fiile and adding data into it
* works together with ExcelXMLHandler
* @author Enriko Käsper
*/

public class ExcelConversionHandler implements ExcelConversionHandlerIF
{
  private String fileName=null;
  private HSSFWorkbook wb=null;

  //pointers
  private int currentSheet=0;
  private int currentRow=0;
  private int currentCell=0;

  private Vector styles=null;
  private HashMap cell_style_map=null;
  private Vector columns=null;
  private Vector rows=null;
  
  public ExcelConversionHandler()
  {
      wb = new HSSFWorkbook();
  }
  public void setFileName(String name)
  {
    this.fileName=name;
  }
  public void addWorksheets(String sheetName)
  {
      if(wb==null) return;

      if (sheetName==null) sheetName="Sheet" + String.valueOf(currentSheet+1);
      
      HSSFSheet sheet = wb.createSheet(sheetName);
      currentSheet=wb.getNumberOfSheets()-1;
     // System.out.println("Worksheet" + currentSheet);
      currentRow=0;
      currentCell=0;
      
 }
  public void addRow(String def_style, String def_type)
  {
     HSSFSheet sh = wb.getSheetAt(currentSheet);
     HSSFRow HSSFrow = sh.createRow(currentRow);
     currentRow=sh.getPhysicalNumberOfRows();

     currentCell=0;

     if (rows==null) rows = new Vector();

     short idx = getStyleIdxByName(def_style, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);
     Short short_idx=new Short(idx);

     HashMap row = new HashMap();
     row.put("data_type", def_type);
     row.put("style", short_idx);
     rows.add(row);

  }
  public void addRows(String def_style, String def_type, int repeated)
  {
      for (int i=0;i<repeated;i++)
      {
        addRow(def_style, def_type);
      }
  }
  public void addColumn(String def_style, String def_type)
  {
     if (columns==null) columns = new Vector();

     short idx = getStyleIdxByName(def_style, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);
     Short short_idx=new Short(idx);

     HashMap column = new HashMap();
     column.put("data_type", def_type);
     column.put("style", short_idx);
     columns.add(column);
  }
  public void addColumns(String def_style, String def_type, int repeated)
  {
      for (int i=0;i<repeated;i++)
      {
        addColumn(def_style, def_type);
      }
  }
  public void addCell(String type, String str_value, String style_name){
     HSSFRow _row = wb.getSheetAt(currentSheet).createRow(currentRow);
     HSSFCell _cell = _row.createCell((short)(currentCell));

//     int i_value=0;
     Double number_value=null;
     Boolean boolean_value=null;
//     long l_value=0;
  //   boolean isInt=false;
     boolean isNumber=false;
    // boolean isLong=false;
     boolean isBoolean=false;
     boolean isDate=false;
     if (type==null){
       type=(String)getDefaultParams("data_type");
     }
     if (type!=null){
        if (type.equals("float")||type.equals("number")){
           if (str_value!=null){
              try{
                 number_value= new Double(str_value);
                 isNumber=true;
              }
              catch(Exception e){
                  //the value is not number, it will be inserted as a string
                 // System.out.println(e.toString());
             }
           }
           else
             isNumber=true;
        }
        else if (type.equals("boolean")){
           if (str_value!=null){
              try{
                 boolean_value= new Boolean(str_value);
                 isBoolean=true;
              }
              catch(Exception e){
                  //the value is not boolean, it will be inserted as a string
                 // System.out.println(e.toString());
             }
           }
           else
             isBoolean=true;
        }
        else if (type.equals("date")){
           if (str_value!=null){
              try{
  //                cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyymmdd"));

/*
 * 
 *    The way how to handle user defined formats
 *    not supported right now
                  HSSFDataFormat format = wb.createDataFormat();
                  HSSFCellStyle style = wb.createCellStyle();
                  style.setDataFormat(format.getFormat("yyyymmdd"));
                  _cell.setCellStyle(style); 


*/                  
                  //cellStyle.setDataFormat(new HSSFDataFormat("yyyymmdd"));
                  /*try{
                     l_value=Long.parseLong(value);
                      System.out.println(String.valueOf(l_value));
                     isLong=true;
                  }
                  catch(Exception e){
                     System.out.println(e.toString());
                  }*/
                  /*if (isLong){
                    Date d = new Date();
                    _cell.setCellStyle(cellStyle);
                    //_cell.setCellValue(d);
                    _cell.setCellValue(value);
                    //System.out.println(d.toString());
                    isDate=true;
                  }
                  else
                    _cell.setCellValue(value);*/
                 // System.out.println("hh");
                  
              }
              catch(Exception e){
                  System.out.println(e.toString());
             }
           }
        }
     }
     if (isNumber){
       if (number_value!=null)
         _cell.setCellValue(number_value.doubleValue());
       _cell.setCellType(_cell.CELL_TYPE_NUMERIC);
     }
     else if (isBoolean){
       if (boolean_value!=null)
         _cell.setCellValue(boolean_value.booleanValue());
       _cell.setCellType(_cell.CELL_TYPE_BOOLEAN);
     }
     else if (isDate){
       
     }
     else{
       _cell.setCellType(_cell.CELL_TYPE_STRING);
       _cell.setCellValue(str_value);
     }

     short idx=-1;
     if (style_name!=null){
       idx = getStyleIdxByName(style_name, ExcelStyleIF.STYLE_FAMILY_TABLE_CELL);
     }
     
     if (idx<0){
       Short Short_idx=(Short)getDefaultParams("style");
       if (Short_idx!=null){
         idx=Short_idx.shortValue();
       }
     }

      if (idx>-1)
        _cell.setCellStyle(wb.getCellStyleAt(idx));

     currentCell = _cell.getCellNum()+1;
  //    System.out.println("Cell" + currentCell+ "-" + value);
  }
  public void addCells(String type, String style_name, int repeated)
  {
      for (int i=1;i<repeated;i++)
      {
        addCell(type, null, style_name);
      }
  }

  public void addStyle(ExcelStyleIF style){
     if (style==null) return;
     if (styles==null) styles=new Vector();
     if (cell_style_map==null) cell_style_map= new HashMap();

     if (!styleExists(style))
        styles.add(style);
     if (style.getFamily().equals(ExcelStyleIF.STYLE_FAMILY_TABLE_CELL)){
        addStyleToWorkbook(style);  
     }
  }
  private void addStyleToWorkbook(ExcelStyleIF style){
      HSSFFont font = wb.createFont();
      // Font Size eg.12
      short height = style.getFontSize();
      if (height>0)
        font.setFontHeightInPoints((short)height);
      // Font Name eg.Arial
      String font_name = style.getFontName();
      if (font_name!=null)
        font.setFontName(font_name);
      // Italic
      font.setItalic(style.getItalic());
      // Font Weight eg.bold
      short weight = style.getFontWeight();
      font.setBoldweight((short)weight);

      // Fonts are set into a style so create a new one to use.
      HSSFCellStyle HSSFStyle = wb.createCellStyle();
      HSSFStyle.setFont(font);
      // Text alignment eg.center
      short align = style.getTextAlign();
      HSSFStyle.setAlignment((short)align);

      style.setWorkbookIndex((short)HSSFStyle.getIndex());
      
  }
  private boolean styleExists(ExcelStyleIF style){
      if (style==null) return false;

      for (int i=0; i<styles.size();i++){
        if (style.equals((ExcelStyleIF)styles.get(i)))
            return true;
      }
      return false;
  }
  public ExcelStyleIF getStyleByName(String name, String family){

      if (styles ==null) return null;
      if (name==null || family==null) return null;

      for (int i=0; i<styles.size();i++){
        ExcelStyleIF style=(ExcelStyleIF)styles.get(i);
        if (name.equals(style.getName()) && family.equals(style.getFamily()))
            return style;
      }
      return null;
  }
  private short getStyleIdxByName(String name, String family){

      if (styles ==null) return -1;
      if (name==null || family==null) return -1;
      
      for (int i=0; i<styles.size();i++){
        ExcelStyleIF style=(ExcelStyleIF)styles.get(i);
        if (name.equals(style.getName()) && family.equals(style.getFamily()))
            return style.getWorkbookIndex();
      }
      return -1;
  }
  private Object getDefaultParams(String param){
    if (param==null) return null;
    if (columns==null) return null;
    if (rows==null) return null;
    if (rows.size()<currentRow) return null;
    if (columns.size()<currentCell) return null;

    //Find default value defined at row level
    HashMap row_map = (HashMap)rows.get(currentRow-1);
    if (row_map.containsKey(param)){
      Object value = row_map.get(param);
      if (value!=null){
        if (param.equals("style")){
            Short short_value = (Short)value;
            if (short_value.shortValue()>-1)
              return row_map.get(param);
        }
        else
          return row_map.get(param);
      }
    }
    //Find default value defined at column level
    HashMap col_map = (HashMap)columns.get(currentCell);
    if (col_map.containsKey(param)){
      if (col_map.get(param)!=null)
        return col_map.get(param);
    }

    return null;
  }
  public void writeToFile() throws GDEMException{
      // Write the output to a file
    try
    {     
        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
//        System.out.println("closed");

    }
    catch(Exception e)
    {
       throw new GDEMException("ErrorConversionHandler - couldn't save the Excel file: " + e.toString());
    }
  }
  public void writeToFile(OutputStream outstream) throws GDEMException{
      // Write the output to  Outputstream
    try
    {     
        wb.write(outstream);
    }
    catch(Exception e)
    {
       throw new GDEMException("ErrorConversionHandler - couldn't save the Excel file: " + e.toString());
    }
  }

}