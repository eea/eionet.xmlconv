/*
 * Created on 25.02.2008
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

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS TestConvAction
 */

public class TestConvAction extends Action {

    private static LoggerIF _logger = GDEMServices.getLogger();

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {

        ActionErrors errors = new ActionErrors();

        String ticket = (String) httpServletRequest.getSession().getAttribute(Names.TICKET_ATT);

        ConversionForm cForm = (ConversionForm) actionForm;

        String url = cForm.getUrl();
        String convert_id = cForm.getConversionId();
        String errorForward = cForm.getErrorForward();

        httpServletRequest.getSession().setAttribute("converted.url", url);
        httpServletRequest.getSession().setAttribute("converted.conversionId", convert_id);

        // create custom HttpServletResponseWrapper
        HttpMethodResponseWrapper methodResponse = new HttpMethodResponseWrapper(httpServletResponse);
        // get request parameters
        try {
            // parse request parameters
            if (Utils.isNullStr(convert_id)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.noconversionselected"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward(errorForward);
            }
            if (Utils.isNullStr(url)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.selectSource"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward(errorForward);
            }
            if (!Utils.isURL(url)) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.url.malformed"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward(errorForward);
            }

            // call ConversionService
            ConversionServiceIF cs = new ConversionService();
            // set up the servlet outputstream form converter
            cs.setHttpResponse(methodResponse);
            cs.setTicket(ticket);
            // execute conversion
            cs.convert(url, convert_id);
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error testing conversion", e);
            HttpSession sess = httpServletRequest.getSession(true);
            // GDEMException err= new GDEMException(errMsg);

            if (e instanceof GDEMException) {
                sess.setAttribute("gdem.exception", e);
            } else {
                sess.setAttribute("gdem.exception", new GDEMException("Error testing conversion."));
            }

            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/" + Names.ERROR_JSP);
        }
        finally{
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
        }
        // Do nothing, then response is already sent.
        return null;
    }
}
