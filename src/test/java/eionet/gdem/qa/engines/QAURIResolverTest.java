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

import eionet.gdem.test.ApplicationTestContext;
import javax.xml.transform.TransformerException;

import eionet.gdem.test.TestUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testing QAURIResolver
 *
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QAURIResolverTest {

    /**
     * Set up test case properties
     */
    @Before
    public void setUp() throws Exception {
        try {
            TestUtils.setUpProperties(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Test
    public void testResolve() throws TransformerException {

        QAURIResolver resolver = new QAURIResolver();
        assertNull(resolver.resolve("http://some.url.ee", ""));
        assertNull(resolver.resolve("script.xquery", ""));

        //Expecting StreamSource
        assertNotNull(resolver.resolve("seed-ozone-station.xml", ""));
    }

}
