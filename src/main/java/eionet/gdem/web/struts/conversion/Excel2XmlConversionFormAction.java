/*
 * Created on 26.02.2008
 */
package eionet.gdem.web.struts.conversion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS Excel2XmlConversionFormAction
 */

public class Excel2XmlConversionFormAction extends Action {

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        DynaValidatorForm cForm = (DynaValidatorForm) actionForm;
        cForm.set("split", "all");
        return actionMapping.findForward("success");
    }
}
