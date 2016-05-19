/*
 * Created on 21.04.2008
 */
package eionet.gdem.web.struts.schema;

import eionet.gdem.test.ApplicationTestContext;
import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS UplSchemaFormActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class UplSchemaFormActionTest extends MockStrutsTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
    }
    @Test
    public void testSuccessfulForward() {

        setRequestPathInfo("/uplSchemas");
        actionPerform();
        verifyForward("success");
        verifyTilesForward("success", "/uplSchema.jsp");
        verifyNoActionErrors();

        // test if the list of schemas is stored in request attribute
        UplSchemaHolder holder = (UplSchemaHolder) request.getAttribute("schemas.uploaded");
        assertTrue(holder.getSchemas().size() > 0);
    }
}
