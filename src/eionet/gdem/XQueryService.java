package eionet.gdem;

import net.sf.saxon.Query;
import java.io.IOException;

/**
* Container for holding XQueryService methods
*/
public class XQueryService {
  public XQueryService()  {
  }


  /**
  */
  public String analyze(String sourceURL, String xqScript) throws GDEMException {
      String sourceFile="", xqFile="";

      /* try {
        sourceFile=Utils.saveSrcFile(sourceURL);
      } catch (IOException ioe ) {
        throw new GDEMException("Error getting source file from:" + sourceURL);
      } */

      try {
        xqFile=Utils.saveStrToFile(xqScript, "xq");
      } catch (IOException ioe ) {
        throw new GDEMException("Error storing XQScript into file:" + ioe.toString());
      }

      String xqParam=Utils.XQ_SOURCE_PARAM_NAME + "=" + sourceURL;
      String outFile=Utils.tmpFolder + "gdem_" + System.currentTimeMillis() + ".txt";
      String[] args = {"-o", outFile, xqFile, xqParam};
 
      try {
        Query.main(args);
      } catch (Exception e ) {
      
      }
      String ret="";
      try {
         ret = Utils.readStrFromFile(outFile);
      } catch (IOException ioe ) {
          Utils.log("Error reading result: " + ioe.toString())      ;
          throw new GDEMException("Proceeding XQuery did not succeed "  + ioe.toString());
      }
      Utils.deleteFile(xqFile);
      //Utils.deleteFile(outFile);
      
      return ret;
  }

}