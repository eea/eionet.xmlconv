/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.web.struts.stylesheet;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestUtils;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import servletunit.struts.MockStrutsTestCase;

/**
 *
 * @author Nakas Nikolaos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public final class EditStylesheetFormActionTest extends MockStrutsTestCase {
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        super.setContextDirectory(TestUtils.getContextDirectory());
    }
    @Test
    public void testNonExistingStylesheet() {
        super.setRequestPathInfo("/stylesheetViewForm?stylesheetId=9999999");
        
        try {
            super.actionPerform();
        }
        catch (AssertionError err) { }
        
        assertEquals(super.getMockResponse().getStatusCode(), HttpServletResponse.SC_NOT_FOUND);
    }
}
