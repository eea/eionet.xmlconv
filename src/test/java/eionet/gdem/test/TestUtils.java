/*
 * Created on 17.03.2008
 */
package eionet.gdem.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionMapping;

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.mocks.MockActionMapping;
import eionet.gdem.test.mocks.MockServletMultipartRequest;
import eionet.gdem.test.mocks.MockServletRequest;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.web.struts.BaseAction;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS TestUtils
 */

public class TestUtils {

    /**
     * Set up test runtime properties
     *
     * @param obj
     */
    public static void setUpProperties(Object obj) {
        GDEMServices.setTestConnection(true);
        Properties.metaXSLFolder = Properties.appRootFolder + "/dcm";
        Properties.convFile = Properties.metaXSLFolder + "/conversions.xml";

        /*
         * GDEMServices.setTestConnection(true); String conversions_filename =
         * obj.getClass().getClassLoader().getResource("dcm/conversions.xml").getFile(); Properties.convFile = conversions_filename;
         * Properties.metaXSLFolder = obj.getClass().getClassLoader().getResource("dcm").getFile(); Properties.schemaFolder =
         * obj.getClass().getClassLoader().getResource("schema").getFile(); Properties.queriesFolder =
         * obj.getClass().getClassLoader().getResource("queries").getFile(); Properties.xmlfileFolder =
         * obj.getClass().getClassLoader().getResource("xmlfile").getFile();
         */
    }

    /**
     * get struts config location
     *
     * @param obj
     */
    public static String getStrutsConfigLocation() {
        return Properties.appRootFolder + "/webapp/WEB-INF/struts-config.xml";
    }

    public static File getContextDirectory() {
        return new File(Properties.appRootFolder + "/webapp");
    }

    public static String getStrutsTempDir(Object obj) {
        String s = obj.getClass().getClassLoader().getResource("schema").getPath().substring(1);
        return s;
    }

    /**
     * construct URI from seed file name
     *
     * @param seedName
     *            eg. "seed.xml"
     * @return
     */
    public static String getSeedURL(String seedName, Object obj) {

        String filename = obj.getClass().getClassLoader().getResource(seedName).getFile();
        return "file://".concat(filename);
    }

    /**
     * Method for executing Struts action.execute() methods. The method returns MockServletResponse object and it's header and
     * outputstream can be validated.
     *
     * @param action
     *            Action class, that excute() method will be called
     * @param paramsMap
     *            HasMap contains request parameters
     * @return MockServletResponse
     * @throws Exception
     */
    public static MockServletResponse executeAction(BaseAction action, Map paramsMap) throws Exception {
        // Create the mock objects
        MockServletRequest request = new MockServletRequest();
        ActionMapping actionMap = new MockActionMapping();
        MockServletResponse response = new MockServletResponse();

        request.setParameterMap(paramsMap);

        action.execute(actionMap, null, request, response);

        return response;
    }

    /**
     * Method for executing Struts action.execute() methods with multipart request The method returns MockServletResponse object and
     * it's header and outputstream can be validated.
     *
     * @param action
     *            initilised action class
     * @param paramsMap
     *            set it to request parameter map
     * @param uploadFile
     *            the full path to file
     * @param fileContentType
     *            file content type
     * @return
     * @throws Exception
     */
    public static MockServletResponse executeActionMultipart(BaseAction action, Map paramsMap, String fileItemParam,
            String uploadFile, String fileContentType) throws Exception {

        // Create the mock objects
        MockServletMultipartRequest request = new MockServletMultipartRequest();
        ActionMapping actionMap = new MockActionMapping();
        MockServletResponse response = new MockServletResponse();

        request.setParameterMap(paramsMap);
        request.writeFile(fileItemParam, uploadFile, fileContentType);
        action.execute(actionMap, null, request, response);

        return response;
    }

    // conversion service checks if the dataset is the latest released verison
    // otherwise conversion fails
    public static void setUpReleasedDataset() {
        Map<String, String> mockDataset = new HashMap<String, String>();
        mockDataset.put("id", "4948");
        mockDataset.put("status", "Released");
        mockDataset.put("isLatestReleased", "true");
        mockDataset.put("dateOfLatestReleased", "");
        mockDataset.put("idOfLatestReleased", "");
        DDServiceClient.setMockDataset(mockDataset);

    }
}
