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

package eionet.gdem.qa.engines;

import java.util.HashMap;

import junit.framework.TestCase;
import eionet.gdem.Properties;
import org.junit.Test;

/**
 * @author Enriko Käsper, Tieto Estonia XGawkQueryEngineTest
 */

public class XGawkQueryEngineTest extends TestCase {

    @Test
    public void testGetShellCommand() throws Exception {
        String dataFile = "data.xml";
        String scriptFile = "script.xml";

        XGawkQueryEngine engine = new XGawkQueryEngine();
        String command = engine.getShellCommand(dataFile, scriptFile, null);

        assertEquals(Properties.xgawkCommand + " -f script.xml data.xml", command);

    }

    //TODO(refactor): HashMap.get, Iterators does not gurantee that the objects will fetched in the same order the were inserted
    @Test
    public void testGetShellCommandWithParams() throws Exception {
        String dataFile = "data.xml";
        String scriptFile = "script.xml";
        HashMap params = new HashMap();
        params.put("param2", "param2value");
        params.put("source_url", "http://localhost/dummy.xml");

        XGawkQueryEngine engine = new XGawkQueryEngine();
        String command = engine.getShellCommand(dataFile, scriptFile, params);
        
        assertEquals(Properties.xgawkCommand + " -v source_url=\"http://localhost/dummy.xml\" -v param2=\"param2value\" "
                + "-f script.xml data.xml", command);

    }
}
