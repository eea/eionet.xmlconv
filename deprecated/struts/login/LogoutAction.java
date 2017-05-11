/*
 * Created on 20.01.2009
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

import eionet.gdem.XMLConvException;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS LogoutAction
 */

public class LogoutAction extends Action {

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException, XMLConvException {

        httpServletRequest.setCharacterEncoding("UTF-8");

        AppUser user = SecurityUtil.getUser(httpServletRequest, Constants.USER_ATT);
        QAScriptListLoader.clearPermissions(httpServletRequest);
        httpServletRequest.getSession().invalidate();

        String logoutURL = SecurityUtil.getLogoutURL(httpServletRequest);
        if (logoutURL != null) {
            httpServletResponse.sendRedirect(logoutURL);
            return null;
        }
        return actionMapping.findForward("home");
    }
}
