package eionet.gdem.utils.xml;

import eionet.gdem.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.HashMap;

/**
 * Analyzes feedback HTML response.
 * @author Unknown
 * @author George Sofianos
 */
public final class FeedbackAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackAnalyzer.class);
    /**
     * Default private constructor for util class
     */
    private FeedbackAnalyzer() {
        //do nothing
    }

    /**
     * Parses the XQ feedback string from file and searches feedbackStatus and feedbackMessage parameters in the first element
     * (&lt;div&gt;).
     *
     * @param fileName
     *            XQ Script result file
     * @return HashMap containing the element values
     */
    public static HashMap<String, String> getFeedbackResultFromFile(String fileName) {
        FileInputStream stream = null;
        HashMap<String, String> fbResult = null;

        try {
            stream = new FileInputStream(fileName);
            Document document = Jsoup.parse(stream, "UTF-8", "");
            fbResult = getFeedbackMap(document);
        } catch (Exception e) {
            LOGGER.error("Error getting feedback result from file " + e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return fbResult;
    }

    /**
     * Returns feedback status and feedback message.
     * @param document Jsoup Document
     * @return feedback status and feedback message.
     */
    private static HashMap<String, String> getFeedbackMap(Document document) {
        HashMap<String, String> fbResult = new HashMap<String, String>();
        String fbStatus = Constants.XQ_FEEDBACKSTATUS_UNKNOWN;
        LOGGER.info("Feedback status is set to " + Constants.XQ_FEEDBACKSTATUS_UNKNOWN);
        String fbMessage = "";

        Element feedbackStatus = document.select("#feedbackStatus").first();
        if (feedbackStatus != null) {
            fbStatus = StringUtils.defaultIfBlank(feedbackStatus.attr("class"), Constants.XQ_FEEDBACKSTATUS_UNKNOWN);
            if(fbStatus.equals(Constants.XQ_FEEDBACKSTATUS_UNKNOWN)){
                LOGGER.info("feedbackStatus.attr(class) is blank so status will be set to " + Constants.XQ_FEEDBACKSTATUS_UNKNOWN);
            }
            fbMessage = feedbackStatus.text();
        }
        fbResult.put(Constants.RESULT_FEEDBACKSTATUS_PRM, fbStatus);
        LOGGER.info("Feedback status is set to " + fbStatus);
        fbResult.put(Constants.RESULT_FEEDBACKMESSAGE_PRM, fbMessage);
        return fbResult;
    }
    /**
     * Parses the XQ feedback string from string and searches feedbackStatus and feedbackMessage parameters in the first element.
     *
     * @param scriptResult
     *            XQ Script result
     * @return HashMap containing the element values
     */
    public static HashMap<String, String> getFeedbackResultFromStr(String scriptResult) {

        Document document = Jsoup.parse(scriptResult);
        HashMap<String, String> fbResult = null;
        try {
            fbResult = getFeedbackMap(document);
        } catch (Exception e) {
            LOGGER.error("Error getting feedback result from file " + e);
        }
        return fbResult;
    }

}
