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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Dusan Popovic (ED)
 */

package eionet.gdem.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eionet.gdem.utils.Streams;

/**
 * DTD entity resolver.
 * @author Unknown
 * @author george Sofianos
 */
public class TransformDTDEntityResolver implements EntityResolver {
    private Map<String, byte[]> dtds;

    /**
     * Constructor
     * @param dtds dtds
     */
    public TransformDTDEntityResolver(Map<String, byte[]> dtds) {
        this.dtds = dtds;
    }

    /**
     * Returns resolver entity.
     * @param publicId Public Id
     * @param systemId System Id
     * @return Resolver entity
     * @throws SAXException If an error occurs.
     * @throws IOException If an error occurs.
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        try {
            // System.out.println("TSER: ( " + publicId + " Taking " + systemId + " from cache");

            byte[] dtd = (byte[]) dtds.get(systemId);
            if (dtd == null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                URL url = new URL(systemId);
                Streams.drain(url.openStream(), baos);
                dtd = baos.toByteArray();
                synchronized (dtds) {
                    dtds.put(systemId, dtd);
                }
            }

            if (dtd != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(dtd);
                InputSource is = new InputSource(bais);
                is.setPublicId(publicId);
                is.setSystemId(systemId);

                return is;
            }

        } catch (Throwable t) // java.io.IOException x
        {
            t.printStackTrace();
        }

        return null;

    }
}
