/*
 * Created on 16.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eionet.gdem.Constants;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.SaxValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ValidateXMLAction
 */

public class ValidateXMLAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateXMLAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        String ticket = (String) httpServletRequest.getSession().getAttribute(Constants.TICKET_ATT);
        ActionErrors errors = new ActionErrors();

        ConversionForm cForm = (ConversionForm) actionForm;
        String url = cForm.getUrl();
        String schema = cForm.getSchemaUrl();

        if (Utils.isNullStr(url)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.conversion.selectSource"));
            // httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        if (!Utils.isURL(url)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED));
            // httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }

        try {
            List<ValidateDto> valid;
            String validatedSchema = null;
            String originalSchema = null;
            String warningMessage = null;
            try {
                ValidationService v = new JaxpValidationService();
                //v.setTrustedMode(false);
                //v.setTicket(ticket);
                if (schema == null) {
                    v.validate(url);
                } else {
                    v.validateSchema(url, schema);
                }
                valid = v.getErrorList();
                /*validatedSchema = v.getValidatedSchemaURL();
                originalSchema = v.getOriginalSchema();
                warningMessage = v.getWarningMessage();*/
                validatedSchema = "";
                originalSchema = "";
                warningMessage = "";

            } catch (DCMException dcme) {
                throw dcme;
            } catch (Exception e) {
                throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
            }
            httpServletRequest.setAttribute("conversion.valid", valid);
            httpServletRequest.setAttribute("conversion.originalSchema", originalSchema);
            if (!originalSchema.equals(validatedSchema)) {
                httpServletRequest.setAttribute("conversion.validatedSchema", validatedSchema);
            }
            httpServletRequest.setAttribute("conversion.warningMessage", warningMessage);
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error validating xml", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error validating xml", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }
        return actionMapping.findForward("success");
    }

}
