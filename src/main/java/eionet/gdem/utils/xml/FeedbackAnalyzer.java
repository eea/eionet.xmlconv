package eionet.gdem.utils.xml;

import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import eionet.gdem.Constants;

public class FeedbackAnalyzer {


    /**
     * Parses the XQ feedback string from file and searches feedbackStatus and feedbackMessage parameters in the first element
     * (<div>).
     *
     * @param fileName
     *            XQ Script result file
     * @return HashMap containing the element values
     */
    public static HashMap<String, String> getFeedbackResultFromFile(String fileName) {

        InputSource is = null;
        FileReader fileReader = null;
        HashMap <String, String> fbResult = null;
        try {
            fileReader = new FileReader(fileName);
            is = new InputSource(fileReader);

            fbResult =  getParsedFeedbackResult(is);

        } catch (Exception e) {
            System.err.println("Error getting feedback result from file " + e);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }


        return fbResult;
    }


    /**
     * Parses the XQ feedback string from file and searches feedbackStatus and feedbackMessage parameters in the first element
     * (<div>).
     *
     * @param InputSource
     *            XQ Script result file
     * @return HashMap containing the element values
     */
    private static HashMap<String, String> getParsedFeedbackResult(InputSource is) {

        String fbStatus = Constants.XQ_FEEDBACKSTATUS_UNKNOWN;
        String fbMessage = "";

        HashMap<String, String> fbResult = new HashMap<String, String>();
        try {

            FeedbackHandler handler = new FeedbackHandler();
            SAXParserFactory spfact = SAXParserFactory.newInstance();
            SAXParser parser = spfact.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);

            //FeedbackXMLReader lexicalReader = new FeedbackXMLReader(); // parser.getXMLReader();

            //DefaultHandler handler = new FeedbackXMLHandler();
            //parser.setProperty("http://xml.org/sax/properties/lexical-handler", lexicalReader);

            parser.parse(is, handler);
            fbStatus = handler.getFeedbackStatus();
            fbMessage = handler.getFeedbackMessage();

        } catch (Exception e) {
            System.err.println("Error parsing feedback result " + e);
        }

        fbResult.put(Constants.RESULT_FEEDBACKSTATUS_PRM, fbStatus);
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

        InputSource is = new InputSource(new StringReader(scriptResult));
        HashMap <String, String> fbResult = null;
        try {

            fbResult =  getParsedFeedbackResult(is);

        } catch (Exception e) {
            System.err.println("Error getting feedback result from String " + e);
        }

        return fbResult;
    }

    /**
     * Parses feedback xml/html.
     *
     */
    private static class FeedbackHandler extends DefaultHandler {
        //if true nothing else is done
        private boolean feedbackElementFound = false;
        private boolean parsingFeedBack = false;

        private String feedbackStatus = Constants.XQ_FEEDBACKSTATUS_UNKNOWN;
        private String feedbackMessage = "";

        StringBuilder fbTextBuilder = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (!feedbackElementFound) {
                String idValue = attributes.getValue("id");
                if (idValue != null && idValue.equalsIgnoreCase("feedbackStatus")) {
                    feedbackElementFound = true;
                    feedbackStatus = StringUtils.defaultIfBlank(attributes.getValue("class"), Constants.XQ_FEEDBACKSTATUS_UNKNOWN);
                    //feedbackMessage = StringUtils.defaultIfBlank(attributes.getValue("feedbackMessage"), "");
                    parsingFeedBack = true;
                }
            }
        }



        @Override
        public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
            if (parsingFeedBack) {
                parsingFeedBack = false;
                feedbackMessage = fbTextBuilder.toString();
            }
        }



        @Override
        public void characters(char[] characters, int i1, int i2) throws SAXException {
            if (parsingFeedBack) {
                fbTextBuilder.append(new String(characters, i1, i2));
            }
        }



        public String getFeedbackStatus() {
            return feedbackStatus;
        }

        public String getFeedbackMessage() {
            return feedbackMessage;
        }

    }
}
