package eionet.gdem.api.qa.service.impl;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQueryService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Service
public class QaServiceImpl implements QaService {

    private XQueryService xQueryService;

    public QaServiceImpl() {
    }

    public QaServiceImpl(XQueryService xQueryService) {
        this.xQueryService = xQueryService;
    }

    @Override
    public HashMap<String, String> extractLinksAndSchemasFromEnvelopeUrl(String envelopeUrl) throws XMLConvException {
        HashMap<String, String> fileSchemaAndLinks = new HashMap<String, String>();

        try {
            Document doc = this.getXMLFromEnvelopeURL(envelopeUrl);
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("//envelope/file");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                NamedNodeMap fileNode = nl.item(i).getAttributes();
                fileSchemaAndLinks.put(fileNode.getNamedItem("link").getTextContent(), fileNode.getNamedItem("schema").getTextContent());
            }

        } catch (XPathExpressionException ex) {
            throw new XMLConvException("exception while parsing the envelope URL:" + envelopeUrl + " to extract files and schemas", ex);
        }
        return fileSchemaAndLinks;
    }

    @Override
    public List<QaResultsWrapper> scheduleJobs(String envelopeUrl) throws XMLConvException {

        HashMap<String, String> fileLinksAndSchemas = extractLinksAndSchemasFromEnvelopeUrl(envelopeUrl);

        XQueryService xqService = getXqueryService();
        Hashtable table = new Hashtable();
        try {
            for (Map.Entry<String, String> entry : fileLinksAndSchemas.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != "" && value != "") {
                    Vector files = new Vector();
                    files.add(key);
                    table.put(value, files);
                }
            }
            Vector jobIdsAndFileUrls = xqService.analyzeXMLFiles(table);
            List<QaResultsWrapper> results = new ArrayList<QaResultsWrapper>();
            for (int i = 0; i < jobIdsAndFileUrls.size(); i++) {
                Vector<String> KeyValuePair = (Vector<String>) jobIdsAndFileUrls.get(i);
                QaResultsWrapper qaResult = new QaResultsWrapper();
                qaResult.setJobId(KeyValuePair.get(0));
                qaResult.setFileUrl(KeyValuePair.get(1));
                results.add(qaResult);
            }

            return results;
        } catch (XMLConvException ex) {
            throw new XMLConvException("error scheduling Jobs with XQueryService ", ex);
        }

    }

    @Override
    public Vector runQaScript(String sourceUrl, String scriptId) throws XMLConvException {
        XQueryService xqService = getXqueryService();
        try {
            return xqService.runQAScript(sourceUrl, scriptId);
        } catch (XMLConvException ex) {
            throw new XMLConvException("error running Qa Script for sourceUrl :" + sourceUrl + " and scriptId:" + scriptId, ex);
        }
    }

    @Override
    public Hashtable<String, Object> getJobResults(String jobId) throws XMLConvException {

        XQueryService xqueryService = getXqueryService(); // new XQueryService();
        Hashtable<String, Object> results = xqueryService.getResult(jobId);
        int resultCode = Integer.parseInt((String) results.get(Constants.RESULT_CODE_PRM));
        String executionStatusName = "";
        switch (resultCode) {

            case Constants.JOB_READY:
                executionStatusName = "Ready";
                break;
            case Constants.JOB_LIGHT_ERROR:
                executionStatusName = "Not Found";
                break;

            case Constants.JOB_FATAL_ERROR:
                executionStatusName = "Failed";
                break;

            case Constants.JOB_NOT_READY:
                executionStatusName = "Pending";
                break;

        }
        results.put("executionStatusName", executionStatusName);
        return results;
    }

    @Override
    public List<LinkedHashMap<String, String>> listQAScripts(String schema, String active) throws XMLConvException {
        XQueryService xqueryService = new XQueryService();
        Vector xqueryServiceResults = xqueryService.listQAScripts(schema, active);
        List<LinkedHashMap<String, String>> resultsList = new LinkedList<LinkedHashMap<String, String>>();
        for (Object xqueryServiceResult : xqueryServiceResults) {
            Hashtable hs = (Hashtable) xqueryServiceResult;
            String scriptType = (String)hs.get(QaScriptView.SCRIPT_TYPE);
            if (scriptType==null) {
                scriptType ="xsd";
            }
            LinkedHashMap<String, String> rearrangedResults = new LinkedHashMap<String, String>();
            rearrangedResults.put(QaScriptView.QUERY_ID, (String) hs.get(QaScriptView.QUERY_ID));
            rearrangedResults.put(QaScriptView.TYPE, scriptType);
            rearrangedResults.put(QaScriptView.CONTENT_TYPE_ID, (String) hs.get(QaScriptView.CONTENT_TYPE_ID));
            rearrangedResults.put(QaScriptView.QUERY_AS_URL, (String) hs.get(QaScriptView.QUERY));
            rearrangedResults.put(QaScriptView.SHORT_NAME, (String) hs.get(QaScriptView.SHORT_NAME));
            rearrangedResults.put(QaScriptView.DESCRIPTION, (String) hs.get(QaScriptView.DESCRIPTION));
            rearrangedResults.put(QaScriptView.IS_ACTIVE, (String) hs.get(QaScriptView.IS_ACTIVE));
            rearrangedResults.put(QaScriptView.UPPER_LIMIT, (String) hs.get(QaScriptView.UPPER_LIMIT));
            rearrangedResults.put(QaScriptView.XML_SCHEMA, (String) hs.get(QaScriptView.XML_SCHEMA));
            resultsList.add(rearrangedResults);
        }

        return resultsList;
    }

    @Override
    public XQueryService getXqueryService() {
        if (xQueryService == null) {
            synchronized (QaServiceImpl.class){
                if(xQueryService ==null){
                    xQueryService = new XQueryService();
                }
            }
        }
        return xQueryService;
    }

    @Override
    public Document getXMLFromEnvelopeURL(String envelopeUrl) throws XMLConvException {

        Document doc;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            URL url = new URL(envelopeUrl + "/xml");
            doc = db.parse(url.openStream());
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            throw new XMLConvException("exception while parsing the envelope URL:" + envelopeUrl + " to extract files and schemas", ex);
        }
        return doc;
    }

}
