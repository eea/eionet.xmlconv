package eionet.gdem.web.spring.converter;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ConversionServiceIF;
import eionet.gdem.dto.ConversionResultDto;
import eionet.gdem.dto.ConvertedFileDto;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.cdr.UrlUtils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.IRootElemDao;
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

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/converter")
public class Excel2xmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Excel2xmlController.class);
    private MessageService messageService;
    private IRootElemDao rootElemDao;

    @Autowired
    public Excel2xmlController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
    }

    @GetMapping("/excel2xml")
    public String excel2xml(@ModelAttribute("form") Excel2xmlForm form, @ModelAttribute("conversionLog") String conversionLog, Model model) {
        if (form != null) {
            form.setSplit("all");
        }
        model.addAttribute("form", form);
        model.addAttribute("conversionLog", conversionLog);
        model.addAttribute("conversionLinks", model.asMap().get("conversionLinks"));
        return "/converter/excel2xml";
    }

    @PostMapping("/excel2xml")
    public String excel2xmlSubmit(@ModelAttribute Excel2xmlForm form, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();

        String ticket = (String) session.getAttribute(Constants.TICKET_ATT);

        String url = processFormStr(form.getUrl());
        String split = processFormStr(form.getSplit());
        String sheet = processFormStr(form.getSheet());
        Boolean showConversionLog = processFormBoolean(form.isConversionLog());

        // get request parameters
        try {
            // parse request parameters
            if (Utils.isNullStr(url)) {
                errors.add(messageService.getMessage("label.conversion.insertExcelUrl"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter/excel2xml";
            }
            if (Utils.isNullStr(split)) {
                errors.add(messageService.getMessage("label.conversion.insertSplit"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter/excel2xml";
            }
            if (split.equals("split") && Utils.isNullStr(sheet) && !showConversionLog) {
                errors.add(messageService.getMessage("label.conversion.insertSheet"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/converter/excel2xml";
            }
            ConversionServiceIF cs = new ConversionService();
            cs.setTicket(ticket);
            cs.setTrustedMode(true);
            ConversionResultDto conversionResult = null;
            // execute conversion
            if (split.equals("split")) {
                conversionResult = cs.convertDD_XML(url, true, sheet);
            } else {
                conversionResult = cs.convertDD_XML(url, true, null);
            }
            List<String> conversionLinks = new ArrayList<>();
            for (ConvertedFileDto dto : conversionResult.getConvertedFiles()) {
                conversionLinks.add("//" + Properties.appHost + "/" + Properties.contextPath + "/tmp/" + UrlUtils.getFileName(dto.getFilePath()));
            }
            redirectAttributes.addFlashAttribute("conversionLinks", conversionLinks);
            String conversionLog = conversionResult.getConversionLogAsHtml();
            if (!Utils.isNullStr(conversionLog)) {
                form.setConversionLog(true);
                redirectAttributes.addFlashAttribute("conversionLog", conversionLog);
            } else {
                form.setConversionLog(false);
            }


            redirectAttributes.addFlashAttribute("form", form);
        } catch (XMLConvException e) {
            LOGGER.error("Error testing conversion", e);
            errors.add(messageService.getMessage(e.getMessage()));
            return "redirect:/converter/excel2xml";
        }
        return "redirect:/converter/excel2xml";
    }



    /**
     * Process String
     * @param arg Argument
     * @return Result
     */
    private String processFormStr(String arg) {
        String result = null;
        if (arg != null) {
            if (!arg.trim().equalsIgnoreCase("")) {
                result = arg.trim();
            }
        }
        return result;
    }

    /**
     * Process Boolean
     * @param arg Argument
     * @return Result
     */
    private Boolean processFormBoolean(Boolean arg) {
        if (arg == null) {
            arg = false;
        }
        return arg;
    }
}
