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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import eionet.gdem.GDEMException;

/**
 * Provides methods for uploading file from the client computer to the server. using commons-fileupload-0.1.jar
 *
 * File is uploaded to the same computer, where the servlet engine is running. File content is transferred using MIME-multipart HTTP
 * request.
 *
 * @author Enriko Käsper
 * @version $Revision: 10280 $
 */
public class MultipartFileUpload {

    // Objects for synchronizing file locking and session locking
    // private static Object fileLock = new Object();
    // private static Object SessionIdLock = new Object();

    // integer for generating unique name for temporary file
    // private static int HOW_LONG = 6;
    private static final String DEFAULT_ENCODING = "UTF-8";

    /** tmp folder for files. */
    private String _folderName;
    private String _fileName;
    private FileItem _fileItem;
    private DiskFileItemFactory factory = null;
    private ServletFileUpload upload;
    private boolean _uploadAtOnce = true;

    private HashMap _params = null;
    private List fileItems = null;
    private String encoding = null;
    private HashMap fileNameEscapes = new HashMap();
    private static char[] restrictedChars = {'\\', '/', ':', '*', '?', '"', '<', '>', '|', '\'', '&', '%', '#', ';'};

    /**
     * Constructor. Creates a new FileUploadAdapter object
     *
     */
    public MultipartFileUpload(boolean uploadAtOnce) {

        // Create a factory for disk-based file items
        factory = new DiskFileItemFactory();

        // Create a new file upload handler
        upload = new ServletFileUpload(factory);
        _params = new HashMap();
        initEscapes();
        this._uploadAtOnce = uploadAtOnce;
    }

    public MultipartFileUpload() {
        new MultipartFileUpload(true);
    }

    /**
     * Sets folder name where to insert uploaded file.
     *
     * @param fldName - folder for the uploaded file
     */
    public void setFolder(String fldName) {
        _folderName = fldName;
    }

    /**
     *
     */
    public HashMap getRequestParams() {
        return _params;
    }

    /**
     * Returns filename from request.
     */
    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String name) {
        _fileName = name;
    }

    /**
     * Checks whether file with the specified filename already exists in the destination folder.
     */
    public boolean getFileExists() {

        if (_fileName == null || _folderName == null)
            return false;

        File file = new File(_folderName, _fileName);

        if (file == null)
            return false;

        return file.exists();

    }

    /**
     * @param request
     *            Servlet request
     * @throws GDEMException
     *             If an error occurs
     */
    public void processMultiPartRequest(HttpServletRequest request) throws GDEMException {

        List items = null;
        setEncoding(request.getCharacterEncoding());
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException fue) {
            throw new GDEMException(fue.toString());
        }

        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            // String itemName = item.getName();
            String fieldName = item.getFieldName();
            // String id = (String) this.parameterNames.get(fieldName);

            /*
             * System.out.println("Multipart item name is: " + itemName + " and fieldname is: " + fieldName); // + " and id is: " +
             * id); System.out.println("Is formfield: " + item.isFormField()); System.out.println("Content: " + item.getString());
             */
            if (item.isFormField()) {
                // It's a field name, it means that we got a non-file
                // form field. Upload is not required.
                String itemValue = null;
                try {
                    // use encoding from request
                    itemValue = item.getString(getEncoding());
                } catch (UnsupportedEncodingException e) {
                    // use default encoding
                    itemValue = item.getString();
                }

                if (_params.containsKey(fieldName)) {
                    Object curObj = _params.get(fieldName);
                    List valueList = null;

                    if (curObj instanceof String) {
                        valueList = new ArrayList();
                        valueList.add((String) curObj);
                    } else if (curObj instanceof List) {
                        valueList = (List) curObj;
                    }
                    valueList.add(itemValue);
                    _params.put(fieldName, valueList);
                } else {
                    _params.put(fieldName, itemValue);
                }
            } else {
                _fileItem = item;
                addFileItem(_fileItem);
                String fileName = getFileItemName(_fileItem.getName());
                setFileName(fileName);
                if (_uploadAtOnce)
                    saveFile();
            }
        }
    }

    /**
     * Stores uploaded file in the filesystem with the original filename. If the file with the same name exisits, appends next
     * available number at the end of the filename.
     *
     * @return File name
     *
     * @throws GDEMException Thrown in case of missing data or error during file writing.
     */
    public String saveFile() throws GDEMException {

        String fileName = getFileName();

        if (_folderName == null)
            throw new GDEMException("Folder name is empty!");
        if (fileName == null)
            throw new GDEMException("File name is empty!");
        if (_fileItem == null)
            throw new GDEMException("No files found!");
        factory.setRepository(new File(_folderName));

        if (_fileItem.getSize() == 0)
            return null; // There is nothing to save, file size is 0

        File file = new File(_folderName, fileName);// getUniqueFile(_folderName, fileName);
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

    /**
     * Stores uploaded file in the filesystem with specified name. Renames the existing file, if needed. Otherwise overwrites
     * exisitng file
     *
     * @param saveAs Destination file name.
     * @param keepExisting true, if rename existing file before saving the new faile. false, if overwrite exisitng file
     * @throws GDEMException Thrown in case of missing data or error during file writing.
     */
    public void saveFileAs(String saveAs, boolean keepExisting) throws GDEMException {

        File file = null;

        if (_folderName == null)
            throw new GDEMException("Folder name is empty!");
        if (_fileItem == null)
            throw new GDEMException("No files found!");

        factory.setRepository(new File(_folderName));

        if (_fileItem.getSize() == 0)
            return; // There is nothing to save, file size is 0

        if (saveAs == null)
            saveAs = getFileName();

        file = new File(_folderName, saveAs);

        if (file.exists()) {
            if (keepExisting) {
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
     * Generates filename.
     *
     * @param fileName
     * @param n set larger than 0, if file with the same name already exists in the tmp folder ex: genFileName( test.xls, 1 )= test_1.xls
     *            genFileName( test_1.xls, 2 )= test_2.xls
     */
    public static String getGeneratedFileName(String fileName, int n) {
        String ret;
        int pos = fileName.lastIndexOf(".");

        int dashPos = fileName.lastIndexOf("_");
        if (dashPos > 1 && dashPos < pos) {
            String snum = fileName.substring(dashPos + 1, pos);
            try {
                int inum = Integer.parseInt(snum);
                ret = fileName.substring(0, dashPos) + "_" + (inum + 1) + fileName.substring(pos);
            } catch (Exception e) {
                ret = fileName.substring(0, pos) + "_" + n + fileName.substring(pos);
            }
        } else {
            ret = fileName.substring(0, pos) + "_" + n + fileName.substring(pos);
        }

        return ret;
    }

    /**
     * Finds unique filename using genFileName method.
     *
     * @param folderName
     *            Folder where the file will be stored
     * @param fileName
     *            File name that should be used for generating the unique filename
     *
     * @return Filename that does not exist in the folder
     */
    public static File getUniqueFile(String folderName, String fileName) {

        int n = 0;
        File file = new File(folderName, fileName);
        while (file.exists()) {
            n++;
            fileName = getGeneratedFileName(fileName, n);
            file = new File(folderName, fileName);
        }

        return file;
    }

    public static String getUniqueFileName(String folderName, String fileName) {
        File file = getUniqueFile(folderName, fileName);
        String strFileName = file.getName();
        return strFileName;
    }

    /**
     * Returns filename from filename with full path in: "C:\TEMP\test.txt" out: "test.txt".
     */
    private String getFileItemName(String fileName) {
        int i = fileName.lastIndexOf("\\");
        if (i < 0 || i >= fileName.length() - 1) {
            i = fileName.lastIndexOf("/");
            if (i < 0 || i >= fileName.length() - 1)
                return getEscapedItemName(fileName);
        }

        // fileName = Utils.escapeFileNsame(fileName.substring(i + 1));
        fileName = fileName.substring(i + 1);
        return getEscapedItemName(fileName);
    }

    /**
     * Appends current date value at the end of the filename.
     *
     * @param folderName
     *            Folder where the file will be stored
     * @param fileName
     *            File name that should be used for generating the unique filename
     *
     * @return Filename with appended date (in format yyMMddHHmmss)
     */
    private File getDateAappendedFile(String folderName, String fileName) {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyMMddHHmmss");
        String dateVal = sdf.format(new Date());

        int pos = fileName.lastIndexOf(".");
        StringBuffer buf = new StringBuffer();
        buf.append(fileName.substring(0, pos));
        buf.append("_");
        buf.append(dateVal);
        buf.append(fileName.substring(pos));

        return getUniqueFile(folderName, buf.toString());

    }

    /**
     * Stores fileItems into list.
     */
    private void addFileItem(FileItem item) {
        if (fileItems == null)
            fileItems = new ArrayList();

        fileItems.add(item);

    }

    /**
     * returns file item for sepcified fieldName.
     */
    private FileItem getFileItem(String fieldName) {
        if (fileItems == null)
            fileItems = new ArrayList();

        for (int i = 0; i < fileItems.size(); i++) {
            FileItem item = (FileItem) fileItems.get(i);
            if (item.getFieldName().equalsIgnoreCase(fieldName))
                return item;
        }

        return null;

    }

    /**
     * returns the file name for specified fieldName. Useful for posting several fileitems.
     *
     * @param fieldName
     *            - file item field name
     * @return
     */
    public String getFileName(String fieldName) {
        String strRet = null;

        if (fileItems == null || fieldName == null)
            return strRet;

        for (int i = 0; i < fileItems.size(); i++) {
            FileItem item = (FileItem) fileItems.get(i);
            if (item.getFieldName().equalsIgnoreCase(fieldName))
                return getFileItemName(item.getName());
        }
        return strRet;
    }

    /**
     * Stores uploaded file in the filesystem with the original filename. If the file with the same name exisits, appends next
     * available number at the end of the filename.
     *
     * @param fieldName
     *            - file item field name
     * @param folderName
     *            - target folder
     *
     * @return File name
     * @throws GDEMException
     *             Thrown in case of missing data or error during file writing.
     */
    public String saveFile(String fieldName, String folderName) throws GDEMException {

        folderName = (folderName == null) ? _folderName : folderName;
        if (folderName == null)
            throw new GDEMException("Folder name is empty!");

        FileItem fileItem = getFileItem(fieldName);
        if (fileItem == null)
            throw new GDEMException("No files found!");

        String fileName = getFileItemName(fileItem.getName());
        if (fileName == null)
            throw new GDEMException("File name is empty!");

        factory.setRepository(new File(folderName));

        if (fileItem.getSize() == 0)
            return null; // There is nothing to save, file size is 0

        File file = getUniqueFile(folderName, fileName);
        fileName = file.getName();

        if (fileItem.getSize() > 0) {
            try {
                fileItem.write(file);
            } catch (Exception e) {
                throw new GDEMException(e.toString());
            }
        }
        return fileName;
    }

    public InputStream getFileAsInputStream(String fieldName) throws GDEMException, IOException {

        FileItem fileItem = getFileItem(fieldName);
        if (fileItem == null)
            throw new GDEMException("No files found!");

        if (fileItem.getSize() == 0)
            throw new GDEMException("File size is 0KB!"); // There is nothing to save, file size is 0

        return fileItem.getInputStream();
    }

    public String getEncoding() {
        if (encoding == null)
            encoding = DEFAULT_ENCODING;
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFolderName() {
        return this._folderName;
    }

    private String getEscapedItemName(String itemName) {
        String fileItemName = null;
        try {
            // use encoding from request
            fileItemName = new String(itemName.getBytes(), getEncoding());
        } catch (UnsupportedEncodingException e) {
            // use default encoding
            fileItemName = itemName;
        }
        return parseFileName(fileItemName);

    }

    /**
     * eemaldab faili nimest veidrad symbolid
     *
     * @param fileName
     * @return
     */
    private String parseFileName(String fileName) {
        StringBuffer ret = new StringBuffer();

        int code = 0;
        int lastCode = 0;

        for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);
            code = Character.valueOf(fileName.charAt(i));
            if (code == 63 && lastCode == 65533) {
                ret.append("s");
            } else if (fileNameEscapes.containsKey(new Integer(code))) {
                ret.append(fileNameEscapes.get(code));
            } else if (code > 127 || isRestrictedChar(c)) {
                ret.append("-");
            } else {
                ret.append(c);
            }
            // System.out.println(c + "=" + code);
            lastCode = code;
        }
        return ret.toString();
    }

    private void initEscapes() {
        fileNameEscapes.put(352, "S");
        fileNameEscapes.put(381, "Z");
        fileNameEscapes.put(382, "z");
        fileNameEscapes.put(252, "u");
        fileNameEscapes.put(245, "o");
        fileNameEscapes.put(246, "o");
        fileNameEscapes.put(228, "a");
        fileNameEscapes.put(213, "O");
        fileNameEscapes.put(214, "O");
        fileNameEscapes.put(196, "A");
    }

    private boolean isRestrictedChar(char c) {
        for (int i = 0; i < restrictedChars.length; i++) {
            if (c == restrictedChars[i])
                return true;
        }
        return false;
    }
}
