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
 * Agency. Copyright (C) European Environment Agency. All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper
 */
package eionet.gdem.qa;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import eionet.gdem.XMLConvException;
import eionet.gdem.xml.VtdHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eionet.gdem.Properties;
import eionet.gdem.conversion.datadict.DataDictUtil;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper
 * @author George Sofianos
 *
 * The class processes QA results and add warnings/errors if required.
 */
public class QAResultPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAResultPostProcessor.class);
    private SchemaManager schemaManager = new SchemaManager();
    private String warnMessage;

    /**
     * Checks if the QA was made against expired schema. Adds a warning on top of the QA result if the result is HTML format.
     * @param result QA result
     * @param xmlSchema XML Schema
     * @return Processed result
     * @throws XMLConvException If an error occurs.
     */
    public String processQAResult(String result, Schema xmlSchema) throws XMLConvException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.warnMessage = getWarningMessage(xmlSchema);
        if (warnMessage != null) {
            VtdHandler vdt = new VtdHandler();
            vdt.addWarningMessage(result, warnMessage, out);
        } else {
            try {
                out.write(result.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new XMLConvException("Couldn't write to OutputStream: " + e.getMessage());
            }
        }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Process QA result
     * @param result Result
     * @param xmlSchemaUrl Schema URL
     * @return Processed result
     * @throws XMLConvException If an error occurs.
     */
    public String processQAResult(String result, String xmlSchemaUrl) throws XMLConvException {

        Schema schema = getSchemaObject(xmlSchemaUrl);
        return processQAResult(result, schema);
    }

    /**
     * Returns warning message for given schema URL.
     *
     * @param xmlSchemaUrl XML Schema URL
     * @return Warning message
     */
    public String getWarningMessage(String xmlSchemaUrl) {

        if (warnMessage != null) {
            return warnMessage;
        }
        Schema schema = getSchemaObject(xmlSchemaUrl);
        return getWarningMessage(schema);
    }

    /**
     * Returns warning message if schema is expired.
     *
     * @param xmlSchema XML Schema
     * @return Warning message
     */
    private String getWarningMessage(Schema xmlSchema) {

        String localSchemaExpiredMessage = getLocalSchemaExpiredMessage(xmlSchema);
        if (localSchemaExpiredMessage != null) {
            return localSchemaExpiredMessage;
        }

        String ddSchemaExpiredMessage = getDDSchemaExpiredMessage(xmlSchema);
        if (ddSchemaExpiredMessage != null) {
            return ddSchemaExpiredMessage;
        }

        return null;
    }

    /**
     * Get Schema object from database
     *
     * @param xmlSchemaUrl XML Schema URL
     * @return Schema object
     */
    private Schema getSchemaObject(String xmlSchemaUrl) {

        Schema schema = null;
        String schemaId;
        try {
            schemaId = schemaManager.getSchemaId(xmlSchemaUrl);
            if (schemaId != null) {
                schema = schemaManager.getSchema(schemaId);
            }
        } catch (DCMException e) {
            LOGGER.error("Unable to find Schema information from database" + e.toString());
            e.printStackTrace();
        }

        if (schema == null && xmlSchemaUrl != null) {
            schema = new Schema();
            schema.setSchema(xmlSchemaUrl);
        }
        return schema;
    }

    /**
     * Check if given XML Schema is marked as expired in XMLCONV repository. Returns error message, otherwise null.
     *
     * @param xmlSchema XML Schema
     * @return Schema Expired message
     */
    private String getLocalSchemaExpiredMessage(Schema xmlSchema) {

        if (xmlSchema != null && xmlSchema.isExpired()) {

            // schema is expired add message in top of the QA result
            String expDate = Utils.getFormat(xmlSchema.getExpireDate(), "dd.MM.yyyy");
            String message = Properties.getMessage(BusinessConstants.WARNING_QA_EXPIRED_SCHEMA, new String[] {expDate});
            return message;
        }
        return null;
    }

    /**
     * Check if schema is the latest released version in DD (in case of DD schema). If it is not latest released then return warning
     * message.
     *
     * @param xmlSchema XML Schema
     * @return Schema expired message.
     */
    private String getDDSchemaExpiredMessage(Schema xmlSchema) {

        Map<String, String> dataset = getDataset(xmlSchema.getSchema());
        if (dataset != null) {
            String status = dataset.get("status");
            boolean isLatestReleased =
                (dataset.get("isLatestReleased") == null || "true".equals(dataset.get("isLatestReleased"))) ? true
                        : false;
            String dateOfLatestReleased = dataset.get("dateOfLatestReleased");
            String idOfLatestReleased = dataset.get("idOfLatestReleased");

            if (!isLatestReleased && "Released".equalsIgnoreCase(status)) {
                String formattedReleasedDate = Utils.formatTimestampDate(dateOfLatestReleased);
                String message =
                    Properties.getMessage(BusinessConstants.WARNING_QA_EXPIRED_DD_SCHEMA, new String[] {
                            formattedReleasedDate == null ? "" : formattedReleasedDate, idOfLatestReleased});
                return message;
            }
        }
        return null;
    }

    /**
     * Get DD XML Schema released info
     *
     * @param xmlSchema XML Schema
     * @return Dataset Map
     */
    protected Map<String, String> getDataset(String xmlSchema) {
        return DataDictUtil.getDatasetReleaseInfoForSchema(xmlSchema);
    }
}
