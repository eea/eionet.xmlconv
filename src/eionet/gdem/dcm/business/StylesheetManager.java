/*
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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
 */

package eionet.gdem.dcm.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.struts.upload.FormFile;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConvType;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.stylesheet.ConvTypeHolder;


public class StylesheetManager {
    private static LoggerIF _logger = GDEMServices.getLogger();
      private  IStyleSheetDao styleSheetDao = GDEMServices.getDaoService().getStyleSheetDao();;
      private  ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
      private  IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    public void delete(String user, String stylesheetId) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "d")) {
                _logger.debug("You don't have permissions to delete stylesheet!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error deleting stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        if(stylesheetId!=null && stylesheetId.startsWith("DD_")){
            throw new DCMException(BusinessConstants.EXCEPTION_DELETE_DD_XSL);
        }


        try {
            HashMap hash = styleSheetDao.getStylesheetInfo(stylesheetId);
            String fileName = (String) hash.get("xsl");
            String xslFolder = Properties.xslFolder;
            if (!xslFolder.endsWith(File.separator)) xslFolder = xslFolder + File.separator;
            Utils.deleteFile(xslFolder + fileName);
            styleSheetDao.removeStylesheet(stylesheetId);


            /*
             * //removing schema if it doesnt have stylesheets String schema =
             * (String)hash.get("xml_schema"); String schemaId =
             * (String)hash.get("schema_id");
             *
             * Vector vDb = dbM.listConversions(schema);
             *
             * if(vDb.size()==0) { dbM.removeSchema( schemaId, true, true, true); }
             */
        } catch (Exception e) {
            _logger.error("Error deleting stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }


    public ConvTypeHolder getConvTypes() throws DCMException {
        ConvTypeHolder ctHolder = new ConvTypeHolder();
        ArrayList convs;
        try {
            convs = new ArrayList();

            Vector convTypes = convTypeDao.getConvTypes();


            for (int i = 0; i < convTypes.size(); i++) {
                Hashtable hash = (Hashtable) convTypes.get(i);
                String conv_type = (String) hash.get("conv_type");

                ConvType conv = new ConvType();
                conv.setConvType(conv_type);
                convs.add(conv);
            }
            ctHolder.setConvTypes(convs);
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error getting conv types", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return ctHolder;

    }


    public void add(String user, String schema, FormFile file, String type, String descr, String dependsOn) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "i")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            String fileName = file.getFileName();


            if (styleSheetDao.checkStylesheetFile(fileName)) {
                throw new DCMException(BusinessConstants.EXCEPTION_STYLEHEET_FILE_EXISTS);
            }

            InputStream in = file.getInputStream();
            String filepath = new String(Properties.xslFolder + "/" + file.getFileName());
            OutputStream w = new FileOutputStream(filepath);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                w.write(buffer, 0, bytesRead);
            }
            w.close();
            in.close();
            file.destroy();

            String schemaID = schemaDao.getSchemaID(schema);
            if (schemaID == null) schemaID = schemaDao.addSchema(schema, null);

            styleSheetDao.addStylesheet(schemaID, type, fileName, descr, dependsOn);
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }


    public Stylesheet getStylesheet(String stylesheetId) throws DCMException {
        Stylesheet st = new Stylesheet();

        try {
            if (!stylesheetId.equals("")) {
                HashMap xsl = styleSheetDao.getStylesheetInfo(stylesheetId);

                if (xsl == null) xsl = new HashMap();

                st.setSchema((String) xsl.get("xml_schema"));
                st.setXsl_descr((String) xsl.get("description"));
                st.setType((String) xsl.get("content_type_out"));
                String xslFolder = Properties.xslFolder;
                st.setConvId(stylesheetId);
                st.setDependsOn((String) xsl.get("depends_on"));

                if(!Utils.isNullStr((String)xsl.get("xsl"))){
                    st.setXsl(Names.XSL_FOLDER + xsl.get("xsl"));
                    st.setXslFileName((String)xsl.get("xsl"));
                    if (!xslFolder.endsWith(File.separator)) xslFolder = xslFolder + File.separator;
                    String xslText = null;
                    try {
                        xslText = Utils.readStrFromFile(xslFolder + xsl.get("xsl"));
                    } catch (IOException e) {
                        xslText = Constants.FILEREAD_EXCEPTION + xslFolder + xsl.get("xsl") + "\n " + e.toString();
                    }
                    st.setXslContent(xslText);
                    String checksum = null;
                    try {
                        checksum = Utils.getChecksumFromFile(xslFolder + xsl.get("xsl"));
                    } catch (IOException e) {
                        checksum="";
                    }
                    st.setChecksum(checksum);
                    try{
                        File f=new File(xslFolder + xsl.get("xsl"));
                        if (f!=null && f.exists())
                            st.setModified(Utils.getDateTime(new Date(f.lastModified())));
                    }
                    catch(Exception e){
                    }

                }
            }

        } catch (Exception e) {
            _logger.error("Error getting stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return st;

    }

    /**
     * Finds stylesheets that belong to specified schema.
     * <p/>
     * Following fields in {@link Stylesheet} are populated: convId, xslFileName, dependsOn.
     *
     * @param schemaId
     *            schema id.
     * @param excludeStylesheetId
     *            stylesheet id to be excluded in returning result. If the value
     *            is null then no exclusion is done.
     * @return list of {@link Stylesheet} -s.
     */
    @SuppressWarnings("unchecked")
    public List<Stylesheet> getSchemaStylesheets(String schemaId, String excludeStylesheetId) throws DCMException  {
        List<Stylesheet> result = new ArrayList<Stylesheet>();
        try {
            Vector<Object> stylesheets = schemaDao.getSchemaStylesheets(schemaId);
            Map<Object, Object> stylesheet;
            Stylesheet st;
            for (Object o : stylesheets) {
                stylesheet = (Map<Object, Object>) o;
                st = new Stylesheet();
                st.setConvId((String)stylesheet.get("convert_id"));
                st.setXslFileName((String)stylesheet.get("xsl"));
                st.setDependsOn((String)stylesheet.get("depends_on"));

                if (excludeStylesheetId == null || !excludeStylesheetId.equals(st.getConvId())) {
                    result.add(st);
                }
            }
        } catch (Exception e) {
            _logger.error("Error getting stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return result;
    }


    public void update(String user, String xsl_id, String schema, FormFile file, String type, String descr, String dependsOn) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "u")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error updating stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            String fileName = file.getFileName().trim();


            if (fileName != null && !fileName.equals("")) {
                if(!styleSheetDao.checkStylesheetFile(xsl_id, fileName)) {
                    if (styleSheetDao.checkStylesheetFile(fileName)) {
                        throw new DCMException(BusinessConstants.EXCEPTION_STYLEHEET_FILE_EXISTS);
                    }
                }

                InputStream in = file.getInputStream();
                String filepath = new String(Properties.xslFolder + "/" + fileName);
                OutputStream w = new FileOutputStream(filepath);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                    w.write(buffer, 0, bytesRead);
                }
                w.close();
                in.close();
                file.destroy();

                // delete Old xsl
                HashMap hash = styleSheetDao.getStylesheetInfo(xsl_id);


                String fileNameOld = (String) hash.get("xsl");
                String xslFolder = Properties.xslFolder;
                // if (!xslFolder.endsWith(File.separator))
                // xslFolder = xslFolder + File.separator;

                Utils.deleteFile(xslFolder + fileNameOld);

            }
            String schemaID = schemaDao.getSchemaID(schema);

            if (schemaID == null) schemaID = schemaDao.addSchema(schema, null);
            styleSheetDao.updateStylesheet(xsl_id, schemaID, descr, fileName, type, dependsOn);
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error updating stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    public void updateContent(String user, String xsl_id, String schema, String fileName, String type,
            String descr, String fileContent,boolean updateContent, String dependsOn) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "u")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error updating stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            if (!Utils.isNullStr(fileName) && !Utils.isNullStr(fileContent) &&
                    fileContent.indexOf(Constants.FILEREAD_EXCEPTION)==-1 && updateContent) {
                Utils.saveStrToFile(Properties.xslFolder + File.separator + fileName, fileContent,null);
            }
            String schemaID = schemaDao.getSchemaID(schema);

            if (schemaID == null) schemaID = schemaDao.addSchema(schema, null);
            styleSheetDao.updateStylesheet(xsl_id, schemaID, descr, fileName, type, dependsOn);
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error updating stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

}
