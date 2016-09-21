package eionet.gdem.web.struts.remoteapi;

import com.google.gson.Gson;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.services.QueueJobsService;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class QueueMonitoringAction extends Action {

    private final String TIME_SINCE_LATEST_JOB_EXECUTION = "minutes";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        QueueJobsService queueJobsService = (QueueJobsService) SpringApplicationContext.getBean("queueJobsService");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HashMap<String, String> results = new HashMap<String, String>();

        try {
            String jobDetails = (queueJobsService.getLatestProcessingJobStartTime());

            if (jobDetails == "") {
                results.put(TIME_SINCE_LATEST_JOB_EXECUTION, "0");
                out.print(new Gson().toJson(results));
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = dateFormat.parse(jobDetails);
            Calendar calendar = Calendar.getInstance();
            long currentTimeInMilliseconds = calendar.getTimeInMillis();
            calendar.setTime(parsedDate);
            long latestJobStartTimeInMillis = calendar.getTimeInMillis();
            long diff = currentTimeInMilliseconds - latestJobStartTimeInMillis;
            long diffInMinutes = ((diff / (1000 * 60)) % 60);
            results.put(TIME_SINCE_LATEST_JOB_EXECUTION, Long.toString(diffInMinutes));
            out.print(new Gson().toJson(results));
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

}
