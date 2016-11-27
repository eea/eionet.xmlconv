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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import eionet.gdem.dto.ConversionLogDto.ConversionLogType;
import eionet.gdem.utils.Utils;

/**
 * The DTO structure that keeps conversion result from Excel to XML.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public class ConversionResultDto {

    /**
     * Value: 0 Indicates that conversion went OK.
     */
    public static final String STATUS_OK = "0";

    /**
     * Value: 1 Indicates validation errors (not enough some data)
     */
    public static final String STATUS_ERR_VALIDATION = "1";

    /**
     * Value: 2 Indicates that some unpredictable system error occurred.
     */
    public static final String STATUS_ERR_SYSTEM = "2";

    /**
     * Value 3: Indicates that the schema by URL and version was not found.
     */
    public static final String STATUS_ERR_SCHEMA_NOT_FOUND = "3";

    /**
     * Conversion status code. See Dto public constants.
     */
    private String statusCode;

    /**
     * Status description. In case of errors - explained error information
     */
    private String statusDescription;

    /**
     * List of conversion log messages.
     */
    private List<ConversionLogDto> conversionLogs;
    /**
     * Conversion result log contains warning messages.
     */
    private boolean containsWarnings;
    /**
     * Conversion result log contains error messages.
     */
    private boolean containsErrors;
    /**
     * Conversion result log contains info messages.
     */
    private boolean containsInfos;

    /**
     * URL of source file that was converted
     */
    private String sourceUrl;
    /**
     * Converted XML files according to style sheets. Map key is file name, map value is file content.
     */
    private Map<String, byte[]> convertedXmls;
    /**
     * References to converted files if they are stored temporarily in filesystem.
     */
    private List<ConvertedFileDto> convertedFiles;

    /**
     * Default constructor
     */
    public ConversionResultDto() {
        super();
    }

    /**
     * @return the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the statusDescription
     */
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * @param statusDescription
     *            the statusDescription to set
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * @return the convertedXmls
     */
    public Map<String, byte[]> getConvertedXmls() {
        return convertedXmls;
    }

    /**
     * @param convertedXmls
     *            the convertedXmls to set
     */
    public void setConvertedXmls(Map<String, byte[]> convertedXmls) {
        this.convertedXmls = convertedXmls;
    }

    /**
     * Add new XML file into the conversion result object.
     *
     * @param xmlName
     *            file name
     * @param convertedXml
     *            XML content
     */
    public void addConvertedXml(String xmlName, byte[] convertedXml) {
        if (this.convertedXmls == null) {
            this.convertedXmls = new LinkedHashMap<String, byte[]>();
        }
        this.convertedXmls.put(xmlName, convertedXml);
    }

    /**
     * Add new files into the conversion result object.
     *
     * @param fileName
     *            file name
     * @param filePath
     *            file path
     */
    public void addConvertedFile(String fileName, String filePath) {
        if (this.convertedFiles == null) {
            this.convertedFiles = new ArrayList<ConvertedFileDto>();
        }
        this.convertedFiles.add(new ConvertedFileDto(fileName, filePath));
    }

    /**
     * @return the conversionLogs
     */
    public List<ConversionLogDto> getConversionLogs() {
        return conversionLogs;
    }

    /**
     * @param conversionLogs
     *            the conversionLogs to set
     */
    public void setConversionLogs(List<ConversionLogDto> conversionLogs) {
        this.conversionLogs = conversionLogs;
    }

    /**
     * Add new conversion.
     *
     * @param conversionLog Conversion log
     */
    public void addConversionLog(ConversionLogDto conversionLog) {
        if (this.conversionLogs == null) {
            this.conversionLogs = new ArrayList<ConversionLogDto>();
        }
        switch (conversionLog.getType()) {
            case ERROR:
                this.containsErrors = true;
            case WARNING:
                this.containsWarnings = true;
            case INFO:
                this.containsInfos = true;
        }
        this.conversionLogs.add(conversionLog);

    }

    /**
     * Add new conversion log message.
     *
     * @param type type
     * @param message message
     * @param category category
     */
    public void addConversionLog(ConversionLogType type, String message, String category) {
        addConversionLog(new ConversionLogDto(type, message, category));
    }

    /**
     * @return the containsWarnings
     */
    public boolean isContainsWarnings() {
        return containsWarnings;
    }

    /**
     * @return the containsErrors
     */
    public boolean isContainsErrors() {
        return containsErrors;
    }

    /**
     * @return the containsInfos
     */
    public boolean isContainsInfos() {
        return containsInfos;
    }

    /**
     * @return the sourceUrl
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @param sourceUrl
     *            the sourceUrl to set
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConversionResultDto [statusCode=" + statusCode + ", statusDescription=" + statusDescription + ", conversionLogs="
                + conversionLogs + ", containsWarnings=" + containsWarnings + ", containsErrors=" + containsErrors
                + ", containsInfos=" + containsInfos + ", sourceUrl=" + sourceUrl + ", convertedXmls=" + convertedXmls + "]";
    }

    /**
     * Gets conversion log as HTML
     * @return
     */
    public String getConversionLogAsHtml() {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("<div class=\"feedback\">");
        strBuilder.append("<h2>Conversion log</h2>");
        if (!Utils.isNullStr(getStatusDescription())) {
            strBuilder.append(getResultAsHtml());
        }
        if (!Utils.isNullStr(getSourceUrl())) {
            strBuilder.append("<div>Converted file: <a href=\"" + getSourceUrl() + "\">" + getSourceUrl() + "</a></div>");
        }
        if (!Utils.isNullList(conversionLogs)) {
            strBuilder.append("<table class=\"datatable\">");
            strBuilder.append("<tr><th>Log level</th><th>Category</th><th>Log message</th></tr>");
            for (ConversionLogDto conversionLog : conversionLogs) {
                strBuilder.append("<tr><td");
                switch (conversionLog.getType()) {
                    case CRITICAL:
                        strBuilder.append(" style=\"color:red\"");
                    case ERROR:
                        strBuilder.append(" style=\"color:red\"");
                    case WARNING:
                        strBuilder.append(" style=\"color:orange\"");
                }
                strBuilder.append(">");
                strBuilder.append(conversionLog.getType());
                strBuilder.append("</td><td>");
                strBuilder.append(StringEscapeUtils.escapeXml10(conversionLog.getCategory()));
                strBuilder.append("</td><td>");
                strBuilder.append(StringEscapeUtils.escapeXml10(conversionLog.getMessage()));
                strBuilder.append("</td></tr>");
            }
            strBuilder.append("</table>");
        } else {
            strBuilder.append("<p>Conversion log not found.</p>");
        }
        strBuilder.append("</div>");
        return strBuilder.toString();
    }

    /**
     * Gets result as HTML.
     * @return Result
     */
    private String getResultAsHtml() {
        StringBuilder strBuilder = new StringBuilder();
        String resultClass = "";
        if (STATUS_OK.equals(getStatusCode())) {
            resultClass = "system-msg";
        } else if (STATUS_ERR_VALIDATION.equals(getStatusCode())) {
            resultClass = "caution-msg";
        } else if (STATUS_ERR_SYSTEM.equals(getStatusCode())) {
            resultClass = "error-msg";
        } else if (STATUS_ERR_SCHEMA_NOT_FOUND.equals(getStatusCode())) {
            resultClass = "error-msg";
        }
        if (!Utils.isNullStr(resultClass)) {
            resultClass = "class=\"" + resultClass + "\"";
        }
        strBuilder.append("<div ");
        strBuilder.append(resultClass);
        strBuilder.append(">");
        strBuilder.append(getStatusDescription());
        strBuilder.append(" (");
        strBuilder.append(Utils.getDateTime(new Date()));
        strBuilder.append(")");
        strBuilder.append("</div>");

        return strBuilder.toString();
    }

    /**
     * @return
     */
    public List<ConvertedFileDto> getConvertedFiles() {
        return this.convertedFiles;
    }

    /**
     * Gets converted file by file name
     * @param fileName File name
     * @return Converted file
     */
    public ConvertedFileDto getConvertedFileByFileName(String fileName) {
        if (convertedFiles != null) {
            for (ConvertedFileDto convertedFile : convertedFiles) {
                if (convertedFile.getFileName().equals(fileName)) {
                    return convertedFile;
                }
            }
        }
        return null;
    }
}
