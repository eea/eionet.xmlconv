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
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS Excel2XmlConversionAction
 */

public class Excel2XmlConversionAction extends Action {
    private static LoggerIF _logger = GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        ActionErrors errors = new ActionErrors();

        String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);

        DynaValidatorForm cForm = (DynaValidatorForm) actionForm;
        String url = processFormStr((String) cForm.get("url"));
        String split = processFormStr((String) cForm.get("split"));
        String sheet = processFormStr((String) cForm.get("sheet"));

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
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
            if (split.equals("split") && Utils.isNullStr(sheet)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.insertSheet"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("error");
            }

            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            cs.setTicket(ticket);
            // execute conversion
            if (split.equals("split")) {
                cs.convertDD_XML_split(url, sheet);
            } else {
                cs.convertDD_XML(url);
            }
            // flush the content
            methodResponse.flush();
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error testing conversion", e);
            HttpSession sess = httpServletRequest.getSession(true);
            // GDEMException err= new GDEMException(errMsg);

            sess.setAttribute("gdem.exception", new GDEMException("Error testing conversion: " + e.getMessage()));

            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/" + Names.ERROR_JSP);
        }
        // Do nothing, then response is already sent.
        return actionMapping.findForward(null);
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
}
