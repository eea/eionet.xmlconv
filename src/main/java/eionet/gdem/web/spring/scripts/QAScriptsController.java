package eionet.gdem.web.spring.scripts;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.data.scripts.HeavyScriptReasonEnum;
import eionet.gdem.dto.BackupDto;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.service.QueryHistoryService;
import eionet.gdem.jpa.service.QueryJpaService;
import eionet.gdem.jpa.service.QueryMetadataService;
import eionet.gdem.paging.Paged;
import eionet.gdem.qa.QAScriptManager;
import eionet.gdem.qa.XQScript;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.ThymeleafUtils;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/scripts")
public class QAScriptsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptsController.class);

    private MessageService messageService;
    private QueryHistoryService queryHistoryService;
    private QueryJpaService queryJpaService;
    private QueryMetadataService queryMetadataService;

    @Autowired
    public QAScriptsController(MessageService messageService, QueryHistoryService queryHistoryService, QueryJpaService queryJpaService, QueryMetadataService queryMetadataService) {
        this.messageService = messageService;
        this.queryHistoryService = queryHistoryService;
        this.queryJpaService = queryJpaService;
        this.queryMetadataService = queryMetadataService;
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
        Boolean asynchronousExecution = form.isAsynchronousExecution();
        Boolean markedHeavy = form.isMarkedHeavy();
        String markedHeavyReason = form.getMarkedHeavyReason();
        String markedHeavyReasonOther = form.getMarkedHeavyReasonOther();
        MultipartFile scriptFile = form.getScriptFile();
        boolean active = form.getActive();
        String ruleMatch = form.getRuleMatch();

/*        // if URL is filled download from the remote source
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
        }*/
        new QAScriptValidator().validateAdd(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/scripts/add";
        }

        try {
            QAScriptManager qaScriptManager = new QAScriptManager();
            qaScriptManager.add(user, shortName, schemaId, schema, resultType, desc, scriptType, scriptFile, upperLimit, url, asynchronousExecution,
                    active, markedHeavy, markedHeavyReason, markedHeavyReasonOther, ruleMatch);
            messages.add(messageService.getMessage("label.qascript.inserted"));
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Add QA Script error: " + messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        if (!StringUtils.isEmpty(schemaId)) {
            return "redirect:/schemas/" + schemaId + "/scripts";
        }
        return "redirect:/scripts";
    }


    @GetMapping("/{id}/backupHistory")
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
            form.setAsynchronousExecution(qaScript.isAsynchronousExecution());
            form.setMarkedHeavy(qaScript.getMarkedHeavy());
            form.setRuleMatch(qaScript.getRuleMatch());

            if(qaScript.getMarkedHeavy()){
                if (qaScript.getMarkedHeavyReason() != HeavyScriptReasonEnum.OTHER.getCode()){
                    form.setMarkedHeavyReasonOther(ScriptUtils.getHeavyScriptReasonTextByCode(qaScript.getMarkedHeavyReason()));
                }
                else{
                    form.setMarkedHeavyReasonOther(qaScript.getMarkedHeavyReasonOther());
                }
            }

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
            form.setAsynchronousExecution(qaScript.isAsynchronousExecution());
            form.setMarkedHeavy(qaScript.getMarkedHeavy());
            if(qaScript.getMarkedHeavy()){
                String markedHeavyReasonText = ScriptUtils.getHeavyScriptReasonTextByCode(qaScript.getMarkedHeavyReason());
                if(markedHeavyReasonText != null) {
                    form.setMarkedHeavyReason(markedHeavyReasonText);
                    if(qaScript.getMarkedHeavyReason() == HeavyScriptReasonEnum.OTHER.getCode()){
                        form.setMarkedHeavyReasonOther(qaScript.getMarkedHeavyReasonOther());
                    }
                }
            }
            form.setRuleMatch(qaScript.getRuleMatch());

            model.addAttribute("scripts", QAScriptListLoader.getList(request));
        } catch (DCMException e) {
            throw new RuntimeException("QA Script form error: " + messageService.getMessage(e.getErrorCode()));
        }
        return "/scripts/edit";
    }

    @PostMapping(params = {"upload"})
    public String upload(@ModelAttribute("form") QAScriptForm form,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        SpringMessages messages = new SpringMessages();

        String scriptId = form.getScriptId();
        String schemaId = form.getSchemaId();
        String shortName = form.getShortName();
        String desc = form.getDescription();
        String schema = form.getSchema();
        String resultType = form.getResultType();
        String scriptType = form.getScriptType();
        String curFileName = form.getFileName();
        MultipartFile content = form.getScriptFile();
        String upperLimit = form.getUpperLimit();
        String url = form.getUrl();
        Boolean asynchronousExecution = form.isAsynchronousExecution();
        boolean active = form.getActive();
        Boolean markedHeavy = form.isMarkedHeavy();
        String markedHeavyReason = form.getMarkedHeavyReason();
        String markedHeavyReasonOther = form.getMarkedHeavyReasonOther();
        String ruleMatch = form.getRuleMatch();

        String user = (String) request.getSession().getAttribute("user");

        LOGGER.info("User " + user + " uploading script file " + curFileName + " for script id " + scriptId);

        new QAScriptValidator().validateUpdate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/scripts/edit";
        }

        Integer markedHeavyReasonStatus = null;
        if(markedHeavy){
            //Get Marked heavy reason code
            markedHeavyReasonStatus = ScriptUtils.getHeavyScriptReasonCodeByText(markedHeavyReason);
        }
        if(markedHeavyReasonStatus != HeavyScriptReasonEnum.OTHER.getCode()){
            markedHeavyReasonOther = null;
        }

        try {
            Integer maxVersion = queryJpaService.findMaxVersion(Integer.parseInt(scriptId));
            QAScriptManager qm = new QAScriptManager();
            QueryBackupEntry queryBackupEntry = qm.update(user, scriptId, shortName, schemaId, resultType, desc, scriptType, curFileName,
                    content, upperLimit, url, asynchronousExecution, active, maxVersion+1, markedHeavy, markedHeavyReasonStatus, markedHeavyReasonOther, ruleMatch);

            QueryEntry queryEntry = new QueryEntry(Integer.parseInt(scriptId));
            QueryHistoryEntry queryHistoryEntry = ScriptUtils.createQueryHistoryEntry(user, shortName, schemaId, resultType, desc, scriptType, upperLimit, url,
                    asynchronousExecution, active, curFileName, maxVersion+1, markedHeavy, markedHeavyReasonStatus, markedHeavyReasonOther, ruleMatch);
            queryHistoryEntry.setQueryEntry(queryEntry);
            if (queryBackupEntry!=null) {
                queryHistoryEntry.setQueryBackupEntry(queryBackupEntry);
            }
            queryHistoryService.save(queryHistoryEntry);

            messages.add(messageService.getMessage("label.qascript.updated"));

            // clear qascript list in cache
            QAScriptListLoader.reloadList(request);
        } catch (DCMException e) {
            LOGGER.info("Error during script file upload " + e.getMessage());
            throw new RuntimeException("Edit QA script error: " + messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/scripts/" + scriptId;
//        httpServletRequest.setAttribute("schema", schema);
    }

    @PostMapping(params = {"update"})
    public String update(@ModelAttribute("form") QAScriptForm form,
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
        Boolean asynchronousExecution = form.isAsynchronousExecution();
        boolean active = form.getActive();
        Boolean markedHeavy = form.isMarkedHeavy();
        String markedHeavyReason = form.getMarkedHeavyReason();
        String markedHeavyReasonOther = form.getMarkedHeavyReasonOther();
        String ruleMatch = form.getRuleMatch();

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

        new QAScriptValidator().validateUpdate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/scripts/edit";
        }
        Integer markedHeavyReasonStatus = null;
        if(markedHeavy){
            //Get Marked heavy reason code
            markedHeavyReasonStatus = ScriptUtils.getHeavyScriptReasonCodeByText(markedHeavyReason);
        }
        if(markedHeavyReasonStatus != HeavyScriptReasonEnum.OTHER.getCode()){
            markedHeavyReasonOther = null;
        }


        try {
            Integer maxVersion = queryJpaService.findMaxVersion(Integer.parseInt(scriptId));
            Integer newVersion = maxVersion;

            QueryEntry oldScript = queryJpaService.findByQueryId(Integer.parseInt(scriptId));
            //if the content was updated or the marked heavy field was changed, the version must be increased
            if(updateContent || (oldScript.getMarkedHeavy() != null && oldScript.getMarkedHeavy() != markedHeavy)){
                newVersion = maxVersion + 1;
            }
            QAScriptManager qm = new QAScriptManager();
            QueryBackupEntry queryBackupEntry = qm.update(user, scriptId, shortName, schemaId, resultType, desc, scriptType, curFileName, upperLimit,
                    url, scriptContent, updateContent, asynchronousExecution, active, newVersion, markedHeavy, markedHeavyReasonStatus, markedHeavyReasonOther, ruleMatch);
            qm.activateDeactivate(user, scriptId, active);

            QueryEntry queryEntry = new QueryEntry(Integer.parseInt(scriptId));
            QueryHistoryEntry queryHistoryEntry = ScriptUtils.createQueryHistoryEntry(user, shortName, schemaId, resultType, desc, scriptType, upperLimit, url,
                    asynchronousExecution, active, curFileName, newVersion, markedHeavy, markedHeavyReasonStatus, markedHeavyReasonOther, ruleMatch);
            queryHistoryEntry.setQueryEntry(queryEntry);
            if (queryBackupEntry!=null) {
                queryHistoryEntry.setQueryBackupEntry(queryBackupEntry);
            }
            queryHistoryService.save(queryHistoryEntry);

            // clear qascript list in cache
            QAScriptListLoader.reloadList(request);
            messages.add(messageService.getMessage("label.qascript.updated"));
        } catch (DCMException e) {
            throw new RuntimeException("Edit QA script error: " + messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
//        redirectAttributes.addFlashAttribute("schema", schema);
        return "redirect:/scripts/" + scriptId;
    }

    @GetMapping("/{scriptId}/delete")
    public String deleteGet(@PathVariable String scriptId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();
        String user = (String) httpServletRequest.getSession().getAttribute("user");

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
        return "redirect:/scripts";
    }

    @PostMapping(params = {"delete"})
    public String deletePost(@ModelAttribute("scriptForm") QAScriptForm scriptForm, BindingResult bindingResult,
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

            QueryEntry queryEntry = queryJpaService.findByQueryId(Integer.parseInt(scriptId));
            QueryHistoryEntry queryHistoryEntry = ScriptUtils.createQueryHistoryEntry(user, queryEntry.getShortName(), queryEntry.getSchemaId().toString(), queryEntry.getResultType(), queryEntry.getDescription(),
                    queryEntry.getScriptType(), queryEntry.getUpperLimit().toString(), queryEntry.getUrl(), queryEntry.isAsynchronousExecution(),
                    queryEntry.isActive(), queryEntry.getQueryFileName(), queryEntry.getVersion(), queryEntry.getMarkedHeavy(), queryEntry.getMarkedHeavyReason(), queryEntry.getMarkedHeavyReasonOther(), queryEntry.getRuleMatch());
            queryHistoryEntry.setQueryEntry(queryEntry);
            queryHistoryService.save(queryHistoryEntry);

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

            QueryEntry queryEntry = queryJpaService.findByQueryId(Integer.parseInt(scriptId));
            QueryHistoryEntry queryHistoryEntry = ScriptUtils.createQueryHistoryEntry(user, queryEntry.getShortName(), queryEntry.getSchemaId().toString(), queryEntry.getResultType(), queryEntry.getDescription(),
                    queryEntry.getScriptType(), queryEntry.getUpperLimit().toString(), queryEntry.getUrl(), queryEntry.isAsynchronousExecution(), queryEntry.isActive(), queryEntry.getQueryFileName(),
                    queryEntry.getVersion(), queryEntry.getMarkedHeavy(), queryEntry.getMarkedHeavyReason(), queryEntry.getMarkedHeavyReasonOther(), queryEntry.getRuleMatch());
            queryHistoryEntry.setQueryEntry(queryEntry);
            queryHistoryService.save(queryHistoryEntry);

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

    @GetMapping("/{id}/executionHistory")
    public String executionHistory(@PathVariable String id, @RequestParam(value = "historyPageNumber", required = false, defaultValue = "1") int historyPageNumber,
                                    @RequestParam(value = "historySize", required = false, defaultValue = "10") int historySize,
                                    @RequestParam(value = "versionPageNumber", required = false, defaultValue = "1") int versionPageNumber,
                                    @RequestParam(value = "versionSize", required = false, defaultValue = "10") int versionSize, Model model, HttpServletRequest request) {

        //Setup headerVariables
        model = ThymeleafUtils.setUpTitleAndLogin(model, Properties.getStringProperty("label.qascript.executionHistory.title"), request);
        //Setup breadcrumbs
        model = ThymeleafUtils.setUpBreadCrumbsForScriptPages(model, id, Properties.getStringProperty("label.qascript.executionHistory.title"));

        Boolean isFMEScript = false;

        String changedHistoryPageSize = request.getParameter("pageHistoryEntries");
        if(!Utils.isNullStr(changedHistoryPageSize)){
            historySize = Integer.valueOf(changedHistoryPageSize);
        }

        String changedVersionPageSize = request.getParameter("pageVersionEntries");
        if(!Utils.isNullStr(changedVersionPageSize)){
            versionSize = Integer.valueOf(changedVersionPageSize);
        }

        Paged<QueryMetadataEntry> pagedVersionEntries = queryMetadataService.getQueryMetadataEntries(versionPageNumber, versionSize, Integer.valueOf(id));
        Paged<QueryMetadataHistoryEntry> pagedHistoryEntries = queryMetadataService.getQueryMetadataHistoryEntries(historyPageNumber, historySize, Integer.valueOf(id));

        if(pagedHistoryEntries != null && !Utils.isNullList(pagedHistoryEntries.getPage().getContent())){
            QueryMetadataHistoryEntry firstQueryMetadataHistoryEntry = pagedHistoryEntries.getPage().getContent().get(0);
            if(firstQueryMetadataHistoryEntry.getScriptType().equals("fme")){
                isFMEScript = true;
            }
        }

        model.addAttribute("versionTableSize", queryMetadataService.getCountOfEntriesByScript(Integer.valueOf(id)));
        model.addAttribute("historyTableSize", queryMetadataService.getCountOfHistoryEntriesByScript(Integer.valueOf(id)));

        model.addAttribute("versionTable", pagedVersionEntries);
        model.addAttribute("history", pagedHistoryEntries);
        model.addAttribute("scriptId", id);
        model.addAttribute("pageHistoryEntries", historySize);
        model.addAttribute("pageVersionEntries", versionSize);
        model.addAttribute("fmeJobUrl", Properties.FME_JOB_URL);
        model.addAttribute("isFMEScript", isFMEScript);


        return "scriptHistory/scriptExecutionHistory";
    }

    @GetMapping("/history/{queryId}")
    public String getScriptsHistory(@PathVariable Integer queryId, Model model, HttpServletRequest request) {

        //Setup headerVariables
        model = ThymeleafUtils.setUpTitleAndLogin(model, Properties.getStringProperty("label.qascriptHistory.title"), request);
        //Setup breadcrumbs
        model = ThymeleafUtils.setUpBreadCrumbsForScriptPages(model, queryId.toString(), Properties.getStringProperty("label.qascriptHistory.title"));

        //we pass queryId in html to be able to use it in the tabbed menu and in scriptInfoHistory.js vue script
        model.addAttribute("queryId", queryId);
        return "scriptHistory/scriptHistory";
    }
}
