package eionet.gdem.api;

import eionet.gdem.Properties;
import eionet.gdem.api.errors.BadRequestException;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/workqueue")
public class WorkqueueApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkqueueApiController.class);
    /** Job ID parameter name */
    protected static final String JOB_ID_PARAM = "job_id";
    /** Job deletion token parameter name */
    protected static final String TOKEN_PARAM = "job_deletion_token";

    public void delete(HttpServletRequest request, HttpServletResponse response) {
        StopWatch timer = new StopWatch();
        timer.start();
        String jobId = null;
        String authenticationToken = null;
        try {
            /* The request method should be DELETE*/
            if (request.getMethod() != "POST") {
                throw new BadRequestException("The request method was not POST.");
            }
            Map params = request.getParameterMap();
            // parse request parameters
            if (params.containsKey(TOKEN_PARAM)) {
                authenticationToken = (String) ((Object[]) params.get(TOKEN_PARAM))[0];
            }
            else{
                throw new BadRequestException("No authentication token was provided for job deletion via API.");
            }
            if (params.containsKey(JOB_ID_PARAM)) {
                jobId = (String) ((Object[]) params.get(JOB_ID_PARAM))[0];
            }
            else{
                throw new BadRequestException("No job id was provided for job deletion via API.");
            }

            /* Check validity of token */
            if (authenticationToken != Properties.JOB_DELETION_AUTHENTICATION_TOKEN){
                throw new BadRequestException("Wrong token was provided for job deletion via API.");
            }
            LOGGER.info("Deleting job via API with id " + jobId);

            /* Convert String to String array */
            String[] jobIds = new String[1];
            jobIds[0] = jobId;
            WorkqueueManager workqueueManager = new WorkqueueManager();

            workqueueManager.deleteJobs(jobIds);
            timer.stop();
            LOGGER.info(String.format("Deleting of job #%s via API was completed, total time of execution: %s", jobId, timer.toString()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
