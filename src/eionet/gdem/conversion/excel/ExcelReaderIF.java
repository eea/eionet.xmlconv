package eionet.gdem.conversion.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;

import java.io.InputStream;
import eionet.gdem.GDEMException;

/**
* The main class, which is calling POI HSSF methods for reading Excel file
* @author Enriko Käsper
*/
public interface ExcelReaderIF 
{

/**
* If the excel file is generated from Data Dictionary,
* then it finds the XML Shema from Excel file
* @return - XML Schema URL
*/
public String getXMLSchema();

/**
* Initialize the Excel Workbook from InputStream
* @param InputStream input - input Excel file
*/
public void initReader(InputStream input) throws GDEMException;


/**
* Goes through the Excel worksheets and writes the data into DD_XMLInstance as xml
* @param DD_XMLInstance instance - XML instance file, where the structure xml has been efined before
*/
public void readDocumentToInstance(DD_XMLInstance instance)throws GDEMException;
}