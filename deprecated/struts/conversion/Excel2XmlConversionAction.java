/*
 * Created on 26.02.2008
 */
package eionet.gdem.web.struts.conversion;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eionet.gdem.Constants;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS Excel2XmlConversionAction
 */

public class Excel2XmlConversionAction extends Action {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(Excel2XmlConversionAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        ActionErrors errors = new ActionErrors();

        String ticket = (String) httpServletRequest.getSession().getAttribute(Constants.TICKET_ATT);

        DynaValidatorForm cForm = (DynaValidatorForm) actionForm;
        String url = processFormStr((String) cForm.get("url"));
        String split = processFormStr((String) cForm.get("split"));
        String sheet = processFormStr((String) cForm.get("sheet"));
        Boolean showConversionLog = processFormBoolean((Boolean) cForm.get("showConversionLog"));
        HttpMethodResponseWrapper methodResponse = null;

        // get request parameters
        try {
            // parse request parameters
            if (Utils.isNullStr(url)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.insertExcelUrl"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("error");
            }
            if (Utils.isNullStr(split)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.insertSplit"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("error");
            }
            if (split.equals("split") && Utils.isNullStr(sheet) && !showConversionLog) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.insertSheet"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("error");
            }
            ConversionServiceIF cs = new ConversionService();
            cs.setTicket(ticket);
            if (!showConversionLog) {
                methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
                cs.setHttpResponse(methodResponse);
            }
            cs.setTrustedMode(true);
            ConversionResultDto conversionResult = null;
            // execute conversion
            if (split.equals("split")) {
                conversionResult = cs.convertDD_XML(url, true, sheet);
            } else {
                conversionResult = cs.convertDD_XML(url, false, null);
            }
            String conversionLog  = conversionResult.getConversionLogAsHtml();
            if (!Utils.isNullStr(conversionLog)){
                cForm.set("conversionLog", conversionLog);
            }
            else{
                cForm.set("conversionLog", "Conversion log not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error testing conversion", e);
            HttpSession sess = httpServletRequest.getSession(true);
            sess.setAttribute("gdem.exception", new XMLConvException("Error testing conversion: " + e.getMessage()));
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/" + Constants.ERROR_JSP);
            return null;
        }
        finally{
            if (methodResponse != null){
                try{
                    methodResponse.flush();
                    return null;
                }
                catch (Exception e) {
                    LOGGER.error("Unable to flush content. ", e);
                    e.printStackTrace();
                }
            }
        }
        return actionMapping.findForward("success");
    }

    /**
     * Process String
     * @param arg Argument
     * @return Result
     */
    private String processFormStr(String arg) {
        String result = null;
        if (arg != null) {
            if (!arg.trim().equalsIgnoreCase("")) {
                result = arg.trim();
            }
        }
        return result;
    }

    /**
     * Process Boolean
     * @param arg Argument
     * @return Result
     */
    private Boolean processFormBoolean(Boolean arg) {
        if (arg == null) {
            arg = false;
        }
        return arg;
    }
}
