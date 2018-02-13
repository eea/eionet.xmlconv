package eionet.gdem.web.spring.scripts;

import eionet.gdem.Constants;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.qa.QAScriptManager;
import eionet.gdem.dto.BackupDto;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.listeners.AppServletContextListener;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaForm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/scripts")
public class QAScriptsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptsController.class);

    private MessageService messageService;

    @Autowired
    public QAScriptsController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();

        try {
            model.addAttribute("scripts", QAScriptListLoader.getList(request));
        } catch (DCMException e) {
            LOGGER.error("Error getting QA scripts list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/scripts/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute QAScriptForm form, Model model) {
        model.addAttribute("form", form);
        // todo fix this
        model.addAttribute("resulttypes", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
        model.addAttribute("scriptlangs", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_LANGS));
        return "/scripts/add";
    }

    @GetMapping("/{id}/history")
    public String history(@PathVariable String id, Model model) {

        SpringMessages errors = new SpringMessages();
        List<BackupDto> l = null;
        try {
            BackupManager bm = new BackupManager();
            l = bm.getBackups(id);
        } catch (DCMException e) {
            LOGGER.error("Error getting history for QA scripts list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        model.addAttribute("history", l);
        model.addAttribute("scriptId", id);
        return "/scripts/history";
    }

    @GetMapping("/{scriptId}")
    public String show(@PathVariable String scriptId, Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();

        QAScriptForm form = new QAScriptForm();

        /*String scriptId = httpServletRequest.getParameter("scriptId");

        if (scriptId == null || scriptId.equals("")) {
            scriptId = (String) httpServletRequest.getAttribute("scriptId");
        } else {
            httpServletRequest.getSession().setAttribute("scriptId", scriptId);
        }
        if (scriptId == null || scriptId.equals("")) {
            scriptId = (String) httpServletRequest.getSession().getAttribute("scriptId");
        }*/


        try {
            QAScriptManager qm = new QAScriptManager();
            QAScript qaScript = qm.getQAScript(scriptId);
            form.setScriptId(qaScript.getScriptId());
            form.setDescription(qaScript.getDescription());
            form.setShortName(qaScript.getShortName());
            form.setFileName(qaScript.getFileName());
            form.setFilePath(qaScript.getFilePath());
            form.setSchema(qaScript.getSchema());
            form.setSchemaId(qaScript.getSchemaId());
            form.setResultType(qaScript.getResultType());
            form.setScriptType(qaScript.getScriptType());
            form.setModified(qaScript.getModified());
            form.setChecksum(qaScript.getChecksum());
            form.setScriptContent(qaScript.getScriptContent());
            form.setUpperLimit(qaScript.getUpperLimit());
            form.setUrl(qaScript.getUrl());
            form.setActive(qaScript.isActive());

            model.addAttribute("QAScriptForm", form);
            model.addAttribute("scripts", QAScriptListLoader.getList(request));

        } catch (DCMException e) {
            LOGGER.error("QA Script form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "/scripts/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/scripts/view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();

        QAScriptForm form = new QAScriptForm();
        try {
            QAScriptManager qm = new QAScriptManager();
            QAScript qaScript = qm.getQAScript(id);
            form.setScriptId(qaScript.getScriptId());
            form.setDescription(qaScript.getDescription());
            form.setShortName(qaScript.getShortName());
            form.setFileName(qaScript.getFileName());
            form.setFilePath(qaScript.getFilePath());
            form.setSchema(qaScript.getSchema());
            form.setSchemaId(qaScript.getSchemaId());
            form.setResultType(qaScript.getResultType());
            form.setScriptType(qaScript.getScriptType());
            form.setModified(qaScript.getModified());
            form.setChecksum(qaScript.getChecksum());
            form.setScriptContent(qaScript.getScriptContent());
            form.setUpperLimit(qaScript.getUpperLimit());
            form.setUrl(qaScript.getUrl());
            form.setActive(qaScript.isActive());

            model.addAttribute("QAScriptForm", form);
            model.addAttribute("scripts", QAScriptListLoader.getList(request));

        } catch (DCMException e) {
            LOGGER.error("QA Script form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "/scripts/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/scripts/edit";
    }

    @PostMapping(params = {"update"})
    public String editSubmit(@ModelAttribute QAScriptForm form, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String scriptId = form.getScriptId();
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String curFileName = form.getFileName();
        String scriptContent = form.getScriptContent();
        String upperLimit = form.getUpperLimit();
        String url = form.getUrl();
        String checksum = form.getChecksum();
        boolean active = form.getActive();

        boolean updateContent = false;
        String newChecksum = null;

        String user = (String) session.getAttribute("user");

        /*httpServletRequest.setAttribute("scriptId", scriptId);*/

        /*if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", scriptId);
        }*/

        if (!Utils.isNullStr(curFileName) && !Utils.isNullStr(scriptContent)
                && scriptContent.indexOf(Constants.FILEREAD_EXCEPTION) == -1) {

            // compare checksums
            try {
                newChecksum = Utils.getChecksumFromString(scriptContent);
            } catch (Exception e) {
                LOGGER.error("unable to create checksum");
            }
            if (checksum == null) {
                checksum = "";
            }
            if (newChecksum == null) {
                newChecksum = "";
            }

            updateContent = !checksum.equals(newChecksum);
        }

        // Zip result type can only be selected for FME scripts
        if (!XQScript.SCRIPT_LANG_FME.equals(scriptType) && XQScript.SCRIPT_RESULTTYPE_ZIP.equals(resultType)) {
            errors.add(messageService.getMessage("label.qascript.zip.validation"));
        }

        // upper limit between 0 and 10Gb
        if (upperLimit == null || !Utils.isNum(upperLimit) || Integer.parseInt(upperLimit) <= 0
                || Integer.parseInt(upperLimit) > 10000) {
            errors.add(messageService.getMessage("label.qascript.upperlimit.validation"));
        }

        if (errors.isEmpty()) {
            try {
                QAScriptManager qm = new QAScriptManager();
                qm.update(user, scriptId, shortName, schemaId, resultType, desc, scriptType, curFileName, upperLimit,
                        url, scriptContent, updateContent);
                messages.add(messageService.getMessage("label.qascript.updated"));
                qm.activateDeactivate(user, scriptId, active);
                // clear qascript list in cache
                QAScriptListLoader.reloadList(request);
            } catch (DCMException e) {
                LOGGER.error("Edit QA script error", e);
                errors.add(messageService.getMessage(e.getErrorCode()));
            }
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/scripts/{scriptId}/edit";
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        redirectAttributes.addFlashAttribute("schema", schema);
        return "redirect:/scripts/{scriptId}";
    }

    @PostMapping(params = {"add"})
    public String addSubmit(@ModelAttribute("form") @Valid QAScriptForm form, HttpSession session,
                            Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                            HttpServletRequest httpServletRequest) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String url = form.getUrl();
        String upperLimit = form.getUpperLimit();
        MultipartFile scriptFile = form.getScriptFile();

        // if URL is filled download from the remote source
        if (!Utils.isNullStr(url)) {
            QAScriptManager qam = new QAScriptManager();
            String fileName = StringUtils.substringAfterLast(url, "/");
            try {
                if (qam.fileExists(fileName)) {
                    errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS));
                    model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                    return "/scripts/add";
                }
                qam.replaceScriptFromRemoteFile(user, url, fileName);
                form.setFileName(fileName);
            } catch (Exception e) {
                errors.add(messageService.getMessage("label.qascript.download.error"));
                model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "/scripts/add";
            }
        }

        new QAScriptValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/scripts/add";
        }

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.add(user, shortName, schemaId, schema, resultType, desc, scriptType, scriptFile, upperLimit, url);
            messages.add(messageService.getMessage("label.qascript.inserted"));
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Add QA Script error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "scripts/add";
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        if (schemaId != null) {
            return "redirect:/schemas/" + schemaId + "/scripts";
        }
        return "redirect:/scripts";
    }

    @PostMapping(params = {"delete"})
    public String delete(@ModelAttribute QAScriptForm scriptForm, @ModelAttribute SchemaForm schemaForm, @RequestParam String action, @RequestParam String schemaId, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        String scriptId = scriptForm.getScriptId();

            /*if (scriptId == null || scriptId.length() == 0) {
                scriptId = form.getScriptId();
            }*/
            /*String schemaId = form.getSchemaId();
            if (schemaId == null || schemaId.length() == 0) {
                schemaId = httpServletRequest.getParameter("schemaId");
            }*/

        /*httpServletRequest.setAttribute("schemaId", httpServletRequest.getParameter("schemaId"));*/

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.delete(user, scriptId);
            messages.add(messageService.getMessage("label.qascript.deleted"));
            // clear qascript list in cache
            /*QAScriptListLoader.reloadList(httpServletRequest);*/
        } catch (DCMException e) {
            LOGGER.error("Error deleting QA script", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

    @PostMapping(params = {"activate"})
    public String activate(@ModelAttribute QAScriptForm scriptForm, @ModelAttribute SchemaForm schemaForm, @RequestParam String action, @RequestParam String schemaId, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        String scriptId = scriptForm.getScriptId();

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.activateDeactivate(user, scriptId, true);
            messages.add(messageService.getMessage("label.qascript.activated"));
            // clear qascript list in cache
            /*QAScriptListLoader.reloadList(httpServletRequest);*/
        } catch (DCMException e) {
            LOGGER.error("Error activating QA script", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

    @PostMapping(params = {"deactivate"})
    public String deactivate(@ModelAttribute QAScriptForm scriptForm, @ModelAttribute SchemaForm schemaForm, @RequestParam String action, @RequestParam String schemaId, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        String scriptId = scriptForm.getScriptId();

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.activateDeactivate(user, scriptId, false);
            messages.add(messageService.getMessage("label.qascript.deactivated"));
            // clear qascript list in cache
            /*QAScriptListLoader.reloadList(httpServletRequest);*/
        } catch (DCMException e) {
            LOGGER.error("Error deactivating QA script", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

    @PostMapping(params = {"toggleSchemaValidation"})
    public String toggleSchemaValidation(@ModelAttribute QAScriptForm scriptForm, @ModelAttribute SchemaForm schemaForm, @RequestParam String action, @RequestParam String schemaId, HttpSession session, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");
        String scriptId = scriptForm.getScriptId();

        /*String schemaId = schemaForm.getSchemaId();*/
        boolean validate = schemaForm.isDoValidation();
        boolean blocker = schemaForm.isBlocker();

        /*httpServletRequest.setAttribute("schemaId", schemaId);*/

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.updateSchemaValidation(user, schemaId, validate, blocker);
            messages.add(messageService.getMessage("label.qascript.validation.updated"));
        } catch (DCMException e) {
            LOGGER.error("Error updating schema validation", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

}
