/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import eionet.gdem.test.ApplicationTestContext;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ValidateXMLFormActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ValidateXMLFormActionTest extends MockStrutsTestCase {

    @Autowired
    private DataSource db;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_CONVERSIONS_XML);

    }

    /**
     * test if the form is successfully formwarding and stores the schemas list in session
     */
    @Test
    public void testSuccessfulForward() {

        setRequestPathInfo("/validateXMLForm");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/validateXML.jsp");
        verifyNoActionErrors();

        List schemasInSession = (List) request.getAttribute("conversion.schemas");
        assertTrue(schemasInSession.size() > 0);

    }

}
