/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.utils;

import javax.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import eionet.gdem.GDEMException;

/**
 *
 * Provides methods for uploading file from the client computer
 * to the server. using commons-fileupload-0.1.jar
 *
 * File is uploaded to the same computer, where the servlet engine is running.
 * File content is transferred using MIME-multipart HTTP request.
 *
 * @author Enriko Käsper
 * @version $Revision: 1.2 $
 */
public class MultipartFileUpload{
  
  //Objects for synchronizing file locking and session locking
  //private static Object fileLock = new Object();
  //private static Object SessionIdLock = new Object();
  
  //integer for generating unique name for temporary file
  //private static int HOW_LONG = 6;
  
  
  private String _folderName;		//tmp folder for files
  private String _fileName;
  private FileItem _fileItem;
  private DiskFileUpload upload;
  private boolean _uploadAtOnce=true;
    //System's line separator
  private static String lineSep;
  
  //+RV020508
  //private int lenRcvd;

  private HashMap _params=null;  

  /**
   * Constructor. Creates a new FileUploadAdapter object
   * @param String folderName - folder for the uploaded file
   */
  public MultipartFileUpload(boolean uploadAtOnce){

  //  lineSep = System.getProperty("line.separator");
     upload = new DiskFileUpload();
     _params = new HashMap();
     this._uploadAtOnce=uploadAtOnce;
  }
  public MultipartFileUpload(){ 
      new MultipartFileUpload(true);
  }
  /**
   * Sets folder name where to insert uploaded file 
   *
   * @param String folderName - folder for the uploaded file
   */
  public void setFolder(String fldName){
      _folderName = fldName;  
  }
  /**
   * Sets folder name where to insert uploaded file 
   *
   * @param String folderName - folder for the uploaded file
   */
  public HashMap getRequestParams(){
      return _params;
  }
  /**
   * Generates filename
   *
   * @param fileName, n >0, if file with the same name already exists in the tmp folder
   * ex: genFileName( test.xls, 1 )= test_1.xls
   *     genFileName( test_1.xls, 2 )= test_2.xls
   */
  private String genFileName(String fileName, int n){
    String ret ;
    int pos = fileName.lastIndexOf(".");
    
    // if name > 1, we have test_1.xsl and have to remove _1
    if (n > 1){
      int dashPos = fileName.lastIndexOf( "_" + ( n-1 ) );
      ret = fileName.substring(0, dashPos ) + "_" + n + fileName.substring( pos );
    } else
      ret = fileName.substring(0, pos ) + "_" + n + fileName.substring( pos);
    
    return ret;
  }
  
  /**
   * Returns filename, uploaded to the server
   */
  public String getFileName() {
    return _fileName;
  }
  
  
  private void setFileName(String name){
    _fileName = name;
  }
  
  /**
   * Returns filename from filename with full path
   * in: "C:\TEMP\test.txt"
   * out: "test.txt"
   */
  private String getFileName(String fileName) {
    int i = fileName.lastIndexOf("\\");
    if(i < 0 || i >= fileName.length() - 1) {
      i = fileName.lastIndexOf("/");
      if(i < 0 || i >= fileName.length() - 1)
        return fileName;
    }
    
    fileName =  fileName.substring(i + 1);
    return fileName;
  }
    /**
     * @param request Servlet request
     * @return the calculated trigger
     * @throws XFormsException If an error occurs
     */
    public void processMultiPartRequest(HttpServletRequest request) throws GDEMException {

        List items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException fue) {
            throw new GDEMException(fue.toString());
        }

        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            String itemName = item.getName();
            String fieldName = item.getFieldName();
            //String id = (String) this.parameterNames.get(fieldName);

            /*System.out.println("Multipart item name is: " + itemName
                        + " and fieldname is: " + fieldName);
                       // + " and id is: " + id);
            System.out.println("Is formfield: " + item.isFormField());
            System.out.println("Content: " + item.getString());
            */
            if (item.isFormField()) {
                // It's a field name, it means that we got a non-file
                // form field. Upload is not required. 
                _params.put(fieldName, item.getString());
                
            } else {
                _fileItem = item;
                if (_uploadAtOnce)
                    saveFile();
           }
      }
    }
    public String saveFile() throws GDEMException {

        if (_folderName==null) throw new GDEMException("Folder name is empty!");

        if (_fileItem==null)  throw new GDEMException("No files found!");
        upload.setRepositoryPath(_folderName);

        if (_fileItem.getSize()==0) return null; //There is nothing to save, file size is 0

        String fileName = getFileName(_fileItem.getName());
        File file = new File(_folderName, fileName);
        int n =0;
        while ( file.exists() ){
          n++;
          fileName = genFileName( fileName , n) ;
          file = new File(_folderName, fileName);
        }
        setFileName( fileName );

        byte[] tmpData=_fileItem.get();

        if (_fileItem.getSize() > 0) {
          try {
            _fileItem.write(file);
          } catch (Exception e) {
            throw new GDEMException(e.toString());
          }
        }
        return fileName;
    }
}
