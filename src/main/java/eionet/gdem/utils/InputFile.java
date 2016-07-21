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

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IHostDao;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

/**
 * Utility class for reading files from URL.
 * The class reads the host credentials from database and if it runs in trusted mode, then it passes the basic authentication info
 * to remote server for files with limited access.
 *
 * NB! Always call close() method in finally block, otherwise the InputStream stays open.
 */
public class InputFile {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(InputFile.class);

    /**
     * Authentication ticket: Base64 encoded username and password.
     */
    private String ticket = null;
    /**
     * URL of the inpurt file.
     */
    private URL url = null;
    /**
     * InputStream of input file data retrieved from URLConnection.
     */
    private InputStream inputStream = null;
    /**
     * Allowed to use ticket when requesting input file.
     */
    private boolean isTrustedMode = false;
    /**
     * File name without extension extracted from URL.
     */
    private String strFileName = null;
    /**
     * File name extracted from URL.
     */
    private String strFileNameNoExtension = null;
    /**
     * Host name extracted from URL.
     */
    private String strHostName = null;
    /**
     * Folder name (path before file name) extracted from URL.
     */
    private String strFolderName = null;
    /**
     * Status of InputStream.
     */
    boolean isClosed = false;
    /**
     * Store the content of URL in local storage before streaming the result to requester.
     */
    boolean storeLocally = false;
    /**
     * Location in local storage if the file is stored locally temporarily.
     */
    String tmpFileLocation = null;

    /**
     * DAO for getting authorisation for known hosts.
     */
    private IHostDao hostDao = GDEMServices.getDaoService().getHostDao();

    /**
     * Initializes InputUrl object and sets the URI from str_url.
     *
     * @param strUrl - the URL of source file
     * @throws MalformedURLException wrong URL.
     */
    public InputFile(String strUrl) throws MalformedURLException {

        // Java's URL class doesn't escape certain characters with % +hexidecimal digits.
        // This is a bug in the class java.net.URL.
        // The correct way to create a URL object is to use class called java.net.URI (Java 1.4 and later).

        // this.url = new URL(str_url);
        setURL(strUrl);
    }

    /**
     * Get source file from url as InputStream user basic auth, if we know the credentials.
     *
     * @return InputStream of source file.
     * @throws IOException the source is not available.
     */
    public InputStream getSrcInputStream() throws IOException {
        fillInputStream();
        return inputStream;
    }

    /**
     * Save the InputFile to the specified text file with default extension.
     *
     * @return Full path of file.
     * @throws IOException if it's not possible to save file in the filesystem.
     */
    public String saveSrcFile() throws IOException {
        return saveSrcFile("xml");
    }

    /**
     * Save the InputFile to the specified text file with given extension.
     *
     * @param extension file extension to use when storing source file temporarily.
     * @return Full path of file.
     * @throws IOException if it's not possible to save file in the filesystem.
     */
    public String saveSrcFile(String extension) throws IOException {

        fillInputStream();
        String fileName = saveFileInLocalStorage(extension);

        return fileName;

    }

    /**
     * Closes InputStream of input file.
     */
    public void close() {
        try {
            if (inputStream != null && !isClosed) {
                inputStream.close();
                isClosed = true;
                if (storeLocally && tmpFileLocation != null) {
                    FileUtils.deleteQuietly(new File(tmpFileLocation));
                    LOGGER.info("Deleted temporary file: " + tmpFileLocation);
                } else {
                    LOGGER.info("Closed inputstream for URL: " + url.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Closing inputstream in FileInput: " + e.toString());
        }

    }

    /**
     * Sets the authentication ticket for the source file.
     *
     * @param ticket authentication value.
     */
    public void setAuthentication(String ticket) {
        this.ticket = ticket;
    }

    /**
     * Sets the boolean to use authentication ticket for grabbing the source file or not.
     *
     * @param mode If true - use ticket
     */
    public void setTrustedMode(boolean mode) {
        this.isTrustedMode = mode;
    }

    /**
     * Extracts the file name from URL path. E.g: BasicQuality.xml where the full url is
     * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
     *
     * @return file name
     */
    public String getFileName() {
        return strFileName;
    }

    /**
     * Extracts the file name without file extension from URL path. E.g: BasicQuality where the full url is
     * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
     *
     * @return file name without extension
     */
    public String getFileNameNoExtension() {
        return strFileNameNoExtension;
    }

    /**
     * Return source file URL as a String.
     *
     * @return source URL as String
     */
    @Override
    public String toString() {
        return (url == null) ? null : url.toString();
    }

    /**
     * Extracts the full host name from URL. E.g: http://cdrtest.eionet.europa.eu where the full url is
     * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
     *
     * @return host name from source URL.
     */
    public String getHostName() {
        return strHostName;
    }

    /**
     * Extracts the folder from URL path. E.g: /al/eea/colrjhlyq/envrjhqwa where the full url is
     * http://cdrtest.eionet.europa.eu/al/eea/colrjhlyq/envrjhqwa/BasicQuality.xml
     *
     * @return path without file name
     */
    public String getFolderName() {
        return strFolderName;
    }

    /**
     * Extracts CDR file info from URL and returns it as a map of parameters. If the source file is a file from CDR then the Map
     * contains the following parameters: envelopeurl, envelopepath, instance, filename
     *
     * @return Map of parameters extracted from URL and can be sent to Conversion Service.
     */
    public Map<String, String> getCdrParams() {
        String strEnvelopeUrl = null;
        String strInstance = null;
        Map<String, String> h = new HashMap<String, String>();
        if (getHostName() != null && getFolderName() != null) {
            strEnvelopeUrl = getHostName().concat(getFolderName());
        }
        if (getHostName() != null && getFolderName() != null && getFileName() != null) {
            strInstance =
                    getHostName().concat(getFolderName()).concat(getFolderName().endsWith("/") ? "" : "/").concat(getFileName());
        }
        h.put("filename", getFileName());
        h.put("envelopeurl", strEnvelopeUrl);
        h.put("envelopepath", getFolderName());
        h.put("instance", strInstance);

        return h;
    }

    public URL getURL() {
        return this.url;
    }

    /**
     * Get the authentication ticket for the source file, if available.
     *
     * @return authentication ticket
     */
    public String getAuthentication() {
        if (Utils.isNullStr(ticket) && isTrustedMode) {
            String host = url.getHost();
            getHostCredentials(host);
        }
        return ticket;
    }

    /**
     * Is the URL content stored locally.
     *
     * @return true if the file is stored locally.
     */
    public boolean isStoreLocally() {
        return storeLocally;
    }

    /**
     * Set the flag to store URL content locally.
     *
     * @param storeLocally status flag to store the file locally.
     */
    public void setStoreLocally(boolean storeLocally) {
        this.storeLocally = storeLocally;
    }
/*
     * PRIVATE METHODS
     */

    /**
     * Get Host credentials from database. There could be restriction for accessing files in different servers. Username and
     * password are saved in the T_HOST table for these cases.
     *
     * @param host URL.
     */
    private void getHostCredentials(String host) {
        try {

            Vector v = hostDao.getHosts(host);

            if (v == null) {
                return;
            }
            if (v.size() > 0) {
                Hashtable h = (Hashtable) v.get(0);
                String user = (String) h.get("user_name");
                String pwd = (String) h.get("pwd");
                this.ticket = Utils.getEncodedAuthentication(user, pwd);

            }

        } catch (Exception e) {
            LOGGER.error("Error getting host data from the DB " + e.toString());
            LOGGER.error("Conversion proceeded");
        }
    }

    /**
     * Opens URLConnection and reads the source into InputStream.
     *
     * @throws IOException in case the reading of InuptStream from URLConnection fails.
     */
    private void fillInputStream() throws IOException {

        isClosed = false;
        URLConnection uc = url.openConnection();

        ticket = getAuthentication();
        uc.addRequestProperty("Accept", "*/*");

        if (ticket != null) {
            // String auth = Utils.getEncodedAuthentication(user,pwd);
            uc.addRequestProperty("Authorization", " Basic " + ticket);
        }
        LOGGER.info("Start download file: " + url.toString());
        this.inputStream = uc.getInputStream();

        if (storeLocally) {
            tmpFileLocation = saveFileInLocalStorage("tmp");
            isClosed = false;
            inputStream = new FileInputStream(tmpFileLocation);
        }
    }

    /**
     * Stores the URL of remote file.
     *
     * @param strUrl URL of input file
     * @throws MalformedURLException Invalid URL.
     */
    private void setURL(String strUrl) throws MalformedURLException {
        try {
            URI uri = new URI(escapeSpaces(strUrl));
            parseUri(uri);

            this.url = uri.toURL();

        } catch (URISyntaxException ue) {
            throw new MalformedURLException(ue.toString());
        } catch (IllegalArgumentException ae) {
            throw new MalformedURLException(ae.toString());
        }
    }

    /**
     * Escape spaces with %20 in source URI.
     *
     * @param strUri URI of input file.
     * @return Escaped URI.
     */
    private String escapeSpaces(String strUri) {
        return Utils.Replace(strUri, " ", "%20");
    }

    /**
     * Extracts filename from URI's path [scheme:][//authority][path][?query][#fragment].
     *
     * @param uri URI of input file.
     */
    private void parseUri(URI uri) {

        this.strHostName = uri.getScheme() + "://" + uri.getAuthority();
        findFileName(uri.getPath());
    }

    /**
     * Extracts filename and folder from URI's path.
     *
     * @param strUri URI of input file.
     */
    private void findFileName(String strUri) {

        String fileName = null;
        String folderName = null;
        if (Utils.isNullStr(strUri)) {
            return;
        }

        if (strUri.endsWith("/")) {
            strUri = strUri.substring(0, strUri.length() - 1);
        }

        int lastSlash = strUri.lastIndexOf("/");

        if (lastSlash > -1) {
            fileName = strUri.substring(lastSlash + 1);
            folderName = strUri.substring(0, lastSlash);
        } else {
            fileName = strUri;
            folderName = "";
        }

        findFileNameNoExtension(fileName);

        this.strFileName = fileName;
        this.strFolderName = folderName;
    }

    /**
     * Extracts filename without file extension from URI's path.
     *
     * @param strFileName File name extracted from URI of input file.
     */
    private void findFileNameNoExtension(String strFileName) {

        String name = null;
        if (Utils.isNullStr(strFileName)) {
            return;
        }

        int lastDot = strFileName.lastIndexOf(".");

        if (lastDot > -1) {
            name = strFileName.substring(0, lastDot);
        } else {
            name = strFileName;
        }

        this.strFileNameNoExtension = name;
    }

    /**
     * Saves the inputStream as local file in tmp folder.
     *
     * @param extension file extension
     * @return full path to file
     * @throws IOException if file system operation fails.
     */
    private String saveFileInLocalStorage(String extension) throws IOException {
        String fileName =
                Properties.tmpFolder + File.separatorChar + "gdem_" + System.currentTimeMillis() + "-" + UUID.randomUUID() + "."
                        + extension;
        File file = new File(fileName);

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            LOGGER.info("File stored locally url=" + url.toString() + " at " + fileName);
        } finally {
            close();
            IOUtils.closeQuietly(outputStream);
        }

        return fileName;

    }
}
