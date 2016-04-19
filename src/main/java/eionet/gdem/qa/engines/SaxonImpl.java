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

package eionet.gdem.qa.engines;

import java.io.*;
import java.net.URI;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.FeatureKeys;
import net.sf.saxon.s9api.*;
import net.sf.saxon.value.StringValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;

public class SaxonImpl extends QAScriptEngineStrategy {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SaxonImpl.class);

    public SaxonImpl() throws GDEMException {
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws GDEMException {

        SaxonListener listener = new SaxonListener();

        Processor proc = new Processor(false);
        //System.err.println(proc.getSaxonEdition());
        //proc.setConfigurationProperty("http://saxon.sf.net/feature/generateByteCode", "false");
        proc.setConfigurationProperty("http://saxon.sf.net/feature/timing", "true");
        proc.setConfigurationProperty("http://saxon.sf.net/feature/trace-external-functions", "true");
        proc.setConfigurationProperty("http://saxon.sf.net/feature/allow-multithreading", "true");
        proc.setConfigurationProperty("http://saxon.sf.net/feature/optimizationLevel", "0");

        XQueryCompiler comp = proc.newXQueryCompiler();

        String queriesPathURI = Utils.getURIfromPath(eionet.gdem.Properties.queriesFolder, true);
        comp.setBaseURI(new File(queriesPathURI).toURI());
        comp.setErrorListener(listener);
        try {
            Serializer out = proc.newSerializer(result);
            XQueryExecutable exp = comp.compile(script.getScriptSource());
            XQueryEvaluator ev = exp.load();
            ev.setExternalVariable(new QName("source_url"), new XdmAtomicValue(script.getSrcFileUrl()));
            XdmValue val = ev.evaluate();
            proc.writeXdmValue(val, out);
        } catch (SaxonApiException e) {
            e.printStackTrace();
        }
    }
/*
        // Source sourceInput = null;
        // StringBuffer err_buf = new StringBuffer();

        Configuration config = new Configuration();

        // our own extension of Saxon's error listener to send feedback to the user

        config.setErrorListener(listener);
        config.setURIResolver(new QAURIResolver());

        config.setHostLanguage(Configuration.XQUERY);
        config.setLineNumbering(true);
        StaticQueryContext staticEnv = new StaticQueryContext(config);
        // staticEnv.setConfiguration(config);
        DynamicQueryContext dynamicEnv = new DynamicQueryContext(config);

        SaxonListener dynamicListener = new SaxonListener();
        dynamicEnv.setErrorListener(dynamicListener);

        Properties outputProps = new Properties();
        outputProps.setProperty(OutputKeys.INDENT, "no");
        outputProps.setProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
        // if the output is html, then use method="xml" in output, otherwise, it's not valid xml
        if (getOutputType().equals(HTML_CONTENT_TYPE)) {
            outputProps.setProperty(OutputKeys.METHOD, XML_CONTENT_TYPE);
        } else {
            outputProps.setProperty(OutputKeys.METHOD, getOutputType());
        }
        // add xml declaration only, if the output should be XML
        if (getOutputType().equals(XML_CONTENT_TYPE)) {
            outputProps.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        } else {
            outputProps.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        String queriesPathURI = Utils.getURIfromPath(eionet.gdem.Properties.queriesFolder, true);
        if (queriesPathURI != null) {
            staticEnv.setBaseURI(queriesPathURI);
        }

        Reader queryReader = null;

        try {
            if (!Utils.isNullStr(script.getScriptSource())) {
                queryReader = new StringReader(script.getScriptSource());
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                queryReader = new FileReader(script.getScriptFileName());
            } else {
                throw new GDEMException("XQuery engine could not find script source or script file name!");
            }

            // handle xq Parameters, extract from Saxon code
            if (script.getParams() != null) {
                for (int p = 0; p < script.getParams().length; p++) {
                    String arg = script.getParams()[p];
                    int eq = arg.indexOf("=");
                    if (eq < 1 || eq >= arg.length() - 1) {
                        throw new GDEMException("Bad param=value pair");
                        // handleError("Bad param=value pair", true);
                    }
                    String argname = arg.substring(0, eq);
                    if (argname.startsWith("!")) {
                        // parameters starting with "!" are taken as output properties
                        outputProps.setProperty(argname.substring(1), arg.substring(eq + 1));
                    } else if (argname.startsWith("+")) {
                        // parameters starting with "+" are taken as inputdocuments
                        // List sources = Transform.loadDocuments(arg.substring(eq+1), true, config);
                        // dynamicEnv.setParameter(argname.substring(1), sources);
                    } else {
                        dynamicEnv.setParameter(argname, new StringValue(arg.substring(eq + 1)));
                    }

                }
            }
            // compile XQuery
            XQueryExpression exp;
            try {
                exp = staticEnv.compileQuery(queryReader);
                staticEnv = exp.getStaticContext();
            } catch (net.sf.saxon.trans.XPathException e) {
                throw e;
            } catch (java.io.IOException e) {
                throw e;
            }

            try {
                // evaluating XQuery
                exp.run(dynamicEnv, new StreamResult(result), outputProps);
            } catch (net.sf.saxon.trans.XPathException e) {
                listener.error(e);
            }

        } catch (Exception e) {
            String errMsg = (listener.hasErrors() ? listener.getErrors() : e.toString());
            try {
                errMsg = parseErrors(errMsg, staticEnv);
            } catch (Exception ex) {
                LOGGER.error("Unable to parse exception string: " + ex.toString());
            }

            LOGGER.error("==== CATCHED EXCEPTION " + errMsg, e);
            throw new GDEMException(errMsg, e);
            // listener.error(e);
        } finally {
            if (queryReader != null) {
                try {
                    queryReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (listener.hasErrors() || dynamicListener.hasErrors()) {
                String errMsg = listener.getErrors() + dynamicListener.getErrors();
                try {
                    errMsg = parseErrors(errMsg, staticEnv);
                } catch (Exception ex) {
                    LOGGER.error("Unable to parse exception string: " + ex.toString());
                }
                LOGGER.error(errMsg);
                throw new GDEMException(errMsg);
            }
        }
    }

    // if URL contains ticket information, then remove it
    // if the error messages contains staticEnv.baseURI, then remove it
    private String parseErrors(String err, StaticQueryContext staticEnv) {

        if (err == null) {
            return null;
        }

        String baseURI = (staticEnv == null) ? null : staticEnv.getBaseURI();

        if (baseURI != null && err.indexOf(baseURI) > 0) {
            err = eionet.gdem.utils.Utils.Replace(err, baseURI, "xquery");
        }

        err = err.replaceAll(Constants.TICKET_PARAM + "=.*?&", "");
        err = err.replaceAll(Constants.TICKET_PARAM + "%3D.*?%26", "");
        err = err.replaceAll("systemId:.*source_url=", "systemId: ");
        return err;
    }*/
}
