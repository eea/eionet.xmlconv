/*
 * Created on 26.02.2008
 */
package eionet.gdem.web.struts.conversion;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.DynaValidatorForm;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS Excel2XmlConversionAction
 */

public class Excel2XmlConversionAction extends Action {
    private static LoggerIF _logger = GDEMServices.getLogger();

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        ActionErrors errors = new ActionErrors();

        String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);

        DynaValidatorForm cForm = (DynaValidatorForm) actionForm;
        String url = processFormStr((String) cForm.get("url"));
        String split = processFormStr((String) cForm.get("split"));
        String sheet = processFormStr((String) cForm.get("sheet"));
        Boolean showConversionLog = processFormBoolean((Boolean) cForm.get("showConversionLog"));

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
            // flush the content
            if (!showConversionLog &&
                    (ConversionResultDto.STATUS_OK.equals(conversionResult.getStatusCode()) ||
                            ConversionResultDto.STATUS_ERR_VALIDATION.equals(conversionResult.getStatusCode()))) {
                if (conversionResult.getConvertedXmls().size()>0) {
                    String firstXml = conversionResult.getConvertedXmls().keySet().iterator().next();
                    String resultFile = conversionResult.getConvertedXmls().get(firstXml);
                    // create custom HttpServletResponseWrapper
                    HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
                    methodResponse.setContentType("text/xml");
                    methodResponse.setContentDisposition(firstXml);
                    methodResponse.getOutputStream().write(resultFile.getBytes("UTF-8"));
                    methodResponse.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error testing conversion", e);
            HttpSession sess = httpServletRequest.getSession(true);
            sess.setAttribute("gdem.exception", new GDEMException("Error testing conversion: " + e.getMessage()));
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/" + Names.ERROR_JSP);
            return actionMapping.findForward(null);
        }
        return actionMapping.findForward("success");
    }

    private String processFormStr(String arg) {
        String result = null;
        if (arg != null) {
            if (!arg.trim().equalsIgnoreCase("")) {
                result = arg.trim();
            }
        }
        return result;
    }
    private Boolean processFormBoolean(Boolean arg) {
        if (arg == null) {
            arg = false;
        }
        return arg;
    }
}
