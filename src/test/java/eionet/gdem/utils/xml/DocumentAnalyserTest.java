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

import junit.framework.TestCase;
import eionet.gdem.test.TestConstants;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, Tieto Estonia DocumentAnalyserTest
 */

public class DocumentAnalyserTest extends TestCase {

    public void testXMLSchema() throws Exception {

        String schemaFile = getClass().getClassLoader().getResource(TestConstants.SEED_GW_CONTAINER_SCHEMA).getFile();
        byte[] schemaBytes = Utils.fileToBytes(schemaFile);
        String dtdFile = getClass().getClassLoader().getResource(TestConstants.SEED_XLIFF_DTD).getFile();
        byte[] dtdBytes = Utils.fileToBytes(dtdFile);

        boolean isSchema = DocumentAnalyser.sourceIsXMLSchema(schemaBytes);
        assertTrue(isSchema);
        isSchema = DocumentAnalyser.sourceIsXMLSchema(dtdBytes);
        assertFalse(isSchema);
    }

    public void testDTD() throws Exception {

        String schemaFile = getClass().getClassLoader().getResource(TestConstants.SEED_GW_SCHEMA).getFile();
        byte[] schemaBytes = Utils.fileToBytes(schemaFile);
        String dtdFile = getClass().getClassLoader().getResource(TestConstants.SEED_XLIFF_DTD).getFile();
        byte[] dtdBytes = Utils.fileToBytes(dtdFile);

        boolean isDTD = DocumentAnalyser.sourceIsDTD(schemaBytes);
        assertFalse(isDTD);
        isDTD = DocumentAnalyser.sourceIsDTD(dtdBytes);
        assertTrue(isDTD);
    }

}
