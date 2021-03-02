package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.JobResultHandlerService;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

import static eionet.gdem.Constants.JOB_VALIDATION;

@Service
public class JobResultHandlerServiceImpl implements JobResultHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResultHandlerServiceImpl.class);

    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    @Autowired
    public JobResultHandlerServiceImpl() {

    }

    /**
     * Checks if the job is ready (or error) and returns the result (or error message).
     *
     * @param jobId Job Id
     * @return Hash including code and result
     * @throws XMLConvException If an error occurs.
     */
    @Override
    public Hashtable<String,String> getResult(String jobId) throws XMLConvException {

        LOGGER.info("XML/RPC call for getting result with JOB ID: " + jobId);

        String[] jobData = null;
        HashMap scriptData = null;
        int status = 0;
        try {
            jobData = xqJobDao.getXQJobData(jobId);

            if (jobData == null) { // no such job
                // throw new XMLConvException("** No such job with ID=" + jobId + " in the queue.");
                status = Constants.XQ_JOBNOTFOUND_ERR;
            } else {
                scriptData = queryDao.getQueryInfo(jobData[5]);

                status = Integer.valueOf(jobData[3]).intValue();
            }
        } catch (SQLException sqle) {
            throw new XMLConvException("Error getting XQJob data from DB: " + sqle.toString());
        }

        LOGGER.info("QueryService found status for job (" + jobId + "):" + String.valueOf(status));

        Hashtable ret = prepareResult(status, jobData, scriptData, jobId);
        if (LOGGER.isInfoEnabled()) {
            String result = ret.toString();
            if (result.length() > 100) {
                result = result.substring(0, 100).concat("....");
            }
            LOGGER.info("result: " + result);
        }
        return ret;
    }

    /**
     * Hashtable to be composed for the getResult() method return value.
     * @param status Status
     * @param jobData Job data
     * @param scriptData Script data
     * @param jobId Job Id
     * @return Result
     * @throws XMLConvException If an error occurs.
     */
    protected Hashtable<String,String> prepareResult(int status, String[] jobData, HashMap scriptData, String jobId) throws XMLConvException {
        Hashtable<String, String> h = new Hashtable<String, String>();
        int resultCode;
        String resultValue = "";
        String metatype = "";
        String script_title = "";

        String feedbackStatus = Constants.XQ_FEEDBACKSTATUS_UNKNOWN;
        String feedbackMsg = "";

        if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING) {
            resultCode = Constants.JOB_NOT_READY;
            resultValue = "*** Not ready ***";
        } else if (status == Constants.XQ_JOBNOTFOUND_ERR) {
            resultCode = Constants.JOB_LIGHT_ERROR;
            resultValue = "*** No such job or the job result has been already downloaded. ***";
        } else {
            if (status == Constants.XQ_READY) {
                resultCode = Constants.JOB_READY;
            } else if (status == Constants.XQ_LIGHT_ERR) {
                resultCode = Constants.JOB_READY;
            } else if (status == Constants.XQ_FATAL_ERR) {
                resultCode = Constants.JOB_READY;
            } else {
                resultCode = -1; // not expected to reach here
            }

            try {
                int xq_id = 0;
                try {
                    xq_id = Integer.parseInt(jobData[5]);
                } catch (NumberFormatException n) {
                }

                if (xq_id == JOB_VALIDATION) {
                    metatype = "text/html";
                    script_title = "XML Schema validation";
                } else if (xq_id > 0) {
                    metatype = (String) scriptData.get(QaScriptView.META_TYPE);
                    script_title = (String) scriptData.get(QaScriptView.SHORT_NAME);
                }

                resultValue = Utils.readStrFromFile(jobData[2]);
                HashMap<String, String> feedbackResult = FeedbackAnalyzer.getFeedbackResultFromFile(jobData[2]);

                feedbackStatus = feedbackResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM);
                feedbackMsg = feedbackResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM);


            } catch (Exception ioe) {
                resultCode = Constants.JOB_FATAL_ERROR;
                resultValue = "<error>Error reading the XQ value from the file:" + jobData[2] + "</error>";
            }

        }
        try {
            h.put(Constants.RESULT_CODE_PRM, Integer.toString(resultCode));
            h.put(Constants.RESULT_VALUE_PRM, resultValue);
            h.put(Constants.RESULT_METATYPE_PRM, metatype);
            h.put(Constants.RESULT_SCRIPTTITLE_PRM, script_title);
            h.put(Constants.RESULT_FEEDBACKSTATUS_PRM, feedbackStatus);
            h.put(Constants.RESULT_FEEDBACKMESSAGE_PRM, feedbackMsg);

        } catch (Exception e) {
            String err_mess =
                    "JobID: " + jobId + "; Creating result Hashtable for getResult method failed result: " + e.toString();
            LOGGER.error(err_mess);
            throw new XMLConvException(err_mess, e);
        }

        return h;

    }
}