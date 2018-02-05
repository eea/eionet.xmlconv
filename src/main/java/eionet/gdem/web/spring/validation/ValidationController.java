package eionet.gdem.web.spring.validation;

import eionet.gdem.Constants;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import eionet.gdem.web.spring.SpringMessages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/validation")
public class ValidationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationController.class);
    private MessageService messageService;

    @Autowired
    public ValidationController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String form(@ModelAttribute(name = "form") ValidationForm form, Model model) {

        SpringMessages errors = new SpringMessages();
        model.addAttribute("form", form);
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/validation";
    }

    @PostMapping
    public String formSubmit(@ModelAttribute(name = "form") ValidationForm form, RedirectAttributes redirectAttributes, HttpSession session) {

        String ticket = (String) session.getAttribute(Constants.TICKET_ATT);
        SpringMessages errors = new SpringMessages();

        String url = form.getXmlUrl();
        String schema = form.getSchemaUrl();

        redirectAttributes.addFlashAttribute("form", form);

        if (Utils.isNullStr(url)) {
            errors.add(messageService.getMessage("label.conversion.selectSource"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/validation";
        }
        if (!Utils.isURL(url)) {
            errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/validation";
        }

        try {
            List<ValidateDto> validationErrors;
            String validatedSchema = null;
            String originalSchema = null;
            String warningMessage = null;

            ValidationService v = new JaxpValidationService();
            //v.setTrustedMode(false);
            //v.setTicket(ticket);
            if (schema == null) {
                v.validate(url);
            } else {
                v.validateSchema(url, schema);
            }
            validationErrors = v.getErrorList();
            validatedSchema = v.getValidatedSchemaURL();
            originalSchema = v.getOriginalSchema();
            warningMessage = v.getWarningMessage();
            redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
            redirectAttributes.addFlashAttribute("originalSchema", originalSchema);
            if (!StringUtils.equals(originalSchema, validatedSchema)) {
                redirectAttributes.addFlashAttribute("validatedSchema", validatedSchema);
            }
            redirectAttributes.addFlashAttribute("warningMessage", warningMessage);
        } catch (DCMException e) {
            LOGGER.error("Error validating xml", e);
            errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_VALIDATION_ERROR));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/validation";
        }
        return "redirect:/validation";
    }

}