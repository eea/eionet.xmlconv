package eionet.gdem.web.spring.workqueue;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 *
 *
 */
@Controller
@RequestMapping("/workqueue")
public class QAJobsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAJobsController.class);
    private MessageService messageService;

    @Autowired
    public QAJobsController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model, HttpServletRequest httpServletRequest) {

        String userName = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            boolean wqdPrm;
            boolean wquPrm;
            boolean wqvPrm;
            boolean logvPrm;
            if (userName != null) {
                wqdPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "d");
                wquPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "u");
                wqvPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "v");
                logvPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_LOGFILE_PATH, "v");
            }
        } catch (SignOnException e) {
            LOGGER.error("Error");
        }

        String[][] list = null;
        try {
            eionet.gdem.services.db.dao.IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();
            list = jobDao.getJobData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String tmpFolder = Constants.TMP_FOLDER;
        String queriesFolder = Constants.QUERIES_FOLDER;

        eionet.gdem.services.db.dao.IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
        for (int i = 0; i < list.length; i++) {
            String jobId = list[i][0];
            String url = list[i][1];
            String xqLongFileName = list[i][2];
            String xqFile = list[i][2].substring(list[i][2].lastIndexOf(File.separatorChar) + 1);
            String resultFile = list[i][3].substring(list[i][3].lastIndexOf(File.separatorChar) + 1);
            int status = Integer.parseInt(list[i][4]);
            String timeStamp = list[i][5];
            String xqStringID = list[i][6];
            String instance = list[i][7];

            int xqID = 0;
            String scriptType = "";
            try {
                xqID = Integer.parseInt(xqStringID);
                java.util.HashMap query = queryDao.getQueryInfo(xqStringID);
                if (query != null) {
                    scriptType = (String) query.get("script_type");
                }
            } catch (NumberFormatException n) {
                xqID = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

            String xqFileURL = "";
            String xqText = "Show script";
            if (xqID == Constants.JOB_VALIDATION) {
                xqText = "Show XML Schema";
                xqFileURL = xqLongFileName;
            } else if (xqID == Constants.JOB_FROMSTRING) {
                xqFileURL = tmpFolder + xqFile;
            } else {
                xqFileURL = queriesFolder + xqFile;
            }


            if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING)
                resultFile = null;

            //TODO Status name, maybe better to move to some Java common class
            String statusName = "-- Unknown --";

            if (status == Constants.XQ_RECEIVED)
                statusName = "JOB RECEIVED";
            if (status == Constants.XQ_DOWNLOADING_SRC)
                statusName = "DOWNLOADING SOURCE";
            if (status == Constants.XQ_PROCESSING)
                statusName = "PROCESSING";
            if (status == Constants.XQ_READY)
                statusName = "READY";
            if (status == Constants.XQ_FATAL_ERR)
                statusName = "FATAL ERROR";
            if (status == Constants.XQ_LIGHT_ERR)
                statusName = "RECOVERABLE ERROR";


            if (url.indexOf(Constants.GETSOURCE_URL) > 0 && url.indexOf(Constants.SOURCE_URL_PARAM) > 0) {
                int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
                url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
            }


            String urlName = (url.length() > Constants.URL_TEXT_LEN ? url.substring(0, Constants.URL_TEXT_LEN) + "..." : url);
        }
        model.addAttribute("jobList", list);
        return "/workqueue.jsp";
    }

}
