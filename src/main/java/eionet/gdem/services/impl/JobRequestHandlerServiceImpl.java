package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.ListQueriesMethod;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.rabbitMQ.errors.CreateMQMessageException;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageFactory;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.conversions.IConvTypeDao;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eionet.gdem.services.JobRequestHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static eionet.gdem.Constants.JOB_VALIDATION;
import static eionet.gdem.qa.ListQueriesMethod.DEFAULT_CONTENT_TYPE_ID;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service("jobRequestHandlerService")
public class JobRequestHandlerServiceImpl extends RemoteService implements JobRequestHandlerService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRequestHandlerService.class);

    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    private SchemaManager schManager = new SchemaManager();

    XQueryService xQueryService;

    @Autowired
    public JobRequestHandlerServiceImpl() {
        xQueryService = new XQueryService();
    }

    /**
     * This method is copied from XQueryService public Vector analyzeXMLFiles(Hashtable files) throws XMLConvException {
     *
     * @param filesAndSchemas - Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     * @throws XMLConvException If an error occurs.
     */
    @Override
    public HashMap analyzeMultipleXMLFiles(HashMap<String, List<String>> filesAndSchemas) throws XMLConvException {

        HashMap result = new HashMap();

        if (filesAndSchemas == null) {
            return result;
        }

        for (Map.Entry<String, List<String>> entry : filesAndSchemas.entrySet()) {
            String schema = entry.getKey();
            List<String> fileList = entry.getValue();
            if (Utils.isNullList(fileList)) {
                continue;
            }

            for (String file: fileList){
                // get all possible xqueries from db
                String newId = "-1"; // should not be returned with value -1;

                List<Hashtable> queries = xQueryService.listQueries(schema);

                if (!Utils.isNullList(queries)) {
                    for(Hashtable ht: queries){
                        String query_id = String.valueOf(ht.get( ListQueriesMethod.KEY_QUERY_ID ));
                        newId = analyzeSingleXMLFile( file, query_id , schema );
                        result.put(newId, file);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String analyzeSingleXMLFile(String sourceURL, String scriptId, String schema) throws XMLConvException{
        String jobId = "-1";
        HashMap query;
        String originalSourceURL = sourceURL;

        try {

            if ( String.valueOf(Constants.JOB_VALIDATION).equals(scriptId )  ){ // Validation
                query = createMapForJobValidation(sourceURL, schema);
            }
            else{
                query = queryDao.getQueryInfo( scriptId);
            }

            if (isNull(  query ) ){
                throw new XMLConvException("Script ID does not exist");
            }
            if ( "0".equals(query.get(QaScriptView.IS_ACTIVE) )){
                throw new XMLConvException("Script is not active");
            }

            Vector outputTypes = convTypeDao.getConvTypes();

            String query_id = String.valueOf(query.get(QaScriptView.QUERY_ID));
            String queryFile = (String) query.get(QaScriptView.QUERY);
            String contentType = (String) query.get(QaScriptView.CONTENT_TYPE_ID);
            String scriptType = (String) query.get(QaScriptView.SCRIPT_TYPE);
            String fileExtension = getExtension(outputTypes, contentType);
            String resultFile =
                    eionet.gdem.Properties.tmpFolder + File.separatorChar + "gdem_q" + query_id + "_" + System.currentTimeMillis() + "."
                            + fileExtension;

            int queryId;
            try {
                queryId = Integer.parseInt(query_id);
            } catch (NumberFormatException n) {
                queryId = 0;
            }
            // if it is a XQuery script, then append the system folder
            if (queryId != JOB_VALIDATION
                    && queryFile.startsWith(eionet.gdem.Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile =
                        Utils.Replace(queryFile, eionet.gdem.Properties.gdemURL + "/" + Constants.QUERIES_FOLDER,
                                eionet.gdem.Properties.queriesFolder + File.separator);
            }
            else if (queryId != JOB_VALIDATION
                    && ! queryFile.startsWith(eionet.gdem.Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile = eionet.gdem.Properties.queriesFolder + File.separator + queryFile;
            }

            sourceURL = HttpFileManager.getSourceUrlWithTicket(getTicket(), sourceURL, isTrustedMode());

            long sourceSize = HttpFileManager.getSourceURLSize(getTicket(), originalSourceURL, isTrustedMode());
            LOGGER.info("### File with size=" + sourceSize + " Bytes has been downloaded.");
            //save the job definition in the DB
            jobId = xqJobDao.startXQJob(sourceURL, queryFile, resultFile, queryId ,scriptType);
            LOGGER.debug( jobId + " : " + sourceURL + " size: " + sourceSize );
            LOGGER.info("### Job with id=" + jobId + " has been created.");

            if (Properties.enableQuartz) {
                xQueryService.scheduleJob(jobId, sourceSize, scriptType);
                LOGGER.info("### Job with id=" + jobId + " has been scheduled.");
                getJobHistoryRepository().save(new JobHistoryEntry(jobId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, queryFile, resultFile, scriptType));
                LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
            } else {
                getJobHistoryRepository().save(new JobHistoryEntry(jobId, Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), sourceURL, queryFile, resultFile, scriptType));
                LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
                getRabbitMQMessageFactory().createScriptAndSendMessageToRabbitMQ(jobId);
                LOGGER.info("### Job with id=" + jobId + " has been scheduled.");
            }
        } catch (SQLException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (SchedulerException | CreateMQMessageException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.error("AnalyzeXMLFile:" , e);
            throw new XMLConvException(e.getMessage());
        }
        return jobId;
    }

    private HashMap<String,String> createMapForJobValidation(String sourceFileURL, String schema) throws XMLConvException {
        HashMap<String,String> query = new HashMap();
        if ( isEmpty(schema)){
            InputAnalyser analyser = new InputAnalyser();
            try {
                analyser.parseXML(sourceFileURL);
                schema = analyser.getSchemaOrDTD();
            } catch (Exception e) {
                throw new XMLConvException("Could not extract schema");
            }
            query.put( QaScriptView.QUERY, schema);
        }
        query.put(QaScriptView.QUERY , schema);
        query.put( QaScriptView.QUERY_ID , "-1");
        query.put( QaScriptView.CONTENT_TYPE, DEFAULT_CONTENT_TYPE_ID);
        query.put( QaScriptView.SCRIPT_TYPE, "xsd");
        return query;
    }

    private JobHistoryRepository getJobHistoryRepository() {
        return (JobHistoryRepository) SpringApplicationContext.getBean("jobHistoryRepository");
    }

    private RabbitMQMessageFactory getRabbitMQMessageFactory() {
        return (RabbitMQMessageFactory) SpringApplicationContext.getBean("createJob");
    }

    /**
     * Gets file extension
     * @param outputTypes Output Types
     * @param content_type Content type
     * @return Extension
     */
    private String getExtension(Vector outputTypes, String content_type) {
        String ret = "html";
        if (outputTypes == null) {
            return ret;
        }
        if (content_type == null) {
            return ret;
        }

        for (int i = 0; i < outputTypes.size(); i++) {
            Hashtable outType = (Hashtable) outputTypes.get(i);
            if (outType == null) {
                continue;
            }
            if (!outType.containsKey("conv_type") || !outType.containsKey("file_ext") || outType.get("conv_type") == null
                    || outType.get("file_ext") == null) {
                continue;
            }
            String typeId = (String) outType.get("conv_type");
            if (!content_type.equalsIgnoreCase(typeId)) {
                continue;
            }
            ret = (String) outType.get("file_ext");
        }
        return ret;
    }

}
