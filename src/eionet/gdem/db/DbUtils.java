
package eionet.gdem.db;
import eionet.gdem.GDEMException;

public class DbUtils {

  /**
  * returns a valid DbModuleIF
  */
  public static DbModuleIF getDbModule() throws GDEMException {
    return new DbModule();
  }
}