/*
 * Created on 15.01.2009
 */
package eionet.gdem.web.struts.login;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eionet.gdem.Constants;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.acl.AppUser;

import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS AfterCASLoginAction
 */

public class AfterCASLoginAction extends Action {

    /** */
    public static final String AFTER_LOGIN_ATTR_NAME = "afterLogin";

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {

        // Store user in session
        AppUser aclUser = SecurityUtil.getUser(httpServletRequest, Constants.USER_ATT);

        // remove session data, that contains permission related attributes
        QAScriptListLoader.loadPermissions(httpServletRequest);
        StylesheetListLoader.loadPermissions(httpServletRequest);

        String afterLogin = (String) httpServletRequest.getSession().getAttribute(AFTER_LOGIN_ATTR_NAME);

        if (afterLogin != null && !afterLogin.toLowerCase().contains("/tiles/layout.jsp")) {
            httpServletResponse.sendRedirect(afterLogin);
            return null;
        }

        return actionMapping.findForward("home");

    }
}
