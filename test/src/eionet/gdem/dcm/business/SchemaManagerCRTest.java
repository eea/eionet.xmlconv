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

package eionet.gdem.dcm.business;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;
import eionet.gdem.dto.CrFileDto;

/**
 * This is a class for unit testing the <code>eionet.gdem.dcm.business.SchemaManager</code> class.
 *
 * @author Enriko Käsper, Tieto Estonia
 * SchemaManagerCRTest
 */

public class SchemaManagerCRTest extends TestCase {

    /**
     * Test getCrFiles method. The result should be list of CrFileDto objects.
     *
     * @throws Exception
     */
    public void testGetCrFiles() throws Exception{
        MockSchemaManager mockSchemaManager = new MockSchemaManager();
        List<CrFileDto> crFiles = mockSchemaManager.getCRFiles("http://test.com/schema.xsd");

        assertEquals(crFiles.size(),3);
        assertEquals(crFiles.get(0).getUrl(),"http://test.com/file1.xml");
        assertEquals(crFiles.get(0).getLastModified(),"2006-07-03T13:19:33");
        assertEquals(crFiles.get(0).getLabel(),"http://test.com/file1.xml - (modified: 2006-07-03T13:19:33)");

        assertEquals(crFiles.get(1).getUrl(),"http://test.com/file2.xml");
        assertEquals(crFiles.get(1).getLastModified(),"2007-07-03T13:19:33");

        assertEquals(crFiles.get(2).getUrl(),"http://test.com/file3.xml");
        assertEquals(crFiles.get(2).getLastModified(),"2008-07-03T13:19:33");
}
    class MockSchemaManager extends SchemaManager {
        /**
         * Override getXmlFilesBySchema and construct the result of xml-rpc method (CRServiceClient.getXmlFilesBySchema(schema)
         */
        protected List getXmlFilesBySchema(String schema){

            Hashtable hash1 = new Hashtable();
            hash1.put("uri", "http://test.com/file1.xml");
            hash1.put("lastModified", "2006-07-03T13:19:33");

            Hashtable hash2 = new Hashtable();
            hash2.put("uri", "http://test.com/file2.xml");
            hash2.put("lastModified", "2007-07-03T13:19:33");

            Hashtable hash3 = new Hashtable();
            hash3.put("uri", "http://test.com/file3.xml");
            hash3.put("lastModified", "2008-07-03T13:19:33");

            ArrayList list= new ArrayList();
            list.add(hash1);
            list.add(hash2);
            list.add(hash3);
            return list;
        }
    }

}
