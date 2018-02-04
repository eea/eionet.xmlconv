/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.List;

import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ListConvFormActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ListConvFormActionTest extends MockStrutsTestCase {

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

        setRequestPathInfo("/listConvForm");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/listConv.jsp");
        verifyNoActionErrors();

        List schemasInSession = (List) request.getAttribute("conversion.schemas");
        assertTrue(schemasInSession.size() > 0);

    }

}
