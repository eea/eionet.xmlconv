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

import javax.xml.transform.TransformerException;


import net.sf.saxon.lib.StandardErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extension of the Saxon error listener to catch all the errors and feedback them to user.
 *
 * @author Unknown
 * @author George Sofianos
 */
public class SaxonListener extends StandardErrorListener {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaxonListener.class);

    private StringBuilder _errBuf; // in this buffer we collect all the error messages
    private boolean _hasErrors = false;

    /**
     * Default constructor
     */
    public SaxonListener() {
        _errBuf = new StringBuilder();
    }

    /**
     * Returns if listener has errors
     * @return
     */
    boolean hasErrors() {
        return _hasErrors;
    }

    /**
     * Returns all the error messages gathered when processing the XQuery script
     *
     * @return String errors - all the errors
     */
    public String getErrors() {
        return _errBuf.toString();
    }

    @Override
    public void error(TransformerException exception) {
        _hasErrors = true;
        String message = "Error " + getLocationMessage(exception) + "\n  " + getExpandedMessage(exception);

        _errBuf.append(message).append("\n");
        super.error(exception);
    }

    @Override
    public void warning(TransformerException exception) {
        _hasErrors = true;
        String message = "";
        if (exception.getLocator() != null) {
            message = getLocationMessage(exception) + "\n  ";
        }
        message += getExpandedMessage(exception);

        _errBuf.append(message).append("\n");

        super.warning(exception);
    }
}
