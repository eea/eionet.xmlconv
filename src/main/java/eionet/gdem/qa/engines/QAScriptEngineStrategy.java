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

package eionet.gdem.qa.engines;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;




import eionet.gdem.XMLConvException;
import eionet.gdem.qa.QAResultPostProcessor;
import eionet.gdem.qa.XQEngineIF;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, Tieto Estonia QAScriptEngineStrategy
 */

public abstract class QAScriptEngineStrategy implements XQEngineIF {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptEngineStrategy.class);
    private String encoding = null;
    private String outputType = null;

    /**
     * Runs query
     * @param script Script to run
     * @param result Result
     * @throws XMLConvException If an error occurs.
     */
    protected abstract void runQuery(XQScript script, OutputStream result) throws XMLConvException;

    @Override
    public void getResult(XQScript script, OutputStream out) throws XMLConvException {
        try {
            setOutputType(script.getOutputType());
            runQuery(script, out);
        } catch (Exception e) {
            throw new XMLConvException(e.getMessage(), e);
        }
    }

    @Override
    public String getResult(XQScript script) throws XMLConvException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        String res = "";
        getResult(script, result);
        try {
            res = result.toString(DEFAULT_ENCODING);
            if (LOGGER.isDebugEnabled()){
                String logResult = res.length() > 299 ?  res.substring(0, 300) : res;
                LOGGER.debug("RESULT: \n" + logResult);
            }

        } catch (Exception e) {
            LOGGER.error("==== CATCHED EXCEPTION " + e.toString());
        }

        // add "red coloured warning" if script is expired
        if (script.getOutputType().equals(XQScript.SCRIPT_RESULTTYPE_HTML) && script.getSchema() != null) {

            QAResultPostProcessor postProcessor = new QAResultPostProcessor();
            res = postProcessor.processQAResult(res, script.getSchema());
        }

        return res;
    }

    @Override
    public String getEncoding() {
        if (Utils.isNullStr(encoding)) {
            encoding = DEFAULT_ENCODING;
        }

        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getOutputType() {
        if (Utils.isNullStr(outputType)) {
            outputType = DEFAULT_OUTPUTTYPE;
        }
        return outputType;
    }

    @Override
    public void setOutputType(String outputType) {
        outputType = (outputType == null) ? DEFAULT_OUTPUTTYPE : outputType.trim().toLowerCase();
        outputType = (outputType.equals("txt")) ? "text" : outputType;

        if (outputType.equals("xml") || outputType.equals("html") || outputType.equals("text") || outputType.equals("xhtml")) {
            this.outputType = outputType;
        } else {
            this.outputType = DEFAULT_OUTPUTTYPE;
        }
    }

    /**
     * Parses parameters
     * @param xqParams xquery parameters
     * @return Parameter map
     * @throws XMLConvException If an error occurs.
     */
    public HashMap parseParams(String[] xqParams) throws XMLConvException {
        HashMap<String, String> paramsMap = new HashMap<String, String>();

        if (xqParams != null) {
            for (int p = 0; p < xqParams.length; p++) {
                String arg = xqParams[p];
                int eq = arg.indexOf("=");
                if (eq < 1 || eq >= arg.length() - 1) {
                    throw new XMLConvException("Bad param=value pair");
                    // handleError("Bad param=value pair", true);
                }
                String argname = arg.substring(0, eq);
                paramsMap.put(argname, arg.substring(eq + 1));
            }

        }
        return paramsMap;
    }
}
