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
import java.sql.SQLException;
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

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("resulttypes", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
        model.addAttribute("scriptlangs", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_LANGS));
    }

    @GetMapping
    public String list(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("scripts", QAScriptListLoader.getList(request));
        } catch (DCMException e) {
            throw new RuntimeException("Error getting QA scripts list: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/scripts/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("form") QAScriptForm form, Model model) {
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
            throw new RuntimeException("Error getting history for QA scripts list: " + messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute("history", l);
        model.addAttribute("scriptId", id);
        return "/scripts/history";
    }

    @GetMapping("/{scriptId}")
    public String show(@PathVariable String scriptId, @ModelAttribute("form") QAScriptForm form, Model model, HttpServletRequest request) {

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

            model.addAttribute("scripts", QAScriptListLoader.getList(request));
        } catch (DCMException e) {
            throw new RuntimeException("QA Script form error: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/scripts/view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, @ModelAttribute("form") QAScriptForm form, Model model, HttpServletRequest request) {

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

            model.addAttribute("scripts", QAScriptListLoader.getList(request));

        } catch (DCMException e) {
            throw new RuntimeException("QA Script form error: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/scripts/edit";
    }

    @PostMapping(params = {"update"})
    public String editSubmit(@ModelAttribute("form") QAScriptForm form,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        SpringMessages messages = new SpringMessages();

        String user = (String) request.getSession().getAttribute("user");

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

        new QAScriptValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/scripts/edit";
        }

        try {
            QAScriptManager qm = new QAScriptManager();
            qm.update(user, scriptId, shortName, schemaId, resultType, desc, scriptType, curFileName, upperLimit,
                    url, scriptContent, updateContent);
            qm.activateDeactivate(user, scriptId, active);
            // clear qascript list in cache
            QAScriptListLoader.reloadList(request);
            messages.add(messageService.getMessage("label.qascript.updated"));
        } catch (DCMException e) {
            throw new RuntimeException("Edit QA script error: " + messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
//        redirectAttributes.addFlashAttribute("schema", schema);
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
                    bindingResult.rejectValue("fileName", BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS);
                    return "/scripts/add";
                }
                qam.replaceScriptFromRemoteFile(user, url, fileName);
                form.setFileName(fileName);
            } catch (SQLException e) {
                throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
            } catch (DCMException e) {
                throw new RuntimeException(messageService.getMessage("label.qascript.download.error"));
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
            throw new RuntimeException("Add QA Script error: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        if (schemaId != null) {
            return "redirect:/schemas/" + schemaId + "/scripts";
        }
        return "redirect:/scripts";
    }

    @PostMapping(params = {"delete"})
    public String delete(@ModelAttribute("scriptForm") QAScriptForm scriptForm, BindingResult bindingResult,
                         HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String scriptId = scriptForm.getScriptId();
        String schemaId = scriptForm.getSchemaId();

        new QAScriptValidator().validate(scriptForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/schemas/scripts";
        }

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.delete(user, scriptId);
            messages.add(messageService.getMessage("label.qascript.deleted"));
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error deleting QA script: " + messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

    @PostMapping(params = {"activate"})
    public String activate(@ModelAttribute("scriptForm") QAScriptForm scriptForm, @ModelAttribute("schemaForm") SchemaForm schemaForm,
                           @RequestParam String schemaId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String scriptId = scriptForm.getScriptId();

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.activateDeactivate(user, scriptId, true);
            messages.add(messageService.getMessage("label.qascript.activated"));
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error activating QA script: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

    @PostMapping(params = {"deactivate"})
    public String deactivate(@ModelAttribute("scriptForm") QAScriptForm scriptForm, @ModelAttribute("schemaForm") SchemaForm schemaForm,
                             @RequestParam String schemaId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String scriptId = scriptForm.getScriptId();

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.activateDeactivate(user, scriptId, false);
            messages.add(messageService.getMessage("label.qascript.deactivated"));
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error deactivating QA script: " + messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

    @PostMapping(params = {"toggleSchemaValidation"})
    public String toggleSchemaValidation(@ModelAttribute("scriptForm") QAScriptForm scriptForm, @ModelAttribute("schemaForm") SchemaForm schemaForm,
                                         @RequestParam String schemaId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        String user = (String) httpServletRequest.getSession().getAttribute("user");
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
            throw new RuntimeException("Error updating schema validation: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/scripts";
    }

}
