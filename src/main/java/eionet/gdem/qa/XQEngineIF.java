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

import java.io.OutputStream;

import eionet.gdem.XMLConvException;

/**
 * Interface for XQuery Engine implementation.
 */
public interface XQEngineIF {

    String DEFAULT_ENCODING = "UTF-8";
    String DEFAULT_OUTPUTTYPE = "html";
    String HTML_CONTENT_TYPE = "html";
    String XML_CONTENT_TYPE = "xml";

    /**
     * processes the XQuery.
     *
     * @param script
     *            the XQscript object with required attributes
     * @return the result of XQuery
     * @throws XMLConvException If an error occurs.
     */
    String getResult(XQScript script) throws XMLConvException;

    /**
     * Gets result
     * @param script Script
     * @param out OutputStream
     * @throws XMLConvException If an error occurs.
     */
    void getResult(XQScript script, OutputStream out) throws XMLConvException;

    /**
     * processes the XQuery.
     *
     * @param xqScript
     *            the XQuery script
     * @param params
     *            XQuery parameter name value pairs in format {name1=value1, name2=value2, ... , nameN=valueN}
     * @return the result of XQuery
     * @throws eionet.gdem.XMLConvException
     */
    /*
     * public String getResult(String xqScript, String params[]) throws XMLConvException;
     *
     * public void getResult(String xqScript, String params[], OutputStream out) throws XMLConvException;
     */
    /**
     * get encoding for XQuery engine to use. If not set use default encoding UTF-8.
     *
     * @return
     */
    String getEncoding();

    /**
     * set encoding parameter for XQuery engine. If not set use default encoding UTF-8.
     *
     * @param encoding Encoding
     */
    void setEncoding(String encoding);

    /**
     * get output type of the XQuery script result. Default is text/html.
     *
     * @return
     */
    String getOutputType();

    /**
     * set output type for XQuery engine. If output type is text/xml, then the XML declaration is omitted to the result.
     *
     * @param outputType Output Type
     */
    void setOutputType(String outputType);
}
