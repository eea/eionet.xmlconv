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

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;

/**
 * Saxon XQuery Engine Implementation.
 * @author Unknown
 * @author George Sofianos
 */
public class SaxonImpl extends QAScriptEngineStrategy {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaxonImpl.class);

    /**
     * Default Constructor
     * @throws XMLConvException If an error occurs.
     */
    public SaxonImpl() throws XMLConvException {
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        Processor proc = SaxonProcessor.getProcessor();
        XQueryCompiler comp = proc.newXQueryCompiler();

        String queriesPathURI = Utils.getURIfromPath(eionet.gdem.Properties.queriesFolder, true);
        comp.setBaseURI(URI.create(queriesPathURI));

        Reader queryReader = null;
        try {
            Serializer out = proc.newSerializer(result);
            out.setOutputProperty(Serializer.Property.INDENT, "no");
            out.setOutputProperty(Serializer.Property.ENCODING, DEFAULT_ENCODING);
            // if the output is html, then use method="xml" in output, otherwise, it's not valid xml
            if (getOutputType().equals(HTML_CONTENT_TYPE)) {
                out.setOutputProperty(Serializer.Property.METHOD, XML_CONTENT_TYPE);
            } else {
                out.setOutputProperty(Serializer.Property.METHOD, getOutputType());
            }
            // add xml declaration only, if the output should be XML
            if (getOutputType().equals(XML_CONTENT_TYPE)) {
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
            } else {
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
            }
            if (!Utils.isNullStr(script.getScriptSource())) {
                queryReader = new StringReader(script.getScriptSource());
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                queryReader = new FileReader(script.getScriptFileName());
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }

            XQueryExecutable exp = comp.compile(queryReader);
            XQueryEvaluator ev = exp.load();
            ev.setExternalVariable(new QName("source_url"), new XdmAtomicValue(script.getSrcFileUrl()));
            //ev.setExternalVariable(new QName("base_url"), new XdmAtomicValue("http://" + Properties.appHost + Properties.contextPath));
            XdmValue val = ev.evaluate();
            proc.writeXdmValue(val, out);
        } catch (SaxonApiException e) {
            LOGGER.debug("Error in XQuery script: " + e.getMessage());
            throw new XMLConvException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            LOGGER.error("XQuery script file not found: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("IO Error while reading script: " + e.getMessage());
        } finally {
            if (queryReader != null) {
                try {
                    queryReader.close();
                } catch (IOException e) {
                    LOGGER.error("Error while attempting to close reader: " + e.getMessage());
                }
            }
        }
    }
}
