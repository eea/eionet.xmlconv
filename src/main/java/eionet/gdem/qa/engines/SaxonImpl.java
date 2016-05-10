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

import eionet.gdem.GDEMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.URI;

/**
 * Saxon XQuery Engine Implementation.
 * @author Unknown
 * @author George Sofianos
 */
public class SaxonImpl extends QAScriptEngineStrategy {

    /** */
    private static final Logger logger = LoggerFactory.getLogger(SaxonImpl.class);

    /**
     * Default Constructor
     * @throws GDEMException If an error occurs.
     */
    public SaxonImpl() throws GDEMException {
    }

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws GDEMException {

        Processor proc = SaxonProcessor.getProcessor();
        XQueryCompiler comp = proc.newXQueryCompiler();

        String queriesPathURI = Utils.getURIfromPath(eionet.gdem.Properties.queriesFolder, true);
        comp.setBaseURI(URI.create(queriesPathURI));
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

            XQueryExecutable exp = comp.compile(script.getScriptSource());
            XQueryEvaluator ev = exp.load();
            ev.setExternalVariable(new QName("source_url"), new XdmAtomicValue(script.getSrcFileUrl()));
            XdmValue val = ev.evaluate();
            proc.writeXdmValue(val, out);
        } catch (SaxonApiException e) {
            logger.debug("Error in xquery script: " + e.getMessage());
            throw new GDEMException(e.getMessage(), e);
        }
    }
}
