/*
 * Created on 20.01.2009
 */
package eionet.gdem.web.struts.login;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.tee.uit.security.AppUser;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.utils.SecurityUtil;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * LogoutAction
 */

public class LogoutAction   extends Action {


    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, GDEMException {

        httpServletRequest.setCharacterEncoding("UTF-8");

        AppUser user = SecurityUtil.getUser(httpServletRequest, Names.USER_ATT);
        httpServletRequest.getSession().invalidate();

        String logoutURL = SecurityUtil.getLogoutURL(httpServletRequest);
        if (logoutURL!=null){
            httpServletResponse.sendRedirect(logoutURL);
            return null;
        }
        return actionMapping.findForward("home");
    }
}

