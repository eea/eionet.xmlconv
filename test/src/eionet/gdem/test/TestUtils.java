/*
 * Created on 17.03.2008
 */
package eionet.gdem.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.mocks.MockActionMapping;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.web.struts.BaseAction;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * TestUtils
 */

public class TestUtils {


	/**
	 * Set up test runtime properties
	 *
	 * @param obj
	 */
	public static void setUpProperties(Object obj){

		GDEMServices.setTestConnection(true);
		String conversions_filename = obj.getClass().getClassLoader().getResource("dcm/conversions.xml").getFile();
		Properties.convFile = conversions_filename;
		Properties.metaXSLFolder = obj.getClass().getClassLoader().getResource("dcm").getFile();
	}

	/**
	 * construct URI from seed file name
	 * @param seedName	eg. "seed.xml"
	 * @return	
	 */
	public static String getSeedURL(String seedName, Object obj){

		String filename = obj.getClass().getClassLoader().getResource(seedName).getFile();

		return "file://".concat(filename);
	}
	/**
	 * Method for executing Struts action.execute() methods. 
	 * The method returns MockServletResponse object and it's header and outputstream can be validated.
	 * 
	 * @param action		Action class, that excute() method will be called
	 * @param paramsMap		HasMap contains request parameters 
	 * @return				MockServletResponse
	 * @throws Exception
	 */
	public static MockServletResponse executeAction(BaseAction action, Map paramsMap) throws Exception{
		// Create the mock objects		
		HttpServletRequest request = createMock(HttpServletRequest.class);
		ActionMapping actionMap = new MockActionMapping();		
		MockServletResponse response = new MockServletResponse();

		expect(request.getParameterMap()).andReturn(paramsMap);
		expect(request.getSession(false)).andReturn(null);
		replay(request);

		action.execute(actionMap, null, request, response);

		return response;
	}
}
