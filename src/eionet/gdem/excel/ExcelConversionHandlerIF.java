package eionet.gdem.excel;

import eionet.gdem.GDEMException;
import java.io.OutputStream;

/**
* The main class, which is calling POI HSSF methods for creating Excel fiile and adding data into it
* works together with ExcelXMLHandler
* @author Enriko Käsper
*/

public interface ExcelConversionHandlerIF  {



  /**
  * Sets the filename to output file
  * @param name - MS Excel file name (full path)
  */
  public void setFileName(String name);

  /**
  * Adds a new worksheet into workbook
  * @param sheetName - name of the new worksheet
  */
  public void addWorksheets(String sheetName);
  /**
  * Adds a new row to the active worksheet
  * @param def_style - default style name
  * @param def_type - default data type
  */
  public void addRow(String def_style, String def_type);

  /**
  * Adds several new rows to the active worksheet
  * @param def_style - default style name
  * @param def_type - default data type
  * @param repeated - the number of new rows
  */
  public void addRows(String def_style, String def_type, int repeated);

  /**
  * Adds a new column to the active worksheet
  * @param def_style - default style name
  * @param def_type - default data type
  */
  public void addColumn(String def_style, String def_type);

  /**
  * Adds several new columns to the active worksheet
  * @param def_style - default style name
  * @param def_type - default data type
  * @param repeated - the number of new rows
  */
  public void addColumns(String def_style, String def_type, int repeated);
  
  /**
  * Adds a new cell to the active worksheet and active row
  * @param type - data type for the new cell, if not defined, then inherited from parent level
  * @param str_value - cell value 
  * @param style_name - style name
  */
  public void addCell(String type, String str_value, String style_name);

  /**
  * Adds several empty cells to the active worksheet and active row
  * @param type - data type for the new cell, if not defined, then inherited from parent level
  * @param style_name - style name
  * @param repeated - the number of new cells
  */
  public void addCells(String type, String style_name, int repeated);

  /**
  * Adds a new Excel style to the active workbook
  * @param ExcelStyle - predefined excel style
  */
  public void addStyle(ExcelStyleIF style);
  /**
  * Returns the excel style by style name
  * @param name - Excel style name
  * @param family - Excel objects family (sheet, row, column, cell)
  * @return excel style object
  */
  public ExcelStyleIF getStyleByName(String name, String family);
  
  /**
  * Writes the EXCEL workbook object into file
  */
  public void writeToFile() throws GDEMException;
  /**
  * Writes the EXCEL workbook object into output stream
  */
  public void writeToFile(OutputStream outstream) throws GDEMException;
}