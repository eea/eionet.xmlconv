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
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.dto.Schema;
import eionet.gdem.qa.engines.*;

import java.io.OutputStream;

/**
 * Class for XQ script used by the workqueue XQTask and XQ sandbox.
 * @author Unknown
 * @author George Sofianos
 */
public class XQScript {
    private String[] params; // parameter name + value pairs
    private String strResultFile;
    private String scriptSource; // XQuery script
    private String outputType; // html, txt, xml
    private String scriptType; // xquery, xsl, xgawk
    private String scriptFileName; // full path of script file
    private String srcFileUrl;
    private Schema schema;
    private String jobId;
    private Boolean asynchronousExecution;

    private boolean srcFileDownloaded;

    public static final String SCRIPT_LANG_XQUERY1 = "xquery 1.0";
    public static final String SCRIPT_LANG_XQUERY3 = "xquery 3.0+";
    public static final String SCRIPT_LANG_XSL = "xsl";
    public static final String SCRIPT_LANG_XGAWK = "xgawk";
    public static final String SCRIPT_LANG_FME = "fme";

    public static final String[] SCRIPT_LANGS = {SCRIPT_LANG_XQUERY3, SCRIPT_LANG_XQUERY1, SCRIPT_LANG_XSL, SCRIPT_LANG_XGAWK, SCRIPT_LANG_FME, };

    public enum ScriptLang {
        SCRIPT_LANG_XQUERY("xquery"), SCRIPT_LANG_XSL("xsl"), SCRIPT_LANG_XGAWK("xgawk"), SCRIPT_LANG_FME("fme");
        private String value;

        /**
         * Constructor
         * @param value value
         */
        ScriptLang(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };

    public static final String SCRIPT_RESULTTYPE_XML = "XML";
    public static final String SCRIPT_RESULTTYPE_TEXT = "TEXT";
    public static final String SCRIPT_RESULTTYPE_HTML = "HTML";
    public static final String SCRIPT_RESULTTYPE_ZIP = "ZIP";

    public static final String[] SCRIPT_RESULTTYPES = {SCRIPT_RESULTTYPE_HTML, SCRIPT_RESULTTYPE_XML, SCRIPT_RESULTTYPE_TEXT, SCRIPT_RESULTTYPE_ZIP};

    public enum ScriptResultType {
    	HTML, XML, TEXT, ZIP
    }

    // XQ Engine instance
    private XQEngineIF engine;

    public XQScript() {
        super();
    }

    /**
     * @param xqScript Script
     * @param scriptParams
     *            XQ parameter name + value pairs in an array in format {name1=value1, name2=value2, ... , nameN=valueN} if no
     *            parameters, null should be passed
     */
    public XQScript(String xqScript, String[] scriptParams) {
        this(xqScript, scriptParams, XQEngineIF.DEFAULT_OUTPUTTYPE);
    }

    /**
     * Constructor
     * @param xqScript Script
     * @param scriptParams Parameters
     * @param outputType Output type
     */
    public XQScript(String xqScript, String[] scriptParams, String outputType) {
        this.scriptSource = xqScript;
        this.params = scriptParams;
        this.outputType = outputType;
        scriptType = SCRIPT_LANG_XQUERY1;
    }

    /**
     * Returns original file URL.
     * @return File URL
     */
    public String getOrigFileUrl() {
        if (srcFileUrl != null && srcFileUrl.indexOf(Constants.GETSOURCE_URL) > -1
                && srcFileUrl.indexOf(Constants.SOURCE_URL_PARAM) > -1) {

            return (srcFileUrl.substring(srcFileUrl.indexOf(Constants.SOURCE_URL_PARAM) + Constants.SOURCE_URL_PARAM.length() + 1));
        }

        return srcFileUrl;
    }

    public void setResulFile(String fileName) {
        strResultFile = fileName;
    }

    public String getStrResultFile() {
        return strResultFile;
    }

    public void setStrResultFile(String strResultFile) {
        this.strResultFile = strResultFile;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getSrcFileUrl() {
        return srcFileUrl;
    }

    public void setSrcFileUrl(String srcFileUrl) {
        this.srcFileUrl = srcFileUrl;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(String scriptSource) {
        this.scriptSource = scriptSource;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getScriptFileName() {
        return scriptFileName;
    }

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    public boolean isSrcFileDownloaded() {
        return srcFileDownloaded;
    }

    public void setSrcFileDownloaded(boolean srcFileDownloaded) {
        this.srcFileDownloaded = srcFileDownloaded;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
    
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Boolean getAsynchronousExecution() {
        return asynchronousExecution;
    }

    public void setAsynchronousExecution(Boolean asynchronousExecution) {
        this.asynchronousExecution = asynchronousExecution;
    }
}
