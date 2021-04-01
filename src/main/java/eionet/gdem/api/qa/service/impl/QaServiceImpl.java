package eionet.gdem.api.qa.service.impl;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.dto.Schema;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.QueryService;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.services.JobResultHandlerService;
import eionet.gdem.services.RunScriptAutomaticService;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Service
public class QaServiceImpl implements QaService {

    private QueryService queryService;
    /** DAO for getting schema info. */
    private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();;
    private static final Logger LOGGER = LoggerFactory.getLogger(QaService.class);

    private JobRequestHandlerService jobRequestHandlerService;

    private JobResultHandlerService jobResultHandlerService;

    private RunScriptAutomaticService runScriptAutomaticService;

    public QaServiceImpl() {
    }

    @Autowired
    public QaServiceImpl(QueryService queryService, JobRequestHandlerService jobRequestHandlerService, JobResultHandlerService jobResultHandlerService, RunScriptAutomaticService runScriptAutomaticService) {
        this.queryService = queryService;
        this.jobRequestHandlerService = jobRequestHandlerService;
        this.jobResultHandlerService = jobResultHandlerService;
        this.runScriptAutomaticService = runScriptAutomaticService;
    }

    @Override
    public HashMap<String, String> extractFileLinksAndSchemasFromEnvelopeUrl(String envelopeUrl) throws XMLConvException {
        HashMap<String, String> fileSchemaAndLinks = new HashMap<String, String>();

        try {
            Document doc = this.getXMLFromEnvelopeURL(envelopeUrl);
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expressionForEnvelopeFiles = xPath.compile("//envelope/file");
            NodeList envelopeFilesNodeList = (NodeList) expressionForEnvelopeFiles.evaluate(doc, XPathConstants.NODESET);
            int length = envelopeFilesNodeList.getLength();
            for (int i = 0; i < length; i++) {
                NamedNodeMap fileNode = envelopeFilesNodeList.item(i).getAttributes();
                fileSchemaAndLinks.put(fileNode.getNamedItem("link").getTextContent(), fileNode.getNamedItem("schema").getTextContent());
            }
        } catch (XPathExpressionException ex) {
            throw new XMLConvException("exception while parsing the envelope XML:" + envelopeUrl + " to extract files and schemas", ex);
        }

        return fileSchemaAndLinks;
    }

    @Override
    public List<String> extractObligationUrlsFromEnvelopeUrl(String envelopeUrl) throws XMLConvException {
        try {
            Document doc = this.getXMLFromEnvelopeURL(envelopeUrl);
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expressionForObligation = xPath.compile("//envelope/obligation");
            NodeList obligationNodeList = (NodeList) expressionForObligation.evaluate(doc, XPathConstants.NODESET);
            int length = obligationNodeList.getLength();
            List<String> obligationUrls = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Node obligationNode = obligationNodeList.item(i);
                obligationUrls.add(obligationNode.getTextContent());
            }
            return obligationUrls;
        } catch (XPathExpressionException ex) {
            throw new XMLConvException("exception while parsing the envelope XML:" + envelopeUrl + " to extract obligation", ex);
        }
    }

    @Override
    public List<QaResultsWrapper> scheduleJobs(String envelopeUrl) throws XMLConvException {

        HashMap<String, String> fileLinksAndSchemas = extractFileLinksAndSchemasFromEnvelopeUrl(envelopeUrl);

        HashMap map = new HashMap();
        try {
            for (Map.Entry<String, String> entry : fileLinksAndSchemas.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != "" && value != "") {
                    List<String> files = new ArrayList<>();
                    files.add(key);
                    map.put(value, files);
                }
            }

            this.addObligationsFiles(map,envelopeUrl);
            if (map.size() == 0) {
                LOGGER.info("Could not find files and their schemas. There was an issue with the envelope " + envelopeUrl);
            }
            HashMap<String, String> jobIdsAndFileUrls = getJobRequestHandlerService().analyzeMultipleXMLFiles(map);

            List<QaResultsWrapper> results = new ArrayList<QaResultsWrapper>();
            for (Map.Entry<String, String> entry : jobIdsAndFileUrls.entrySet()) {
                QaResultsWrapper qaResult = new QaResultsWrapper();
                qaResult.setJobId(entry.getKey());
                qaResult.setFileUrl(entry.getValue());
                results.add(qaResult);
            }
            return results;
        } catch (XMLConvException ex) {
            throw new XMLConvException("error scheduling Jobs with QueryService ", ex);
        }

    }

    @Override
    public Vector runQaScript(String sourceUrl, String scriptId) throws XMLConvException {
        try {
            return getRunScriptAutomaticService().runQAScript(sourceUrl, scriptId);
        } catch (XMLConvException ex) {
            throw new XMLConvException("error running Qa Script for sourceUrl :" + sourceUrl + " and scriptId:" + scriptId, ex);
        }
    }

    @Override
    public Hashtable<String, Object> getJobResults(String jobId) throws XMLConvException {

        QueryService queryService = getQueryService(); // new QueryService();
        Hashtable<String, Object> results = getJobResultHandlerService().getResult(jobId);
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

            case Constants.CANCELLED_BY_USER:
                executionStatusName = "Cancelled by user";
                break;

            case Constants.DELETED:
                executionStatusName = "Deleted";
                break;

        }
        results.put("executionStatusName", executionStatusName);
        return results;
    }

    @Override
    public List<LinkedHashMap<String, String>> listQAScripts(String schema, String active) throws XMLConvException {
        QueryService queryService = new QueryService();
        Vector queryServiceResults = queryService.listQAScripts(schema, active);
        List<LinkedHashMap<String, String>> resultsList = new LinkedList<LinkedHashMap<String, String>>();
        for (Object queryServiceResult : queryServiceResults) {
            Hashtable hs = (Hashtable) queryServiceResult;
            String scriptType = (String) hs.get(QaScriptView.SCRIPT_TYPE);
            if (scriptType == null) {
                scriptType = "xsd";
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
    public QueryService getQueryService() {
        if (queryService == null) {
            queryService = new QueryService();
        }
        return queryService;
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

    protected void addObligationsFiles(HashMap<String,List<String>> map,String envelopeUrl) throws XMLConvException{
        List<String> obligationUrls = extractObligationUrlsFromEnvelopeUrl(envelopeUrl);
        for (String obligationUrl: obligationUrls
             ) {
            if(obligationUrl!=null && !obligationUrl.isEmpty())    {
                List<String> obligation = new ArrayList<>();
                obligation.add(envelopeUrl+"/xml");
                map.put(obligationUrl,obligation);
            }
        }
    }

    @Override
    public Schema getSchemaBySchemaUrl(String schemaUrl) throws Exception {
        Schema schema = null;
        try {
            schema = schemaDao.getSchemaBySchemaUrl(schemaUrl);
        } catch (Exception e) {
            throw new Exception("Could not retrieve schema information for schema url " + schemaUrl);
        }
        return schema;
    }

    public JobRequestHandlerService getJobRequestHandlerService() {
        return jobRequestHandlerService;
    }

    public JobResultHandlerService getJobResultHandlerService() {
        return jobResultHandlerService;
    }

    public RunScriptAutomaticService getRunScriptAutomaticService() {
        return runScriptAutomaticService;
    }
}
