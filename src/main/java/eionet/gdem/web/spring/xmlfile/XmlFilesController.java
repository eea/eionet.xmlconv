package eionet.gdem.web.spring.xmlfile;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.FileUploadWrapper;
import eionet.gdem.web.spring.SpringMessages;
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

/**
 *
 */
@Controller
@RequestMapping("/xmlFiles")
public class XmlFilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFilesController.class);
    private MessageService messageService;
    private UplXmlFileManager uplXmlFileManager;

    @Autowired
    public XmlFilesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        SpringMessages errors = new SpringMessages();
        UplXmlFileHolder holder = null;

        String user = (String) session.getAttribute("user");

        try {
            uplXmlFileManager = new UplXmlFileManager();
            holder = uplXmlFileManager.getUplXmlFiles(user);
        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Uploaded XML file form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        model.addAttribute("xmlfiles", holder);
        return "/uplXmlFile.jsp";
    }

    @GetMapping("/add")
    public String add(Model model) {
        UplXmlFileForm form = new UplXmlFileForm();
        model.addAttribute("form", form);
        return "/addUplXmlFile.jsp";
    }

    @PostMapping("/add")
    public String addSubmit(@ModelAttribute UplXmlFileForm updatedForm, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        FileUploadWrapper xmlfile = updatedForm.getXmlfile();
        String title = updatedForm.getTitle();

        String user = (String) session.getAttribute("user");

        /*if (isCancelled(httpServletRequest)) {
            return actionMapping.findForward("success");
        }*/

        if (xmlfile == null || xmlfile.getFile() == null || xmlfile.getFile().getSize() == 0) {
            errors.add(messageService.getMessage("label.uplXmlFile.validation"));
            redirectAttributes.addFlashAttribute("dcm.errors", errors);
            return "redirect:/old/xmlFiles/add";
        }

        /*
         * IXmlCtx x = new XmlContext(); try { x.setWellFormednessChecking(); x.checkFromInputStream(new
         * ByteArrayInputStream(xmlfile.getFileData())); } catch (Exception e) { errors.add(ActionMessages.GLOBAL_MESSAGE, new
         * ActionMessage("label.uplXmlFile.error.notvalid")); httpServletRequest.getSession().setAttribute("dcm.errors", errors);
         * return actionMapping.findForward("fail"); }
         */

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            fm.addUplXmlFile(user, xmlfile, title);
            messages.add(messageService.getMessage("label.uplXmlFile.inserted"));
        } catch (DCMException e) {
            LOGGER.error("Error adding upload XML file", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute("dcm.errors", errors);
        redirectAttributes.addFlashAttribute("dcm.messages", messages);

        return "redirect:/old/xmlFiles";
    }
}
