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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper (TripleDev)
 */

package eionet.gdem.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import eionet.gdem.Properties;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.qa.QAFeedbackType;
import eionet.gdem.utils.Utils;

/**
 * Builds ValidationService result object. Formats validation errors as HTML.
 * TODO: This class looks inefficient, check for replacing methods
 * @author Enriko Käsper
 * @author George Sofianos
 */
public class ValidationServiceFeedback {

    /** XML Schema URL. */
    private String schema;
    /** Feedback text. */
    StringBuilder feedbackText = new StringBuilder();
    /** List of validation errors and warnings. */
    private List<ValidateDto> validationErrors = new ArrayList<ValidateDto>();

    /**
     * Append text to feedback object.
     * @param text to append.
     */
    private void appendFeedback(String text) {
        feedbackText.append(text);
    }

    /**
     * Append escaped text to feedback object.
     * @param text to escape and append.
     */
    private void appendFeedbackText(String text) {
        appendFeedback(StringEscapeUtils.escapeXml(text));
    }

    /**
     * Append property value to feedback text.
     * @param property name.
     */
    private void appendFeedbackTextProperty(String property) {
        appendFeedbackText(Properties.getMessage(property));
    }

    /**
     * Add css according to feedback type.
     * @param type feedback type (INFO, ERROR, BLOCKER, ...)
     */
    private void appendColouredToken(QAFeedbackType type) {

        String sizeCss = " font-size: 0.8em; color: white; padding-left:5px;padding-right:5px;margin-right:5px;text-align:center";
        switch (type) {
            case BLOCKER:
                appendFeedback("<span style=\"background-color: red;" + sizeCss + "\">BLOCKER</span>");
                break;
            case ERROR:
                appendFeedback("<span style=\"background-color: red;" + sizeCss + "\">ERROR</span>");
                break;
            case WARNING:
                appendFeedback("<span style=\"background-color: orange;" + sizeCss + "\">WARNING</span>");
                break;
            case INFO:
                appendFeedback("<span style=\"background-color: green;" + sizeCss + "\">OK</span>");
                break;
            default:
                break;
        }
    }

    /**
     * Format feedback object as HTML text. Used only when validation completed successfully.
     * @param isBlocker true if errors in XML Validation should return blocker flag.
     * @return HTML snippet with validation result.
     */
    public String formatFeedbackText(boolean isBlocker) {

        String result = null;
        String errMsgKey = "label.validation.result.ok";
        QAFeedbackType qaFeedbackType = QAFeedbackType.INFO;

        if (getValidationErrors().size() > 0) {
            if (validationContainsOnlyWarnings()) {
                errMsgKey = "label.validation.result.warning";
                qaFeedbackType = QAFeedbackType.WARNING;
            } else {
                errMsgKey = isBlocker ? "label.validation.result.failed.blocker" : "label.validation.result.failed";
                qaFeedbackType = QAFeedbackType.ERROR;
            }
        }
        result = formatFeedbackText(Properties.getMessage(errMsgKey), qaFeedbackType, isBlocker);
        return result;

    }

    /**
     * Format feedback object as HTML text. The method allows to configure feedback type and summary message.
     * @param text Text to display as a summary of feedback result.
     * @param feedbackType INFO, ERROR, WARNING.
     * @param isBlocker true if errors in XML Validation should return blocker flag.
     * @return HTML snippet with validation result.
     */
    public String formatFeedbackText(String text, QAFeedbackType feedbackType, boolean isBlocker) {

        if (isBlocker && feedbackType == QAFeedbackType.ERROR) {
            feedbackType = QAFeedbackType.BLOCKER;
        }
        appendFeedback("<div class=\"feedbacktext\">");

        // feedback type info.
        appendFeedback("<span id=\"feedbackStatus\" class=\"");
        appendFeedback(feedbackType.name());
        appendFeedback("\" style=\"display:none\">");
        appendFeedbackText(text);
        appendFeedback("</span>");

        // backward compatibility
        if (feedbackType == QAFeedbackType.INFO) {
            appendFeedback("<span style=\"display:none\"><p>OK</p></span>");
        }

        // feedback title.
        appendFeedback("<h2>");
        appendFeedbackTextProperty("label.validation.result.title");
        appendFeedback("</h2>");

        // Colored result text
        appendFeedback("<p>");
        appendColouredToken(feedbackType);
        appendFeedbackText(text);
        appendFeedback("</p>");

        if (!Utils.isNullStr(getSchema())) {
            appendFeedback("<p>");
            appendFeedbackTextProperty("label.validation.result.schema");
            appendFeedback(" <a href=\"");
            appendFeedbackText(getSchema());
            appendFeedback("\">");
            appendFeedbackText(getSchema());
            appendFeedback("</a></p>");
        }
        if (getValidationErrors().size() > 0) {
            // description
            appendFeedback("<p>");
            appendFeedbackTextProperty("label.validation.result.tbl.description");
            appendFeedback("</p>");

            writeErrorsTable();
        }

        appendFeedback("</div>");

        return feedbackText.toString();
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @return the validationErrors
     */
    public List<ValidateDto> getValidationErrors() {
        return validationErrors;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @param validationErrors the validationErrors to set
     */
    public void setValidationErrors(List<ValidateDto> validationErrors) {
        this.validationErrors = validationErrors;
    }

    /**
     * Tests if the list of errors contain only warnings.
     * @return true, if no errors or fatal errors.
     */
    protected boolean validationContainsOnlyWarnings() {

        for (ValidateDto vErrors : getValidationErrors()) {
            if (!vErrors.getType().equals(ValidatorErrorType.WARNING)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Append text in HTML cell to feedback.
     * @param text text to append in table cell.
     */
    private void writeCell(String text) {
        appendFeedback("<td>");
        appendFeedbackText(text);
        appendFeedback("</td>");
    }

    /**
     * Append errors as HTML table to feeback text.
     */
    private void writeErrorsTable() {
        appendFeedback("<table class=\"datatable\" border='1'><tr>");
        appendFeedback("<th>Type</th>");
        appendFeedback("<th>Position</th>");
        appendFeedback("<th>Error message</th>");
        appendFeedback("</tr>");

        for (ValidateDto vError : getValidationErrors()) {
            appendFeedback("<tr>");
            writeCell(vError.getType().name());
            writeCell("Line: " + vError.getLine() + ", Col: " + vError.getColumn());
            writeCell(vError.getDescription());
            appendFeedback("</tr>");

        }
        appendFeedback("</table>");
    }
}
