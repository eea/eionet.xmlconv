package eionet.gdem.web.spring.scripts;

import eionet.gdem.Constants;
import eionet.gdem.dcm.business.BackupManager;
import eionet.gdem.dcm.business.QAScriptManager;
import eionet.gdem.dto.BackupDto;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
            e.printStackTrace();
            LOGGER.error("Error getting QA scripts list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/scripts/list";
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
            model.addAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(request));

        } catch (DCMException e) {
            e.printStackTrace();
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
            model.addAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(request));

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("QA Script form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "/scripts/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/scripts/edit";
    }

    @PostMapping
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
            return "redirect:/old/scripts/{scriptId}/edit";
        }

        redirectAttributes.addFlashAttribute("dcm.messages", messages);
        redirectAttributes.addAttribute("schema", schema);
        return "redirect:/old/scripts/{scriptId}";
    }

    @GetMapping("/{id}/history")
    public String history(@PathVariable String id, Model model) {

        SpringMessages errors = new SpringMessages();

        List<BackupDto> l = null;

        /*String scriptId = httpServletRequest.getParameter(Constants.XQ_SCRIPT_ID_PARAM);*/


        try {
            BackupManager bm = new BackupManager();
            l = bm.getBackups(id);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error getting history for QA scripts list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        model.addAttribute("qascript.history", l);
        model.addAttribute("script_id", id);
        return "/scripts/history";
    }

}
