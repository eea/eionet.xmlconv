package eionet.gdem;

import eionet.gdem.excel.*;
import org.xml.sax.*;
import javax.xml.parsers.*;


/**
* This class is creating handlers for creating Excel file from xml
* called from ConversionService
* @author Enriko Käsper
*/

public class ExcelProcessor  {
  public ExcelProcessor() {
  }
  public void makeExcel(String sIn, String sOut) throws GDEMException {
    
      if (sIn == null) return;
      if (sOut == null) return;
      
      ExcelConversionHandler excel = new ExcelConversionHandler();
      excel.setFileName(sOut);

      try{
        ExcelXMLHandler handler=new ExcelXMLHandler(excel);
        SAXParserFactory spfact = SAXParserFactory.newInstance();
        SAXParser parser = spfact.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        spfact.setValidating(true);

        reader.setContentHandler(handler);
        reader.parse(sIn);
        excel.writeToFile();
      }
      catch (Exception e){
        throw new GDEMException("Error generating Excel file: " + e.toString());
      }
      return;
  }
  public static void main(String[] args){
    String excelFile = "F:\\Projects\\gdem\\test\\test1.xls";
    String srcFile = "F:\\Projects\\gdem\\test\\content2.xml";
    try{
      ExcelProcessor processor = new ExcelProcessor();
      processor.makeExcel(srcFile, excelFile);  
    }
    catch(Exception e){
      System.out.println(e.toString());
    }
  }
}