
package eionet.gdem.conversion.excel;

public class ExcelUtils {

  /**
  * returns a valid ExcelConversionHandlerIF
  */
  public static ExcelConversionHandlerIF getExcelConversionHandler(){
    return new ExcelConversionHandler();
  }
  /**
  * returns a valid ExcelStyleIF
  */
  public static ExcelStyleIF getExcelStyle(){
    return new ExcelStyle();
  }
}