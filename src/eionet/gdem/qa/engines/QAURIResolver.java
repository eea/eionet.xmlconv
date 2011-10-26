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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */
package eionet.gdem.qa.engines;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import eionet.gdem.Properties;

/**
 * Type for resolving QA URIs. If XML file is referenced from XQuery as file in the root folder, then it is resolved to correct
 * location in filesystem.
 *
 * @author Enriko Käsper
 */

public class QAURIResolver implements URIResolver {

    /**
     * The logger.
     */
    private static Logger LOGGER = Logger.getLogger(QAURIResolver.class);

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        Source resolveResult = null;
        if (!href.contains("/") && !href.contains("\\") && !href.endsWith(".xquery")) {
            String xmlFilePath = Properties.xmlfileFolderPath + File.separator + href;
            File file = new File(xmlFilePath);
            if (file.exists()) {
                LOGGER.debug("Streaming XML file from local folder: " + xmlFilePath);
                return new StreamSource(file);
            }
        }
        return resolveResult;
    }
}
