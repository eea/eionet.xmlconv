package eionet.gdem.web.struts.config;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.apache.struts.validator.DynaValidatorForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BaseX Server configuration
 * @author George Sofianos
 *
 */
public class BaseXAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(BaseXAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        DynaValidatorForm form = (DynaValidatorForm) actionForm;

        String host = (String) form.get("host");
        String port = (String) form.get("port");
        String dbUser = (String) form.get("user");
        String dbPwd = (String) form.get("password");
        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {

            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("success");
            }

            if (host == null || host.equals("")) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.config.ldap.url.validation"));
                httpServletRequest.getSession().setAttribute("dcm.errors", errors);
                return actionMapping.findForward("success");
            }
            DcmProperties dcmProp = new DcmProperties();

            dcmProp.setBasexParams(host, port, dbUser, dbPwd);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("BaseXAction error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("success");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("BaseXAction error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
            saveErrors(httpServletRequest, errors);
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("success");
        }
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.basexserver.saved"));

        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        return actionMapping.findForward("success");
    }
}
