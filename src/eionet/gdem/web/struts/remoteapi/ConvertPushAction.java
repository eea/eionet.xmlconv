/*
 * Created on 07.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.BaseAction;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionAction
 */

public class ConvertPushAction extends BaseAction {
    private static LoggerIF _logger = GDEMServices.getLogger();

    public static final String CONVERT_ID_PARAM_NAME = "convert_id";
    public static final String CONVERT_FILE_PARAM_NAME = "convert_file";

    /**
     * Purpose of this action is to execute Conversion Service convertPush method. The request should contain multipart/form-data
     * and convert_id parameter
     *
     * @throws ServletException
     */
    @Override
    public ActionForward execute(ActionMapping map, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse httpServletResponse) throws ServletException {

        InputStream fileInput = null;
        Map params = null;

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
        try {
            String convert_id = null;
            String fileName = null;

            // parse multipart form data
            MultipartFileUpload fu = new MultipartFileUpload(false);
            fu.processMultiPartRequest(request);
            params = fu.getRequestParams();

            // get convert_id parameter
            if (params.containsKey(CONVERT_ID_PARAM_NAME)) {
                convert_id = (String) params.get(CONVERT_ID_PARAM_NAME);
            }
            if (Utils.isNullStr(convert_id)) {
                throw new GDEMException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
            }

            // get the file as inputstream from request
            fileInput = fu.getFileAsInputStream(CONVERT_FILE_PARAM_NAME);
            fileName = fu.getFileName(CONVERT_FILE_PARAM_NAME);

            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            // execute conversion
            cs.convertPush(fileInput, convert_id, fileName);
        } catch (Exception e) {
            _logger.error(e.toString());
            try {
                // error happened
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), map.getPath(), params);
            } catch (Exception ge) {
                _logger.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        } finally {
            if (methodResponse != null){
                try{
                    // flush the content
                    methodResponse.flush();
                }
                catch(Exception e){
                    _logger.error("Unable to close Servlet Output Stream.", e);
                    e.printStackTrace();
                }
            }
            try {
                fileInput.close();
            } catch (Exception e) {
                _logger.error("Unable to close inputstream.");
            }
        }
        // Do nothing, the response is already sent.
        return null;
    }
}
