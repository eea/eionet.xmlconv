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
 * The Original Code is XMLCONV - Converters and QA Services
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s): Enriko Käsper
 */

package eionet.gdem.dto;

/**
 * Object that stores conversion log messages
 *
 * @author Enriko Käsper
 */
public class ConversionLogDto {

    public enum ConversionLogType {
        CRITICAL, ERROR, WARNING, INFO
    };

    /**
     * Log category refers to the Excel file.
     */
    public static final String CATEGORY_WORKBOOK = "Workbook";
    /**
     * Log category refers to the Excel sheet.
     */
    public static final String CATEGORY_SHEET = "Sheet";

    /**
     * Conversion log message.
     */
    private String message;

    /**
     * Conversion log type - error, warning, info.
     */
    private ConversionLogType type;

    /**
     * Conversion log message category.
     */
    private String category;

    /**
     * Constructor creating conversion log object with properties.
     * @param type
     * @param message
     * @param category
     */
    public ConversionLogDto(ConversionLogType type, String message, String category) {
        this.type = type;
        this.message = message;
        this.category = category;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the type
     */
    public ConversionLogType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(ConversionLogType type) {
        this.type = type;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConversionLogDto [message=" + message + ", type=" + type + ", category=" + category + "]";
    }

}
