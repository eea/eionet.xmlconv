/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.web.struts.stylesheet;

import eionet.gdem.test.TestUtils;
import javax.servlet.http.HttpServletResponse;
import servletunit.struts.MockStrutsTestCase;

/**
 *
 * @author Nakas Nikolaos
 */
public final class EditStylesheetFormActionTest extends MockStrutsTestCase {
    
    public EditStylesheetFormActionTest(String testName) {
        super(testName);
    }
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.setContextDirectory(TestUtils.getContextDirectory());
    }
    
    public void testNonExistingStylesheet() {
        super.setRequestPathInfo("/stylesheetViewForm?stylesheetId=9999999");
        
        try {
            super.actionPerform();
        }
        catch (AssertionError err) { }
        
        assertEquals(super.getMockResponse().getStatusCode(), HttpServletResponse.SC_NOT_FOUND);
    }
}
