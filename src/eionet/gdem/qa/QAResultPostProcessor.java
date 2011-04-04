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

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eionet.gdem.Properties;
import eionet.gdem.conversion.datadict.DataDictUtil;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.IXmlSerializer;
import eionet.gdem.utils.xml.XmlContext;
import eionet.gdem.utils.xml.XmlException;
import eionet.gdem.utils.xml.XmlSerialization;

/**
 * @author Enriko Käsper
 *
 *         The class processes QA results and add warnings/errors if required.
 */
public class QAResultPostProcessor {

    /** */
    protected Logger logger = Logger.getLogger(QAResultPostProcessor.class);

    private SchemaManager schemaManager = new SchemaManager();

    private String warnMessage = null;
    /**
     * Checks if the QA was made against expired schema. Adds a warning on top
     * of the QA result if the result is HTML format.
     *
     * @return
     */
    public String processQAResult(String result, Schema xmlSchema) {

        this.warnMessage = getWarningMessage(xmlSchema);

        if (warnMessage != null) {
            result = addExpWarning(result, warnMessage);
        }
        return result;
    }

    public String processQAResult(String result, String xmlSchemaUrl) {

        Schema schema = getSchemaObject(xmlSchemaUrl);
        return processQAResult(result, schema);
    }

    /**
     * Returns warning message for given schema URL.
     *
     * @param xmlSchemaUrl
     * @return
     * @throws DCMException
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
     * @param xmlSchema
     * @return
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
     * @param xmlSchemaUrl
     * @return
     * @throws DCMException
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
            logger.error("Unable to find Schema information from database"
                    + e.toString());
            e.printStackTrace();
        }

        if (schema == null && xmlSchemaUrl != null) {
            schema = new Schema();
            schema.setSchema(xmlSchemaUrl);
        }
        return schema;
    }

    /**
     * Check if given XML Schema is marked as expired in XMLCONV repository.
     * Returns error message, otherwise null.
     *
     * @param xmlSchema
     * @return
     */
    private String getLocalSchemaExpiredMessage(Schema xmlSchema) {

        if (xmlSchema != null && xmlSchema.isExpired()) {

            // schema is expired add message in top of the QA result
            String expDate = Utils.getFormat(xmlSchema.getExpireDate(),
                    "dd.MM.yyyy");
            String message = Properties.getMessage(
                    BusinessConstants.WARNING_QA_EXPIRED_SCHEMA,
                    new String[] { expDate });
            return message;
        }
        return null;
    }

    /**
     * Check if schema is the latest released version in DD (in case of DD
     * schema). If it is not latest released then return warning message.
     *
     * @param xmlSchema
     * @return
     */
    private String getDDSchemaExpiredMessage(Schema xmlSchema) {

        Map<String, String> dataset = getDataset(xmlSchema.getSchema());
        if (dataset != null) {
            String status = (String) dataset.get("status");
            boolean isLatestReleased = (dataset.get("isLatestReleased") == null ||
                        "true".equals((String) dataset.get("isLatestReleased"))) ?
                                true
                                : false;
            String dateOfLatestReleased = (String) dataset
                    .get("dateOfLatestReleased");
            String idOfLatestReleased = (String) dataset
                    .get("idOfLatestReleased");

            if (!isLatestReleased && "Released".equalsIgnoreCase(status)) {
                String formattedReleasedDate = Utils
                        .formatTimestampDate(dateOfLatestReleased);
                String message = Properties.getMessage(
                        BusinessConstants.WARNING_QA_EXPIRED_DD_SCHEMA,
                        new String[] {
                                formattedReleasedDate == null ? ""
                                        : formattedReleasedDate
                                , idOfLatestReleased });
                return message;
            }
        }
        return null;
    }

    private String addExpWarning(String htmlResult, String warnMessage) {

        try {
            IXmlCtx ctx = new XmlContext();
            ctx.checkFromString(htmlResult);

            NodeList divElements = ctx.getDocument()
                    .getElementsByTagName("div");
            boolean foundFeedbackDiv = parseDivNodes(divElements, warnMessage);

            // searching node is case insensitive in XPath - do it twice:
            if (!foundFeedbackDiv) {
                divElements = ctx.getDocument().getElementsByTagName("DIV");
                foundFeedbackDiv = parseDivNodes(divElements, warnMessage);
            }

            if (!foundFeedbackDiv) {
                return htmlResult;
            } else {
                IXmlSerializer serializer = new XmlSerialization(ctx);
                return serializer.serializeToString();
            }
        } catch (Exception e) {
            logger.error("addExpWarning() Error parsing HTML, returning original HTML: "
                    + e.toString());
        }

        return htmlResult;
    }

    private boolean parseDivNodes(NodeList divElements, String warnMessage)
            throws XmlException {
        boolean feedBackDivFound = false;
        try {
            for (int i = 0; divElements != null && i < divElements.getLength(); i++) {
                Node divNode = divElements.item(i);
                Node classNode = divNode.getAttributes().getNamedItem("class");

                if (classNode != null
                        && classNode.getNodeValue().equalsIgnoreCase(
                                "feedbacktext")) {
                    // found feedback div
                    feedBackDivFound = true;

                    Node firstChild = divNode.getFirstChild();
                    Document doc = divNode.getOwnerDocument();

                    Node warningNode =
                            DocumentBuilderFactory
                                    .newInstance()
                                    .newDocumentBuilder()
                                    .parse(new InputSource(
                                            new StringReader(
                                                    "<div class=\"error-msg\">"
                                                            +
                                                            warnMessage
                                                            + "</div>")))
                                    .getFirstChild();
                    warningNode = doc.importNode(warningNode, true);
                    if (firstChild == null) {
                        divNode.appendChild(warningNode);
                    } else {
                        warningNode = divNode.insertBefore(warningNode,
                                firstChild);
                    }
                    //
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error processing divNodes " + e);
        }
        return feedBackDivFound;
    }

    /**
     * Get DD XML Schema released info
     *
     * @param xmlSchema
     * @return
     */
    protected Map<String, String> getDataset(String xmlSchema) {
        return DataDictUtil.getDatasetReleaseInfoForSchema(xmlSchema);
    }
}
