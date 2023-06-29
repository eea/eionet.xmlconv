package eionet.gdem.web.spring.validation;

import eionet.gdem.XMLConvException;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.services.MessageService;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String form(@ModelAttribute("form") ValidationForm form) {
        return "/validation";
    }

    @PostMapping
    public String submit(@ModelAttribute("form") ValidationForm form, BindingResult bindingResult, Model model) {

        String url = form.getXmlUrl();
        String schema = form.getSchemaUrl();

        new ValidationFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/validation";
        }

        try {
            ValidationService v = new JaxpValidationService();
            if (schema == null) {
                v.validate(url);
            } else {
                v.validateSchema(url, schema);
            }
            List<ValidateDto> validationErrors = v.getErrorList();
            String validatedSchema = String.join(", ", v.getValidatedSchemasURL());
            String originalSchema = String.join(", ", v.getOriginalSchemas());
            String warningMessage = v.getWarningMessage();
            model.addAttribute("validationErrors", validationErrors);
            model.addAttribute("originalSchema", originalSchema);
            if (!StringUtils.equals(originalSchema, validatedSchema)) {
                model.addAttribute("validatedSchema", validatedSchema);
            }
            model.addAttribute("warningMessage", warningMessage);
        } catch (XMLConvException e) {
            throw new RuntimeException("Error validating xml: " + ExceptionUtils.getRootCauseMessage(e));
        }
        return "/validation";
    }

}