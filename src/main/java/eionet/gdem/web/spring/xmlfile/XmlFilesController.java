package eionet.gdem.web.spring.xmlfile;

import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.FileUploadWrapper;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.generic.SingleForm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
        model.addAttribute("form", new SingleForm());
        return "/xmlfiles/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        XmlFileForm form = new XmlFileForm();
        model.addAttribute("form", form);
        return "/xmlfiles/add";
    }

    @PostMapping(params = {"add"})
    public String addSubmit(@ModelAttribute XmlFileForm updatedForm, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        MultipartFile xmlfile = updatedForm.getXmlFile();
        String title = updatedForm.getTitle();

        String user = (String) session.getAttribute("user");

        /*if (isCancelled(httpServletRequest)) {
            return actionMapping.findForward("success");
        }*/

        if (xmlfile == null || xmlfile.getSize() == 0) {
            errors.add(messageService.getMessage("label.uplXmlFile.validation"));
            redirectAttributes.addFlashAttribute("dcm.errors", errors);
            return "redirect:/xmlFiles/add";
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
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);

        return "redirect:/xmlFiles";
    }

    @GetMapping("/{fileId}/edit")
    public String edit(@PathVariable String fileId, Model model) {
        SpringMessages errors = new SpringMessages();

        UplXmlFileManager fm = new UplXmlFileManager();
        UplXmlFile file = null;
        try {
            file = fm.getUplXmlFileById(fileId);
        } catch (DCMException e) {
            LOGGER.error("File id not found: ", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/xmlFiles";
        }
        // TODO fix this
        XmlFileForm form = new XmlFileForm(file.getFileName(), "", file.getId(), file.getTitle(), file.getLastModified(), null);
        model.addAttribute("form", form);
        return "/xmlfiles/edit";
    }

    @PostMapping(params = {"update"})
    public String editSubmit(@ModelAttribute("form") XmlFileForm updatedForm, @RequestParam(required = false) MultipartFile xmlFile, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        MultipartFile xmlfile = updatedForm.getXmlFile();
        String title = updatedForm.getTitle();
        String xmlFileId = updatedForm.getXmlfileId();
        String fileName = updatedForm.getXmlFileName();

        String user = (String) session.getAttribute("user");

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            fm.updateUplXmlFile(user, xmlFileId, title, fileName, xmlfile);
            messages.add(messageService.getMessage("label.uplXmlFile.updated"));
        } catch (DCMException e) {
            LOGGER.error("Error updating XML file", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);

        return "redirect:/xmlFiles";
    }

    @PostMapping(params = {"delete"})
    public String deleteSumbit(@ModelAttribute("form") SingleForm singleForm, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String xmlfileId = singleForm.getId();
        if (StringUtils.isEmpty(xmlfileId)) {
            errors.add(messageService.getMessage("label.uplXmlFile.error.notSelected"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/xmlFiles";
        }
        String user_name = (String) httpSession.getAttribute("user");

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            fm.deleteUplXmlFile(user_name, xmlfileId);
            messages.add(messageService.getMessage("label.uplXmlFile.deleted"));
        } catch (DCMException e) {
            LOGGER.error("Error deleting XML file", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);

        return "redirect:/xmlFiles";
    }
}
