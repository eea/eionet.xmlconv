package eionet.gdem.conversion.excel;

public interface ExcelStyleIF  {

/**
* This interface is defing the methods for mapping styles between xml (fo:style) and sytles defined in Excel tool
* 
* @author Enriko Käsper
*/
  

  public static final String STYLE_FAMILY_TABLE="table";
  public static final String STYLE_FAMILY_TABLE_COLUMN="table-column";
  public static final String STYLE_FAMILY_TABLE_ROW="table-row";
  public static final String STYLE_FAMILY_TABLE_CELL="table-cell";

  /**
  * Get methods returns the differemt parameters defined in the ExcelSTyle object
  * @return excel style parameters
  */
  public String getName();
  public String getFamily();
  public boolean getItalic();
  public short getFontWeight();
  public short getFontSize();
  public String getFontName();
  public short getTextAlign();
  public short getWorkbookIndex();

// set methods
  /**
  * Sets the name and family for created Excel style
  * @param name - style name  
  * @param family - excel object family, which has the current style (sheet, column, row, cell)
  */
  public void setExcelStyle(String name, String family);
  /**
  * Sets the font italic parameter
  * @param str_italic  
  */
  public void setItalic(String str_italic);
  /**
  * Sets the font weight parameter
  * @param str_bold  
  */
  public void setFontWeight(String str_bold);
  /**
  * Sets the font size parameter
  * @param str_bold  
  */
  public void setFontSize(String str_size);
  /**
  * Sets the font name parameter
  * @param str_bold  
  */
  public void setFontName(String str_fontname);
  /**
  * Sets the font text align parameter
  * @param str_bold  
  */
  public void setTextAlgin(String str_align);
  /**
  * Compares 2 excel styles (name & family)
  * @param style  
  */
  public boolean equals(ExcelStyleIF style);
  /**
  * Sets the index for the style, defined in 1 workbook
  * @param index  
  */
  public void setWorkbookIndex(short index);
}