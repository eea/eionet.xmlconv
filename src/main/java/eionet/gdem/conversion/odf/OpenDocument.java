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
 * The Original Code is " GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA).
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 21.07.2006
 */
package eionet.gdem.conversion.odf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import eionet.gdem.utils.xml.tiny.TinyTreeContext;
import eionet.gdem.utils.xml.tiny.TinyTreeXpath;
import org.apache.commons.io.IOUtils;

import eionet.gdem.Properties;
import eionet.gdem.conversion.converters.ConvertContext;
import eionet.gdem.conversion.converters.ConvertStrategy;
import eionet.gdem.conversion.converters.XMLConverter;
import eionet.gdem.utils.Streams;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for OpenDocument documents.
 * @author Unknown
 * @author George Sofianos
 */
public class OpenDocument {

    public static final String ODS_TEMPLATE_FILE_NAME = "template.ods";
    public static final String META_FILE_NAME = "meta.xml";
    public static final String METAXSL_FILE_NAME = "meta.xsl";
    public static final String CONTENT_FILE_NAME = "content.xml";

    public static final Logger LOGGER = LoggerFactory.getLogger(OpenDocument.class);

    private String strWorkingFolder = null;
    private String strMetaFile = null;
    private String strMetaXslFile = null;
    private String strOdsTemplateFile = null;
    private String strOdsOutFile = null;
    private String strContentFile = null;

    /**
     * Default constructor
     */
    public OpenDocument() {

    }

    public void setContentFile(String strContentFile) {
        this.strContentFile = strContentFile;
    }

    /**
     * Creates ODS file.
     * @param strOut Output String
     * @throws Exception If an error occurs.
     */
    public void createOdsFile(String strOut) throws Exception {

        FileOutputStream resultFileOutput = new FileOutputStream(strOut);

        try {
            createOdsFile(resultFileOutput);
        } finally {
            IOUtils.closeQuietly(resultFileOutput);
        }

    }

    /**
     * Method unzips the ods file, replaces content.xml and meta.xml and finally zips it together again
     * @param out OutputStream
     * @throws Exception If an error occurs.
     */
    public void createOdsFile(OutputStream out) throws Exception {

        if (strContentFile == null) {
            throw new Exception("Content file is not set!");
        }

        initOdsFiles();

        FileInputStream result_file_input = null;
        FileOutputStream zip_file_output = new FileOutputStream(strOdsOutFile);
        ZipOutputStream zip_out = new ZipOutputStream(zip_file_output);

        try {
            // unzip template ods file to temp directory
            ZipUtil.unzip(strOdsTemplateFile, strWorkingFolder);
            // copy conent file into temp directory
            Utils.copyFile(new File(strContentFile), new File(strWorkingFolder + File.separator + CONTENT_FILE_NAME));
            // try to transform meta with XSL, if it fails then copy meta file into temp directory
            try {
                convertMetaFile();
            } catch (Throwable t) {
                Utils.copyFile(new File(strMetaFile), new File(strWorkingFolder + File.separator + META_FILE_NAME));
            }
            // zip temp directory
            ZipUtil.zipDir(strWorkingFolder, zip_out);
            zip_out.finish();
            zip_out.close();

            // Fill outputstream
            result_file_input = new FileInputStream(strOdsOutFile);
            Streams.drain(result_file_input, out);

        } catch (IOException ioe) {
            throw new Exception("Could not create OpenDocument Spreadsheet file: " + ioe.toString());
        } finally {
            IOUtils.closeQuietly(zip_out);
            IOUtils.closeQuietly(zip_file_output);
            IOUtils.closeQuietly(result_file_input);
        }
        try {
            // delete working folder and temporary ods file
            Utils.deleteFolder(strWorkingFolder);
            Utils.deleteFile(strOdsOutFile);
        } catch (Exception ioe) {
            // TODO fix logger
            // couldn't delete temp files
        }

    }

    /**
     * Prepares working folder.
     * @throws Exception If an error occurs.
     */
    private void prepareWorkingFolder() throws Exception {

        // get temporary folder
        String tmpFilePath = Properties.tmpFolder;
        if (tmpFilePath == null) {
            throw new Exception("Missing property: tmp.folder");
        } else if (!tmpFilePath.endsWith(File.separator)) {
            tmpFilePath = new File(tmpFilePath).getAbsolutePath() + File.separator;
        }

        // build working folder name
        StringBuffer buf = new StringBuffer(tmpFilePath);
        buf.append("ods_");
        buf.append(Utils.getRandomName());

        // create working folder
        File workginFolder = new File(buf.toString());
        workginFolder.mkdir();

        strWorkingFolder = workginFolder.getAbsolutePath();
    }

    /**
     * ODS files initialization.
     * @throws Exception If an error occurs.
     */
    private void initOdsFiles() throws Exception {

        prepareWorkingFolder();
        if (strWorkingFolder == null) {
            throw new Exception("Working folder is not created!");
        }

        // get ods-folder path
        String odsFolder = Properties.odsFolder;
        if (odsFolder == null) {
            throw new Exception("Missing property: ods.folder");
        } else if (!odsFolder.endsWith(File.separator)) {
            odsFolder = new File(odsFolder).getAbsolutePath() + File.separator;
        }

        String tmpFilePath = Properties.tmpFolder;
        if (tmpFilePath == null) {
            throw new Exception("Missing property: tmp.folder");
        } else if (!tmpFilePath.endsWith(File.separator)) {
            tmpFilePath = new File(tmpFilePath).getAbsolutePath() + File.separator;
        }

        strOdsOutFile = tmpFilePath + "gdem_out" + System.currentTimeMillis() + ".ods";
        strOdsTemplateFile = odsFolder + ODS_TEMPLATE_FILE_NAME;
        strMetaFile = odsFolder + META_FILE_NAME;
        strMetaXslFile = odsFolder + METAXSL_FILE_NAME;
    }

    /**
     * Finds schema-url attributes from content file (stored in xsl) table:table attribute and transforms the values into meta.xml
     * file user defined properties
     * @throws Exception If an error occurs.
     */
    private void convertMetaFile() throws Exception {
        String schemaUrl = null;
        FileOutputStream os = null;
        FileInputStream in = null;
        StringBuffer tableSchemaUrls = new StringBuffer();

        try {
            TinyTreeContext ctx = new TinyTreeContext();
            ctx.setFile(strContentFile);
            TinyTreeXpath xQuery = ctx.getQueryManager();
            xQuery.declareNamespace("table", "http://openoffice.org/2000/table");
            List elements = xQuery.getElementAttributes("table:table");
            for (int i = 0; i < elements.size(); i++) {
                HashMap attr_map = (HashMap) elements.get(i);
                if (attr_map.containsKey(OdsReader.SCHEMA_ATTR_NAME) && Utils.isNullStr(schemaUrl)) {
                    schemaUrl = (String) attr_map.get(OdsReader.SCHEMA_ATTR_NAME);
                }
                if (attr_map.containsKey(OdsReader.TBL_SCHEMAS_ATTR_NAME)) {
                    if (attr_map.containsKey("table:name")) {
                        String schema_url = (String) attr_map.get(OdsReader.TBL_SCHEMAS_ATTR_NAME);
                        String name = (String) attr_map.get("table:name");
                        if (!Utils.isNullStr(schema_url) && !Utils.isNullStr(name)) {
                            tableSchemaUrls.append(OdsReader.TABLE_NAME);
                            tableSchemaUrls.append(name);
                            tableSchemaUrls.append(";");
                            tableSchemaUrls.append(OdsReader.TABLE_SCHEMA_URL);
                            tableSchemaUrls.append(schema_url);
                            tableSchemaUrls.append(";");
                        }
                    }
                }
            }
            if (!Utils.isNullStr(schemaUrl)) {
                os = new FileOutputStream(strWorkingFolder + File.separator + META_FILE_NAME);
                in = new FileInputStream(strMetaFile);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put(OdsReader.SCHEMA_ATTR_NAME, schemaUrl);
                parameters.put(OdsReader.TBL_SCHEMAS_ATTR_NAME, tableSchemaUrls.toString());
                ConvertContext conversionContext = new ConvertContext(in, strMetaXslFile, os, "xml");
                ConvertStrategy cs = new XMLConverter();
                cs.setXslParams(parameters);
                conversionContext.executeConversion(cs);

                // XSLTransformer transform = new XSLTransformer();
                // transform.transform(strMetaXslFile, new InputSource(in), os, parameters);
            }

        } catch (Exception ex) {
            LOGGER.error("Error converting meta.xml");
            throw ex;
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(in);
        }
    }
}
