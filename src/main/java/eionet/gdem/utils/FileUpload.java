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
 * The Original Code is "WFTool".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (c) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 *
 * $Id$
 */

package eionet.gdem.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;

/**
 *
 * Provides methods for uploading file from the client computer to the server.
 *
 * File is uploaded to the same computer, where the servlet engine is running. File content is transferred using MIME-multipart HTTP
 * request.
 *
 * @author Rando Valt
 * @version $Revision: 10447 $
 */
public class FileUpload {

    // Objects for synchronizing file locking and session locking
    private static Object fileLock = new Object();
    private static Object SessionIdLock = new Object();

    // integer for generating unique name for temporary file
    private static int HOW_LONG = 6;

    private String _folderName; // tmp folder for files
    private String _fileName;

    // System's line separator
    private static String lineSep;
    // +RV020508
    private int lenRcvd;

    /**
     * Constructor. Creates a new FileUpload object
     *
     * @param String
     *            folderName - folder for the uploaded file
     */
    public FileUpload(String fldName) throws GDEMException {
        if (fldName == null) {
            _folderName = Properties.xslFolder; // props.getString("xsl.folder");
        } else {
            _folderName = fldName;
        }

        lineSep = System.getProperty("line.separator");
    }

    /**
     * Generates filename
     *
     * @param fileName
     *            , n >0, if file with the same name already exists in the tmp folder ex: genFileName( test.xls, 1 )= test_1.xls
     *            genFileName( test_1.xls, 2 )= test_2.xls
     */
    private String genFileName(String fileName, int n) {
        String ret;
        int pos = fileName.lastIndexOf(".");

        // if name > 1, we have test_1.xsl and have to remove _1
        if (n > 1) {
            int dashPos = fileName.lastIndexOf("_" + (n - 1));
            ret = fileName.substring(0, dashPos) + "_" + n + fileName.substring(pos);
        } else {
            ret = fileName.substring(0, pos) + "_" + n + fileName.substring(pos);
        }

        return ret;
    }

    /**
     * Reads line from ServletInputStream
     *
     * Returns null, if EOF is reached.
     */
    // +RV020508 removed exception handling and propagated exceptions to caller
    private String readLine(byte buf[], int i[], ServletInputStream stream, String charEncoding) throws IOException {
        i[0] = stream.readLine(buf, 0, buf.length); // may throw IOException
        if (i[0] == -1) {
            return null;
        }

        // +RV020508
        lenRcvd += i[0];
        //
        try {
            if (charEncoding == null) {
                return new String(buf, 0, i[0]);
            }
            else {
                return new String(buf, 0, i[0], charEncoding); // may throw UnsupportedEncodingException
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Returns filename, uploaded to the server
     */
    public String getFileName() {
        return _fileName;
    }

    private void setFileName(String name) {
        _fileName = name;
    }

    /**
     * Returns filename from filename with full path in: "C:\TEMP\test.txt" out: "test.txt"
     */
    private String getFileName(String fileName) {
        int i = fileName.lastIndexOf("\\");
        if (i < 0 || i >= fileName.length() - 1) {
            i = fileName.lastIndexOf("/");
            if (i < 0 || i >= fileName.length() - 1) {
                return fileName;
            }
        }

        fileName = fileName.substring(i + 1);
        return fileName;
    }

    /**
     * Returns unique number, used for temporary file name
     */
    private String getSessionId() {
        String s = "";
        synchronized (SessionIdLock) {
            long l = System.currentTimeMillis();
            Random random = new Random();
            s = String.valueOf(l);
            for (int i = 1; i <= HOW_LONG; i++) {
                s = s + (int) (1.0D + HOW_LONG * random.nextDouble());
            }
        }
        return s;
    }

    /**
     * Uploads file from client to the server Parses HttpRequestInputstream and writes bytes to the specified folder in the same
     * computer, where servlet runs
     *
     * @param HttpRequestInputstream
     */
    public File uploadFile(HttpServletRequest req) throws GDEMException {

        // helper arrays
        byte[] bt1 = new byte[4096];
        byte[] bt2 = new byte[4096];

        int[] int1 = new int[1];
        int[] int2 = new int[1];
        ServletInputStream si = null;

        try {
            si = req.getInputStream();
            String contentType = req.getContentType();
            String charEncoding = req.getCharacterEncoding();
            // +RV020508
            int contentLength = req.getContentLength();
            lenRcvd = 0;
            if (contentLength == -1) {
                throw new GDEMException("Invalid HTTP POST. Content length is unknown.");
            }
            //
            int boundaryPos;
            if ((boundaryPos = contentType.indexOf("boundary=")) != -1) {
                contentType = contentType.substring(boundaryPos + 9);
                contentType = "--" + contentType;
            } // end if

            // Find filename
            String fileName;
            while ((fileName = readLine(bt1, int1, si, charEncoding)) != null) {
                int i = fileName.indexOf("filename=");
                if (i >= 0) {
                    fileName = fileName.substring(i + 10);
                    i = fileName.indexOf("\"");
                    if (i > 0) {
                        FileOutputStream fileOut = null;
                        boolean fWrite = false;
                        File tmpFile = new File(_folderName, getSessionId());
                        try{

                            fileName = fileName.substring(0, i);

                            fileName = getFileName(fileName);

                            // _fileName is returned by getFileName() method
                            _fileName = fileName;

                            String line2 = readLine(bt1, int1, si, charEncoding);
                            if (line2.indexOf("Content-Type") >= 0) {
                                readLine(bt1, int1, si, charEncoding);
                            }

                            fileOut = new FileOutputStream(tmpFile);

                            String helpStr = null;
                            long l = 0L;

                            // changes to true, if something is written to the output file
                            // remains false, if user has entered a wrong filename or the file's size is 0kB


                            // parse the file in the MIME message
                            while ((line2 = readLine(bt1, int1, si, charEncoding)) != null) {
                                if (line2.indexOf(contentType) == 0 && bt1[0] == 45) {
                                    break;
                                }
                                if (helpStr != null && l <= 75L) {
                                    fWrite = true;
                                    fileOut.write(bt2, 0, int2[0]);
                                    fileOut.flush();
                                } // endif
                                helpStr = readLine(bt2, int2, si, charEncoding);
                                if (helpStr == null || helpStr.indexOf(contentType) == 0 && bt2[0] == 45) {
                                    break;
                                }

                                fWrite = true;
                                fileOut.write(bt1, 0, int1[0]);
                                fileOut.flush();
                            } // end while

                            byte bt0;

                            if (lineSep.length() == 1) {
                                bt0 = 2;
                            } else {
                                bt0 = 1;
                            }

                            if (helpStr != null && bt2[0] != 45 && int2[0] > lineSep.length() * bt0) {
                                fileOut.write(bt2, 0, int2[0] - lineSep.length() * bt0);
                                fWrite = true;
                            }
                            if (line2 != null && bt1[0] != 45 && int1[0] > lineSep.length() * bt0) {
                                fileOut.write(bt1, 0, int1[0] - lineSep.length() * bt0);
                                fWrite = true;
                            }
                        }
                        finally{
                            IOUtils.closeQuietly(fileOut);
                        }
                        if (fWrite) {
                            try {
                                synchronized (fileLock) {
                                    File file = new File(_folderName, fileName);
                                    int n = 0;
                                    while (file.exists()) {
                                        n++;
                                        fileName = genFileName(fileName, n);
                                        file = new File(_folderName, fileName);
                                    }
                                    setFileName(fileName);
                                    try {
                                        file.delete();
                                    } catch (Exception _ex) {
                                        throw new GDEMException("Error deleting temporary file: " + _ex.toString());
                                    }
                                    tmpFile.renameTo(file);
                                    return file;
                                } // sync
                            } catch (Exception _ex) {
                                throw new GDEMException("Error renaming temporary file: " + _ex.toString());
                            }

                        } // end-if file = 0kb or does not exist
                        else {
                            tmpFile.delete();
                            throw new GDEMException("File: " + fileName + " does not exist or contains no data.");
                        }

                    }
                    // break;
                } // end if (filename found)
            } // end while
            // +RV020508
            if (contentLength != lenRcvd) {
                throw new GDEMException("Canceled upload: expected " + contentLength + " bytes, received " + lenRcvd + " bytes.");
            }
        } catch (IOException e) {
            throw new GDEMException("Error uploading file: " + e.toString());
        }
        finally{
            IOUtils.closeQuietly(si);
        }

        return null;
    }

}
