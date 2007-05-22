/**
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
import java.io.UnsupportedEncodingException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
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
   * Returns filename from request
   */
  public String getFileName() {
    return _fileName;
  }


  public void setFileName(String name){
    _fileName = name;
  }

  /**
   * Checks whether file with the specified filename already exists in the destination folder
   */
  public boolean getFileExists() {

	if (_fileName==null || _folderName==null) return false;

	File file = new File(_folderName, _fileName);

	if (file==null) return false;

	return file.exists();

  }

  /**
     * @param request Servlet request
     * @return the calculated trigger
     * @throws XFormsException If an error occurs
     */
    public void processMultiPartRequest(HttpServletRequest request) throws GDEMException {

        List items = null;
    	String encoding = request.getCharacterEncoding();
    	if (encoding==null) encoding="UTF-8";
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
            	try{
            		//use encoding from request
            		_params.put(fieldName, item.getString(encoding));
            	}
            	catch(UnsupportedEncodingException e){
            		//use default encoding
            		_params.put(fieldName, item.getString());
            	}

            } else {
                _fileItem = item;
            	String fileName = getFileItemName(_fileItem.getName());
            	setFileName(fileName);
                if (_uploadAtOnce)
                    saveFile();
           }
      }
    }
    /*
     * Stores uploaded file in the filesystem with the original filename.
     * If the file with the same name exisits, appends next available number at the end of the filename.
     *
	 * @return      			File name
	 * @throws GDEMException    Thrown in case of missing data or error during file writing.
     */
    public String saveFile() throws GDEMException {

        String fileName = getFileName();

        if (_folderName==null) throw new GDEMException("Folder name is empty!");
        if (fileName==null) throw new GDEMException("File name is empty!");
        if (_fileItem==null)  throw new GDEMException("No files found!");
        upload.setRepositoryPath(_folderName);

        if (_fileItem.getSize()==0) return null; //There is nothing to save, file size is 0

        File file = getUniqueFile(_folderName, fileName);
        fileName = file.getName();


        if (_fileItem.getSize() > 0) {
          try {
            _fileItem.write(file);
          } catch (Exception e) {
            throw new GDEMException(e.toString());
          }
        }
        return fileName;
    }

    /*
     * Stores uploaded file in the filesystem with specified name. Renames the existing file, if needed.
     * Otherwise overwrites exisitng file
     *
	 * @param destFileName      Destination file name.
	 * @param keepExisitng      true, if rename existing file before saving the new faile.
	 * 							false, if overwrite exisitng file
	 * @throws GDEMException    Thrown in case of missing data or error during file writing.
     */
    public void saveFileAs(String saveAs, boolean keepExisting) throws GDEMException {

    	File file = null;

        if (_folderName==null) throw new GDEMException("Folder name is empty!");
        if (_fileItem==null)  throw new GDEMException("No files found!");

        upload.setRepositoryPath(_folderName);

        if (_fileItem.getSize()==0) return; //There is nothing to save, file size is 0

        if (saveAs==null)
        	saveAs = getFileName();

        file = new File(_folderName, saveAs);

        if (file.exists()){
        	if (keepExisting){
        		File uniqueFile = getDateAappendedFile(_folderName, saveAs);
        		file.renameTo(uniqueFile);
        	}
        }


        if (_fileItem.getSize() > 0) {
          try {
            _fileItem.write(file);
          } catch (Exception e) {
            throw new GDEMException(e.toString());
          }
        }
    }
    /**
     * Generates filename
     *
     * @param fileName, n >0, if file with the same name already exists in the tmp folder
     * ex: genFileName( test.xls, 1 )= test_1.xls
     *     genFileName( test_1.xls, 2 )= test_2.xls
     */
    private String getGeneratedFileName(String fileName, int n){
      String ret ;
      int pos = fileName.lastIndexOf(".");


      int dashPos = fileName.lastIndexOf( "_" );
      if (dashPos>1 && dashPos<pos){
          String snum = fileName.substring(dashPos+1, pos);
          try{
        	  int inum = Integer.parseInt(snum);
              ret = fileName.substring(0, dashPos ) + "_" + (inum+1) + fileName.substring( pos);
          }
          catch(Exception e){
              ret = fileName.substring(0, pos ) + "_" + n + fileName.substring( pos);
          }
      }
      else{
          ret = fileName.substring(0, pos ) + "_" + n + fileName.substring( pos);
      }

      return ret;
    }

    /**
     * Finds unique filename using genFileName method
     *
     * @param folderName	Folder where the file will be stored
     * @param fileName	File name that should be used for generating the unique filename

     * @return 			Filename that does not exist in the folder
     */
    private File getUniqueFile(String folderName, String fileName){

  	  int n =0;
  	  File file = new File(folderName, fileName);
  	  while ( file.exists()){
  		  n++;
  		  fileName = getGeneratedFileName( fileName , n) ;
  		  file = new File(_folderName, fileName);
  	  }

  	  return file;
    }
    /**
     * Returns filename from filename with full path
     * in: "C:\TEMP\test.txt"
     * out: "test.txt"
     */
    private String getFileItemName(String fileName) {
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
     * Appends current date value at the end of the filename
     *
     * @param folderName	Folder where the file will be stored
     * @param fileName	File name that should be used for generating the unique filename

     * @return 			Filename with appended date (in format yyMMddHHmmss)
     */
    private File getDateAappendedFile(String folderName, String fileName){

    	SimpleDateFormat sdf = new SimpleDateFormat();
    	sdf.applyPattern("yyMMddHHmmss");
    	String dateVal = sdf.format(new Date());

        int pos = fileName.lastIndexOf(".");
    	StringBuffer buf = new StringBuffer();
    	buf.append(fileName.substring(0, pos ));
    	buf.append("_");
    	buf.append(dateVal);
    	buf.append(fileName.substring( pos));


        return getUniqueFile(folderName,buf.toString());

    }
}
