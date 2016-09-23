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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.datadict;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.dom.DomContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, Tieto Estonia DataDictUtil
 */

public class DataDictUtil {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDictUtil.class);

    public static final String INSTANCE_SERVLET = "GetXmlInstance";
    public static final String SCHEMA_SERVLET = "GetSchema";
    public static final String CONTAINER_SCHEMA_SERVLET = "GetContainerSchema";

    /**
     * Constructs instance file URL using schema ID.
     * @param schema_url Schema URL
     * @return Instance URL
     * @throws XMLConvException In case of unexpected error.
     */
    public static String getInstanceUrl(String schema_url) throws XMLConvException {

        try {

            // throws Exception, if not correct URL
            URL schemaURL = new URL(schema_url);

            String id = getSchemaIdParamFromUrl(schema_url);

            String type = id.substring(0, 3);
            id = id.substring(3);

            int path_idx = schema_url.toLowerCase().indexOf(SCHEMA_SERVLET.toLowerCase());
            String path = schema_url.substring(0, path_idx);

            String instance_url = path + INSTANCE_SERVLET + "?id=" + id + "&type=" + type.toLowerCase();

            // throws Exception, if not correct URL
            URL instanceURL = new URL(instance_url);
            return instance_url;
        } catch (MalformedURLException e) {
            throw new XMLConvException("Error getting Instance file URL: " + e.toString() + " - " + schema_url);
        } catch (Exception e) {
            throw new XMLConvException("Error getting Instance file URL: " + e.toString() + " - " + schema_url);
        }
    }

    /**
     * Extract id parameter value from URL if available, otherwise return empty String.
     *
     * @param schemaUrl Schema URL
     * @return ID
     */
    public static String getSchemaIdParamFromUrl(String schemaUrl) {

        String id = "";
        int id_idx = schemaUrl.indexOf("id=");

        if (id_idx > -1) {
            id = schemaUrl.substring(id_idx + 3);
        }
        if (id.indexOf("&") > -1) {
            id = id.substring(0, id.indexOf("&"));
        }

        return id;
    }

    /**
     * gather all element definitions
     *
     * @param schemaUrl Schema URL
     */
    public static Map<String, DDElement> importDDTableSchemaElemDefs(String schemaUrl) {
        InputStream inputStream = null;
        Map<String, DDElement> elemDefs = new HashMap<String, DDElement>();
        try {
            // get element definitions for given schema
            // DataDictUtil.getSchemaElemDefs(elemDefs, schemaUrl);

            // load imported schema URLs
            // TODO: Replace this with VTD or Saxon for faster XPATH.
            IXmlCtx ctx = new DomContext();
            URL url = new URL(schemaUrl);
            inputStream = url.openStream();
            ctx.checkFromInputStream(inputStream);
            XPathQuery xQuery = ctx.getQueryManager();

            // run recursively the same function for importing elem defs for imported schemas
            List<String> schemas = xQuery.getSchemaImports();
            Map<String, String> multiValueElements = xQuery.getSchemaElementWithMultipleValues();

            for (int i = 0; i < schemas.size(); i++) {
                String schema = schemas.get(i);
                DataDictUtil.importDDElementSchemaDefs(elemDefs, schema);
            }

            for (Map.Entry<String, String> entry : multiValueElements.entrySet()) {
                DDElement multiValueElement = null;
                if (elemDefs.containsKey(entry.getKey())) {
                    multiValueElement = elemDefs.get(entry.getKey());
                } else {
                    multiValueElement = new DDElement(entry.getKey());
                }
                multiValueElement.setHasMultipleValues(true);
                multiValueElement.setDelimiter(entry.getValue());
                elemDefs.put(entry.getKey(), multiValueElement);
            }
        } catch (Exception ex) {
            LOGGER.error("Error reading schema file ", ex);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
        return elemDefs;
    }

    /**
     * Import element definitions
     * @param elemDefs Element definitions
     * @param schemaUrl Schema URL
     * @return
     */
    public static Map<String, DDElement> importDDElementSchemaDefs(Map<String, DDElement> elemDefs, String schemaUrl) {
        InputStream inputStream = null;
        if (elemDefs == null) {
            elemDefs = new HashMap<String, DDElement>();
        }

        try {
            // TODO: Replace this with VTD or Saxon for faster XPATH.
            IXmlCtx ctx = new DomContext();
            URL url = new URL(schemaUrl);
            inputStream = url.openStream();
            ctx.checkFromInputStream(inputStream);

            XPathQuery xQuery = ctx.getQueryManager();
            List<String> elemNames = xQuery.getSchemaElements();
            for (int i = 0; i < elemNames.size(); i++) {
                String elemName = elemNames.get(i);
                DDElement element = elemDefs.containsKey(elemName) ? elemDefs.get(elemName) : new DDElement(elemName);
                element.setSchemaDataType(xQuery.getSchemaElementType(elemName));
                elemDefs.put(elemName, element);
            }
        } catch (Exception ex) {
            LOGGER.error("Error reading schema file ", ex);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
        return elemDefs;

    }

    /**
     * Returns the DD container schema URL. It holds the elements definitions
     *
     * @param schema_url Schema URL
     * @return Container schema url
     * @throws XMLConvException In case of unexpected error
     */
    public static String getContainerSchemaUrl(String schema_url) throws XMLConvException {

        try {
            URL SchemaURL = new URL(schema_url);

            String containerSchemaUrl = schema_url.replace(DataDictUtil.SCHEMA_SERVLET, CONTAINER_SCHEMA_SERVLET);

            URL InstanceURL = new URL(containerSchemaUrl);
            return containerSchemaUrl;
        } catch (MalformedURLException e) {
            throw new XMLConvException("Error getting Container Schema URL: " + e.toString() + " - " + schema_url);
        } catch (Exception e) {
            throw new XMLConvException("Error getting Container Schema URL: " + e.toString() + " - " + schema_url);
        }
    }

    /**
     * Check is schema is DD schema and if it does not belong to latest released version of dataset. In that case QA may want to
     * warn users about using obsolete schema.
     *
     * @param xmlSchema XML Schema
     * @return True if DD schema does not belong to latest released version of dataset.
     */
    public static boolean isDDSchemaAndNotLatestReleased(String xmlSchema) {
        if (xmlSchema == null) {
            return false;
        }

        String id = getSchemaIdParamFromUrl(xmlSchema);

        if (id.length() > 4 && (id.startsWith(DD_XMLInstance.DST_TYPE) || id.startsWith(DD_XMLInstance.TBL_TYPE))) {

            Map<String, String> dataset = null;

            String type = id.substring(0, 3);
            String dsId = id.substring(3);
            dataset = getDatasetReleaseInfo(type.toLowerCase(), dsId);

            if (dataset != null) {
                String status = dataset.get("status");
                boolean isLatestReleased =
                    (dataset.get("isLatestReleased") == null || "true".equals(dataset.get("isLatestReleased"))) ? true
                            : false;

                if (!isLatestReleased && "Released".equalsIgnoreCase(status)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retreive dataset released information from Data Dictionary for XML schema. If it is not DD schema, then return null
     *
     * @param xmlSchema XML Schema
     * @return Dataset released information for xml schema
     */
    public static Map<String, String> getDatasetReleaseInfoForSchema(String xmlSchema) {

        Map<String, String> dataset = null;

        if (xmlSchema == null || !xmlSchema.startsWith(Properties.ddURL)) {
            return dataset;
        }

        String id = getSchemaIdParamFromUrl(xmlSchema);

        if (id.length() > 4 && (id.startsWith(DD_XMLInstance.DST_TYPE) || id.startsWith(DD_XMLInstance.TBL_TYPE))) {

            String type = id.substring(0, 3);
            String dsId = id.substring(3);
            dataset = getDatasetReleaseInfo(type.toLowerCase(), dsId);
        }
        return dataset;
    }

    /**
     * Retreive dataset released information from Data Dictionary for given ID and type If it is not DD schema, then return null
     *
     * @param type Dataset type.
     * @param dsId Dataset ID.
     * @return Dataset release information.
     */
    public static Map<String, String> getDatasetReleaseInfo(String type, String dsId) {
        if (!GDEMServices.isTestConnection()) {
            return DDServiceClient.getDatasetWithReleaseInfo(type, dsId);
        } else {
            return DDServiceClient.getMockDataset(type, dsId);
        }

    }
}
