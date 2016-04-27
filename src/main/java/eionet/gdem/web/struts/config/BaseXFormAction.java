package eionet.gdem.web.struts.config;

import eionet.gdem.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.apache.struts.validator.DynaValidatorForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configuration of BaseX Server connection parameters
 * @author George Sofianos
 *
 */
public class BaseXFormAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(BaseXFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) {
        ActionErrors errors = new ActionErrors();
        try {
            DynaValidatorForm form = (DynaValidatorForm) actionForm;
            form.set("host", Properties.basexServerHost);
            form.set("port", Properties.basexServerPort);
            form.set("user", Properties.basexServerUser);
            form.set("password", Properties.basexServerPassword);
        } catch (Exception e) {
            LOGGER.error("Error setting BaseX form", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
            saveMessages(httpServletRequest, errors);
        }
        saveMessages(httpServletRequest, errors);

        return actionMapping.findForward("success");
    }

}
