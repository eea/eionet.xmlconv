package eionet.gdem.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.MessageResourcesConfig;
import org.apache.struts.util.MessageResources;

import eionet.gdem.utils.SecurityUtil;

/**
 * Base action class.
 * @author Unknown
 * @author George Sofianos
 */
public class BaseAction extends Action {

    public static final String KEY_REQDTO = "dto";

    /**
     * Execute
     * @param mapping Mapping
     * @param form Form
     * @param request Request
     * @param response Response
     * @return Action forward
     * @throws Exception If an error occurs.
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return null;
    }

    /**
     * Checks permissions
     * @param request Request
     * @param acl Access list
     * @param permission Permission
     * @return True if user has permission
     * @throws Exception If an error occurs.
     */
    protected boolean checkPermission(HttpServletRequest request, String acl, String permission) throws Exception {
        String username = (String) request.getSession().getAttribute("user");
        boolean result = username != null && SecurityUtil.hasPerm(username, "/" + acl, permission);
        return result;
    }

    /**
     * Process form string
     * @param arg Argument
     * @return Result
     */
    protected String processFormStr(String arg) {
        String result = null;
        if (arg != null) {
            if (!arg.trim().equalsIgnoreCase("")) {
                result = arg.trim();
            }
        }
        return result;
    }

    /**
     * Translate
     * @param map Map
     * @param req Request
     * @param key Key
     * @return Translated message
     */
    protected String translate(ActionMapping map, HttpServletRequest req, String key) {
        MessageResources msgRes = getMessageResources(map);
        return msgRes.getMessage(req.getLocale(), key);
    }

    /**
     * Returns message resources.
     * @param map Map
     * @return Message resources
     */
    protected MessageResources getMessageResources(ActionMapping map) {
        MessageResourcesConfig mrc = map.getModuleConfig().findMessageResourcesConfig(Globals.MESSAGES_KEY);
        MessageResources msgRes = MessageResources.getMessageResources(mrc.getParameter());
        msgRes.setReturnNull(true);
        return msgRes;
    }

}
