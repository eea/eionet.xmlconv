package eionet.gdem;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import java.net.URL;
import java.io.*;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class Utils {
  static String tmpFolder="/tmp";
  static String xslFolder="/tmp/";
  public static final String XQ_SOURCE_PARAM_NAME="source_url";
  private static ResourceBundle props;
  private static Category logger;

  static {
    if(logger == null)
      logger = Category.getInstance("gdem");
      
    if (props==null) {
      props=ResourceBundle.getBundle("gdem");
      try {
        tmpFolder=props.getString("tmp.folder");
        xslFolder=props.getString("xsl.folder");
      } catch (MissingResourceException mse) {
        
      }
      
    }

    
  }

  static String saveSrcFile(String srcUrl )throws IOException {

     URL url = new URL(srcUrl);      
     InputStream is = url.openStream();

     String fileName=null;
     String tmpFileName=tmpFolder + "gdem_" + System.currentTimeMillis() + ".xml";

     File file =new File(tmpFileName);
     FileOutputStream fos=new FileOutputStream(file);
      
      int bufLen = 0;
      byte[] buf = new byte[1024];
      
      while ( (bufLen=is.read( buf ))!= -1 )
        fos.write(buf, 0, bufLen );
     
      fileName=tmpFileName;
      is.close();
      fos.flush(); fos.close();

      return fileName;

  }

 /**
  * Stores String in a file
  */
  static String saveStrToFile(String str, String ext) throws IOException {
    String tmpFileName=tmpFolder + "gdem_" + System.currentTimeMillis() + "." + ext;
    FileWriter fos = new FileWriter(new File(tmpFileName));
    fos.write(str);
    fos.flush(); fos.close();
    return tmpFileName;
  }

  static String readStrFromFile(String fileName) throws java.io.IOException {

    BufferedReader fis = new BufferedReader(new FileReader(fileName)); 
    StringBuffer s = new StringBuffer();
    String line=null;
    while ((line = fis.readLine()) != null) 
      s.append(line + "\n");
     
      fis.close();
      return s.toString();    
  }


  static void deleteFile(String fName) {
    File f = new File(fName);
    f.delete();

  }


   static void log(Object msg) {
    logger.info(msg);
  }

  static boolean isNullStr(String s ) {
    if (s==null || s.trim().equals(""))
      return true;
    else
      return false;
  } 
}