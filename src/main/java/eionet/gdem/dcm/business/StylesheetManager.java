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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConvType;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.stylesheet.ConvTypeHolder;

/**
 *
 * Business logic for managing Stylesheet objects in database and XSLT files in file system.
 *
 * @author Enriko Käsper
 */
public class StylesheetManager {
    /** */
    private static final Log LOGGER = LogFactory.getLog(StylesheetManager.class);

    private IStyleSheetDao styleSheetDao = GDEMServices.getDaoService().getStyleSheetDao();;
    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    /**
     * Deletes stylesheet data from db and XSLT file from file system if provided user has appropriate permissions.
     * @param user logged in user name.
     * @param stylesheetId stylesheet unique ID.
     * @throws DCMException in case of database or file system Exception.
     */
    public void delete(String user, String stylesheetId) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "d")) {
                LOGGER.debug("You don't have permissions to delete stylesheet!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        if (stylesheetId != null && stylesheetId.startsWith("DD_")) {
            throw new DCMException(BusinessConstants.EXCEPTION_DELETE_DD_XSL);
        }

        try {
            Stylesheet stylesheet = styleSheetDao.getStylesheet(stylesheetId);
            Utils.deleteFile(stylesheet.getXslFileFullPath());
            styleSheetDao.deleteStylesheet(stylesheetId);

        } catch (Exception e) {
            LOGGER.error("Error deleting stylesheet. stylesheetId=" + stylesheetId, e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL, "Error deleting stylesheet. stylesheetId=" + stylesheetId);
        }

    }

    /**
     * Gets conversion types
     * @return Conversion types
     * @throws DCMException If an error occurs.
     */
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
            LOGGER.error("Error getting conv types", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return ctHolder;

    }

    /**
     * Add new stylesheet file into repository.
     * @param stylesheet Stylesheet DTO.
     * @param user logged in user name.
     * @throws DCMException if saving of file or database update failed.
     */
    public void add(Stylesheet stylesheet, String user) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "i")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            String fileName = stylesheet.getXslFileName();

            if (styleSheetDao.checkStylesheetFile(fileName)) {
                throw new DCMException(BusinessConstants.EXCEPTION_STYLEHEET_FILE_EXISTS);
            }

            String filepath = new String(Properties.xslFolder + "/" + stylesheet.getXslFileName());

            Utils.saveStrToFile(filepath, stylesheet.getXslContent(), null);

            styleSheetDao.addStylesheet(stylesheet);
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Read stylesheet data from database and XSLT file contents from file system.
     * @param stylesheetId Numeric primary key or xsl file name.
     * @return Stylesheet object
     * @throws DCMException in case of database or file system Exception.
     */
    public Stylesheet getStylesheet(String stylesheetId) throws DCMException {
        Stylesheet stylesheet = null;

        try {
            if (!stylesheetId.equals("")) {
                stylesheet = styleSheetDao.getStylesheet(stylesheetId);

                if (stylesheet != null && !Utils.isNullStr(stylesheet.getXslFileName())) {
                    stylesheet.setXsl(Names.XSL_FOLDER + stylesheet.getXslFileName());

                    String xslText = null;
                    try {
                        // xslText = Utils.readStrFromFile(stylesheet.getXslFileFullPath());
                        xslText = FileUtils.readFileToString(new File(stylesheet.getXslFileFullPath()), "utf-8");
                    } catch (IOException e) {
                        xslText = Constants.FILEREAD_EXCEPTION + stylesheet.getXslFileFullPath() + "\n " + e.toString();
                    }
                    stylesheet.setXslContent(xslText);
                    String checksum = null;
                    try {
                        checksum = Utils.getChecksumFromFile(stylesheet.getXslFileFullPath());
                    } catch (IOException e) {
                        checksum = "";
                    }
                    stylesheet.setChecksum(checksum);
                    try {
                        File f = new File(stylesheet.getXslFileFullPath());
                        if (f != null && f.exists()) {
                            stylesheet.setModified(Utils.getDateTime(new Date(f.lastModified())));
                        }
                    } catch (Exception e) {
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error getting stylesheet. stylesheetId=" + stylesheetId, e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL, "The requested stylesheet does not exist in the system. stylesheetId="
                    + stylesheetId);
        }
        return stylesheet;

    }

    /**
     * Finds stylesheets that belong to specified schema.
     * <p/>
     * Following fields in {@link Stylesheet} are populated: convId, xslFileName, dependsOn.
     *
     * @param schemaId
     *            schema id.
     * @param excludeStylesheetId
     *            stylesheet id to be excluded in returning result. If the value is null then no exclusion is done.
     * @return list of {@link Stylesheet} -s.
     * @throws DCMException If an error occurs.
     */
    @SuppressWarnings("unchecked")
    public List<Stylesheet> getSchemaStylesheets(String schemaId, String excludeStylesheetId) throws DCMException {
        List<Stylesheet> result = new ArrayList<Stylesheet>();
        try {
            Vector<Object> stylesheets = schemaDao.getSchemaStylesheets(schemaId);
            Map<Object, Object> stylesheet;
            Stylesheet st;
            for (Object o : stylesheets) {
                stylesheet = (Map<Object, Object>) o;
                st = new Stylesheet();
                st.setConvId((String) stylesheet.get("convert_id"));
                st.setXslFileName((String) stylesheet.get("xsl"));
                st.setDependsOn((String) stylesheet.get("depends_on"));

                if (excludeStylesheetId == null || !excludeStylesheetId.equals(st.getConvId())) {
                    result.add(st);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error getting stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return result;
    }

    /**
     * Get list of all stylesheets.
     * @return List of Stylesheet dto objects.
     * @throws DCMException in case of database exception
     */
    public List<Stylesheet> getStylesheets() throws DCMException {
        List<Stylesheet> stylesheets;
        try {
            stylesheets = styleSheetDao.getStylesheets();
            if (stylesheets != null) {
                for (Stylesheet stylesheet : stylesheets) {
                    if (!Utils.isNullStr(stylesheet.getXslFileName())) {
                        File xslFile = new File(Properties.xslFolder + File.separatorChar + stylesheet.getXslFileName());
                        if (xslFile != null && xslFile.exists()) {
                            stylesheet.setLastModifiedTime(new Date(xslFile.lastModified()));
                            stylesheet.setModified(Utils.getDateTime(stylesheet.getLastModifiedTime()));
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error getting stylesheets", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL, "Error getting stylesheets from database!");
        }
        return stylesheets;
    }

    /**
     * Update stylesheet file content and properties.
     * @param styleseet Stylesheet DTO.
     * @param user logged in user name.
     * @param updateContent update XML file content.
     * @throws DCMException if saving of file or database update failed.
     */
    public void update(Stylesheet styleseet, String user, boolean updateContent) throws DCMException {
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "u")) {
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_UPDATE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            if (!Utils.isNullStr(styleseet.getXslFileName()) && !Utils.isNullStr(styleseet.getXslContent())
                    && styleseet.getXslContent().indexOf(Constants.FILEREAD_EXCEPTION) == -1 && updateContent) {
                Utils.saveStrToFile(Properties.xslFolder + File.separator + styleseet.getXslFileName(), styleseet.getXslContent(), null);
            }
            styleSheetDao.updateStylesheet(styleseet);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error updating stylesheet", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }
}
