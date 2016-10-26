package eionet.gdem.web.struts.source;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eionet.gdem.http.HttpFileManager;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get Source action class.
 * @author Unknown
 * @author George Sofianos
 */
@Deprecated
public class GetSourceAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSourceAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        String ticket = httpServletRequest.getParameter(Constants.TICKET_PARAM);
        String url = httpServletRequest.getParameter(Constants.SOURCE_URL_PARAM);
        HttpFileManager manager = null;
        try {
            manager = new HttpFileManager();
            manager.getHttpResponse(httpServletResponse, ticket, url);
        } catch (Exception e) {
            LOGGER.error("Error: " + e);
        } finally {
            manager.closeQuietly();
        }
        return null;
    }
}
