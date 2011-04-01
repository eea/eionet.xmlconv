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
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.qa.QAResultPostProcessor;
import eionet.gdem.qa.XQEngineIF;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.IXmlSerializer;
import eionet.gdem.utils.xml.XmlContext;
import eionet.gdem.utils.xml.XmlException;
import eionet.gdem.utils.xml.XmlSerialization;

/**
 * @author Enriko Käsper, Tieto Estonia
 * QAScriptEngineStrategy
 */

public abstract class QAScriptEngineStrategy  implements XQEngineIF{


    private static LoggerIF _logger = GDEMServices.getLogger();
    private String encoding = null;
    private String outputType = null;

    protected abstract void runQuery(XQScript script, OutputStream result) throws GDEMException;

    public void getResult(XQScript script,OutputStream out) throws GDEMException  {
        try {
            setOutputType(script.getOutputType());
            runQuery(script, out);

        } catch(Exception e) {
            throw new GDEMException(e.toString());
        }
    }

    public String getResult(XQScript script) throws GDEMException  {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        String res="";
        getResult(script, result);
        try{
            res = result.toString(DEFAULT_ENCODING);
            if (_logger.enable(LoggerIF.DEBUG)){
                _logger.debug("RESULT: \n" + res.substring(0,300));
            }

        } catch (Exception e) {
            _logger.error("==== CATCHED EXCEPTION " + e.toString() );
        }

        //add "red coloured warning" if script is expired
        if (script.getOutputType().equals(XQScript.SCRIPT_RESULTTYPE_HTML) &&
                script.getSchema() != null ) {

            QAResultPostProcessor postProcessor = new QAResultPostProcessor();
            res=postProcessor.processQAResult(res, script.getSchema());
        }

        return res;
    }

    public String getEncoding() {
        if(Utils.isNullStr(encoding))encoding=DEFAULT_ENCODING;

        return encoding;
    }
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    public String getOutputType() {
        if(Utils.isNullStr(outputType))outputType=DEFAULT_OUTPUTTYPE;
        return outputType;
    }
    public void setOutputType(String _outputType) {
        outputType= (_outputType==null) ? DEFAULT_OUTPUTTYPE : _outputType.trim().toLowerCase();
        outputType = (outputType.equals("txt"))?"text":outputType;

        if(outputType.equals("xml") || outputType.equals("html") ||
                outputType.equals("text") ||outputType.equals("xhtml"))
            this.outputType = outputType;
        else
            this.outputType = DEFAULT_OUTPUTTYPE;
    }
    public HashMap parseParams(String[] xqParams) throws GDEMException{
        HashMap<String,String> paramsMap = new HashMap<String,String>();

        if (xqParams!=null){
            for (int p=0; p<xqParams.length; p++) {
                String arg = xqParams[p];
                int eq = arg.indexOf("=");
                if (eq<1 || eq>=arg.length()-1) {
                    throw new GDEMException("Bad param=value pair");
                    //handleError("Bad param=value pair", true);
                }
                String argname = arg.substring(0,eq);
                paramsMap.put(argname, arg.substring(eq+1));
            }

        }
        return paramsMap;
    }
}

