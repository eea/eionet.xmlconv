package eionet.gdem.api;

import eionet.gdem.XMLConvException;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/workqueue")
public class WorkqueueApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkqueueApiController.class);
    /** Job ID parameter name */
    protected static final String JOB_ID_PARAM = "job_id";

    @RequestMapping(value = "/delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        StopWatch timer = new StopWatch();
        timer.start();
        String jobId = null;
        try {
            /* The request method should be POST*/
            if (!request.getMethod().equals("POST")) {
                LOGGER.error("The request method was not POST.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("\"{\"message\":\"Method was not POST\"}\"");
                return;
            }
            Map params = request.getParameterMap();
            // parse request parameters
            if (params.containsKey(JOB_ID_PARAM)) {
                jobId = (String) ((Object[]) params.get(JOB_ID_PARAM))[0];
            }
            else{
                LOGGER.error("No job id was provided for job deletion via API.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("\"{\"message\":\"Missing job id from request\"}\"");
                return;
            }
            LOGGER.info("Deleting job via API with id " + jobId);

            /* Convert String to String array */
            String[] jobIds = new String[1];
            jobIds[0] = jobId;
            callWQManagerDeleteMethod(jobIds);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("\"{\"message\":\"Job deleted successfully\"}\"");
            timer.stop();
            LOGGER.info(String.format("Deleting of job #%s via API was completed, total time of execution: %s", jobId, timer.toString()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void callWQManagerDeleteMethod(String[] jobIds) throws XMLConvException {
        WorkqueueManager workqueueManager = new WorkqueueManager();
        workqueueManager.deleteJobs(jobIds);
    }


}
