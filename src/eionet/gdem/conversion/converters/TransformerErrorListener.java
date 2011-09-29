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
 * The Original Code is XMLCONV.
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA).
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */
package eionet.gdem.conversion.converters;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * ErrorListener for XSLT Transformer
 *
 * @author Enriko Käsper, TietoEnator Estonia AS TransformerErrorListener
 */
public class TransformerErrorListener implements javax.xml.transform.ErrorListener {

    /** */
    private static final Log LOGGER = LogFactory.getLog(TransformerErrorListener.class);

    @Override
    public void warning(TransformerException te) throws TransformerException {
        LOGGER.error(formatTransformerException(0, te));
    }

    @Override
    public void error(TransformerException te) throws TransformerException {
        throw new TransformerException(formatTransformerException(1, te));
    }

    @Override
    public void fatalError(TransformerException te) throws TransformerException {
        Throwable cause = te.getException();
        if (cause != null) {
            if (cause instanceof SAXException) {
                throw te;
            } else {
                throw new TransformerException(formatTransformerException(2, te));
            }
        } else {
            throw new TransformerException(formatTransformerException(2, te));
        }
    }

    public static String formatTransformerException(int errType, TransformerException te) {
        String[] errorTypes = {"WARNING", "ERROR", "FATAL ERROR"};
        String msg = te.getMessageAndLocation();
        String msgout = "The XSLT processor reported the following " + errorTypes[errType] + ":\n" + msg;
        return msgout;
    }

}
