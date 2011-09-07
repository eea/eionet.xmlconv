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

public class BaseAction extends Action {

    public static final String KEY_REQDTO = "dto";

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return null;
    }

    protected boolean checkPermission(HttpServletRequest request, String acl, String permission) throws Exception {
        String username = (String) request.getSession().getAttribute("user");
        boolean result = username != null && SecurityUtil.hasPerm(username, "/" + acl, permission);
        return result;
    }

    protected String processFormStr(String arg) {
        String result = null;
        if (arg != null) {
            if (!arg.trim().equalsIgnoreCase("")) {
                result = arg.trim();
            }
        }
        return result;
    }

    protected String translate(ActionMapping map, HttpServletRequest req, String key) {
        MessageResources msgRes = getMessageResources(map);
        return msgRes.getMessage(req.getLocale(), key);
    }

    protected MessageResources getMessageResources(ActionMapping map) {
        MessageResourcesConfig mrc = map.getModuleConfig().findMessageResourcesConfig(Globals.MESSAGES_KEY);
        MessageResources msgRes = MessageResources.getMessageResources(mrc.getParameter());
        msgRes.setReturnNull(true);
        return msgRes;
    }

}
