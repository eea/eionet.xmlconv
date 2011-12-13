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

package eionet.gdem.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import com.wutka.dtd.DTDParser;

import eionet.gdem.exceptions.DCMException;

/**
 * @author Enriko Käsper, Tieto Estonia DocumentAnalyser
 */

public class DocumentAnalyser {

    public static boolean sourceIsXMLSchema(byte[] bytes) throws DCMException {

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            javax.xml.validation.Schema s =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(is));

            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

    public static boolean sourceIsDTD(byte[] bytes) throws DCMException {

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            DTDParser dtdparser = new DTDParser(new InputStreamReader(is));
            dtdparser.parse();
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

}
