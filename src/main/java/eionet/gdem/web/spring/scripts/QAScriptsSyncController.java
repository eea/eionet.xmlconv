package eionet.gdem.web.spring.scripts;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.QAScriptManager;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.listeners.AppServletContextListener;
import eionet.gdem.web.spring.SpringMessage;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 *
 *
 */
@Controller
@RequestMapping("/scripts")
public class QAScriptsSyncController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptsSyncController.class);

    private MessageService messageService;

    @Autowired
    public QAScriptsSyncController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("resulttypes", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
        model.addAttribute("scriptlangs", AppServletContextListener.loadConvTypes(XQScript.SCRIPT_LANGS));
    }

    @PostMapping(params = {"diff"})
    public String diffUpload(@ModelAttribute("form") QAScriptForm form, BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest) {

        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        //String uplSchemaId = form.getUplSchemaId();
        String scriptFile = form.getFileName();
        String scriptUrl = form.getUrl();
        String scriptId = form.getScriptId();

        QAScriptSyncForm syncForm = new QAScriptSyncForm();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if (bindingResult.hasErrors()) {
            return "/scripts/edit";
        }

        try {
            byte[] remoteScript = Utils.downloadRemoteFile(scriptUrl);

            String result = Utils.diffRemoteFile(remoteScript, Properties.queriesFolder + File.separator + scriptFile);

            if (BusinessConstants.WARNING_FILES_IDENTICAL.equals(result) || result.equals("")) {
                messages.add(messageService.getMessage(result));
                model.addAttribute(SpringMessages.WARNING_MESSAGES, messages);
                return "/scripts/edit";
            } else {
                syncForm.setScriptId(scriptId);
                syncForm.setUrl(scriptUrl);
                syncForm.setFileName(scriptFile);

                try {
                    syncForm.setScriptFile(new String(remoteScript, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    syncForm.setScriptFile(new String(remoteScript));
                }
                model.addAttribute("form", syncForm);
            }

        } catch (DCMException e) {
            throw new RuntimeException("Unable to diff schemas: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/scripts/sync";
    }

    @PostMapping("/sync")
    public String sync(@ModelAttribute("form") QAScriptSyncForm form, BindingResult bindingResult,
                       @RequestParam String action, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();
        String scriptId = form.getScriptId();
        String scriptFileName = form.getFileName();
        String url = form.getUrl();

        if ("cancel".equals(action)) {
            return "redirect:/scripts/" + scriptId + "/edit";
        }

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            QAScriptManager qm = new QAScriptManager();

            qm.replaceScriptFromRemoteFile(user, url, scriptFileName);

            BackupManager bum = new BackupManager();
            bum.backupFile(Properties.queriesFolder, scriptFileName, scriptId, user);
            messages.add(messageService.getMessage("label.uplScript.cached"));

        } catch (DCMException e) {
            throw new RuntimeException("Unable to sync local script: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/scripts/" + scriptId;
    }

}
