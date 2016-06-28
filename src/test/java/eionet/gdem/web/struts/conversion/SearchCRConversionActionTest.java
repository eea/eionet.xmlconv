/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.CrServiceSparqlClient;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.test.ApplicationTestContext;
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
 * @author Enriko Käsper, TietoEnator Estonia AS SearchCRConversionActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SearchCRConversionActionTest extends MockStrutsTestCase {

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
     * test if the form is successfully forwarding and stores the schemas list in session
     */
    @Test
    public void testSuccessfulForward() {

        String schemaUrl = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";
        CrServiceSparqlClient.setMockXmlFilesBySchema(getXmlFilesBySchema(schemaUrl));

        setRequestPathInfo("/searchCR");

        addRequestParameter("schemaUrl", schemaUrl);
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/crConversion.jsp");
        verifyNoActionErrors();

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getSchemaUrl(), schemaUrl);

    }

    /**
     * test if the form is successfully forwarding and stores the schemas list in session
     */
    @Test
    public void testFailedForward() {

        String schemaUrl = "No such schema";

        setRequestPathInfo("/searchCR");

        addRequestParameter("schemaUrl", schemaUrl);
        actionPerform();
        verifyForward("error");
        verifyForwardPath("/do/crConversionForm");
        String[] errMess = {BusinessConstants.EXCEPTION_GENERAL};
        verifyActionErrors(errMess);

    }

    private List<CrFileDto> getXmlFilesBySchema(String schema) {

        CrFileDto crFile1 = new CrFileDto();
        crFile1.setUrl("http://test.com/file1.xml");
        crFile1.setLastModified("2006-07-03T13:19:33");

        CrFileDto crFile2 = new CrFileDto();
        crFile2.setUrl("http://test.com/file2.xml");
        crFile2.setLastModified("2007-07-03T13:19:33");

        CrFileDto crFile3 = new CrFileDto();
        crFile3.setUrl("http://test.com/file3.xml");
        crFile3.setLastModified("2008-07-03T13:19:33");

        List<CrFileDto> list = new ArrayList<CrFileDto>();
        list.add(crFile1);
        list.add(crFile2);
        list.add(crFile3);
        return list;
    }
}
