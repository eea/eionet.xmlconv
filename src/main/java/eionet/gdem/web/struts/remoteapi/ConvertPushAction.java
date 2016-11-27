/*
 * Created on 07.02.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.BaseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class ConvertPushAction extends BaseAction {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertPushAction.class);

    /** Conversion ID. */
    public static final String CONVERT_ID_PARAM_NAME = "convert_id";
    /** Binary data of the file. */
    public static final String CONVERT_FILE_PARAM_NAME = "convert_file";
    /** File name or URL of the file original location. */
    public static final String FILE_NAME_PARAM_NAME = "file_name";

    /**
     * Purpose of this action is to execute Conversion Service convertPush method. The request should contain multipart/form-data
     * and convert_id parameter. file_name parameter can be used to overwrite the value with full URL.
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
                throw new XMLConvException(CONVERT_ID_PARAM_NAME + " parameter is missing from request.");
            }

            // get the file as inputstream from request
            fileInput = fu.getFileAsInputStream(CONVERT_FILE_PARAM_NAME);
            // get file name from parameter, if this is not provided then use real file name from multipart content.
            if (params.containsKey(FILE_NAME_PARAM_NAME)) {
                fileName = (String) params.get(FILE_NAME_PARAM_NAME);
            } else {
                fileName = fu.getFileName(CONVERT_FILE_PARAM_NAME);
            }

            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            // execute conversion
            cs.convertPush(fileInput, convert_id, fileName);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            try {
                // error happened
                methodResponse.flushXMLError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), map.getPath(), params);
            } catch (Exception ge) {
                LOGGER.error("Unable to flush XML error: " + ge.toString());
                throw new ServletException(ge);
            }
        } finally {
            if (methodResponse != null) {
                try {
                    // flush the content
                    methodResponse.flush();
                } catch (Exception e) {
                    LOGGER.error("Unable to close Servlet Output Stream.", e);
                    e.printStackTrace();
                }
            }
            IOUtils.closeQuietly(fileInput);
        }
        // Do nothing, the response is already sent.
        return null;
    }
}
