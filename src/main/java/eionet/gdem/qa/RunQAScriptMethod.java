package eionet.gdem.qa;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.SourceFileManager;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.RemoteServiceMethod;
import eionet.gdem.dto.Schema;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import eionet.gdem.validation.ValidationService;

/**
 * Implementation of run ad-hoc QA script methods.
 *
 * @author Enriko Käsper, TripleDev
 */
public class RunQAScriptMethod extends RemoteServiceMethod {

    /** Query ID property key in ListQueries method result. */
    public static final String KEY_QUERY_ID = "query_id";
    /** Query file property key in ListQueries method result. */
    public static final String KEY_QUERY = "query";
    /** Query short name property key in ListQueries method result. */
    public static final String KEY_SHORT_NAME = "short_name";
    /** Query description property key in ListQueries method result. */
    public static final String KEY_DESCRIPTION = "description";
    /** Schema ID property key in ListQueries method result. */
    public static final String KEY_SCHEMA_ID = "schema_id";
    /** Schema URL property key in ListQueries method result. */
    public static final String KEY_XML_SCHEMA = "xml_schema";
    /** Type property key in ListQueries method result. */
    public static final String KEY_TYPE = "type";
    /** Output content type property key in ListQueries method result. */
    public static final String KEY_CONTENT_TYPE_OUT = "content_type_out";
    /** Output content type ID property key in ListQueries method result. */
    public static final String KEY_CONTENT_TYPE_ID = "content_type_id";
    /** XML file upper limit property key in ListQueries method result. */
    public static final String KEY_UPPER_LIMIT = "upper_limit";
    /** Upper limit for xml file size to be sent to manual QA. */
    public static final int VALIDATION_UPPER_LIMIT = Properties.qaValidationXmlUpperLimit;

    /** QA script default output content type. */
    public static final String DEFAULT_CONTENT_TYPE = "text/html";

    /** Business logic class for XML Schemas. */
    private SchemaManager schManager = new SchemaManager();
    /** DAO for getting query info. */
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();

    /** */
    private static final Log LOGGER = LogFactory.getLog(RunQAScriptMethod.class);

    /**
     * Remote method for running the QA script on the fly.
     *
     * @param sourceUrl URL of the soucre XML
     * @param scriptId XQueryScript ID or -1 (XML Schema validation) to be processed
     * @return Vector of 2 fields: content type and byte array
     * @throws GDEMException in case of business logic error
     */
    public Vector runQAScript(String sourceUrl, String scriptId) throws GDEMException {

        Vector result = new Vector();
        String fileUrl = null;
        String contentType = DEFAULT_QA_CONTENT_TYPE;
        String strResult = null;
        LOGGER.debug("==xmlconv== runQAScript: id=" + scriptId + " file_url=" + sourceUrl + "; ");
        try {
            // get the trusted URL from source file adapter
            fileUrl = SourceFileManager.getSourceFileAdapterURL(getTicket(), sourceUrl, isTrustedMode());
        } catch (Exception e) {
            String errMess = "File URL is incorrect";
            LOGGER.error(errMess + "; " + e.toString(), e);
            throw new GDEMException(errMess, e);
        }
        if (scriptId.equals(String.valueOf(Constants.JOB_VALIDATION))) {
            try {
                ValidationService vs = new ValidationService();
                strResult = vs.validate(fileUrl);
            } catch (Exception e) {
                String errMess = "Could not execute runQAMethod";
                LOGGER.error(errMess + "; " + e.toString());
                throw new GDEMException(errMess, e);
            }
        } else {
            String[] pars = new String[1];
            pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + fileUrl;

            try {
                String xqScript = queryDao.getQueryText(scriptId);
                HashMap hash = queryDao.getQueryInfo(scriptId);
                String schemaId = (String) hash.get("schema_id");
                Schema schema = null;
                // check because ISchemaDao.getSchema(null) returns first schema
                if (schemaId != null) {
                    schema = schManager.getSchema(schemaId);
                }

                if (Utils.isNullStr(xqScript) || hash == null) {
                    String errMess = "Could not find QA script with id: " + scriptId;
                    LOGGER.error(errMess);
                    throw new GDEMException(errMess, new Exception());
                } else {
                    if (!Utils.isNullStr((String) hash.get("meta_type"))) {
                        contentType = (String) hash.get("meta_type");
                    }
                    LOGGER.debug("Script: " + xqScript);
                    XQScript xq = new XQScript(xqScript, pars, (String) hash.get("content_type"));
                    xq.setScriptType((String) hash.get("script_type"));
                    xq.setSrcFileUrl(fileUrl);
                    xq.setSchema(schema);

                    strResult = xq.getResult();
                }
            } catch (SQLException sqle) {
                throw new GDEMException("Error getting data from DB: " + sqle.toString());
            } catch (Exception e) {
                String errMess = "Could not execute runQAMethod";
                LOGGER.error(errMess + "; " + e.toString(), e);
                throw new GDEMException(errMess, e);
            }
        }
        if (isHttpRequest()) {
            try {
                HttpMethodResponseWrapper httpResponse = getHttpResponse();
                httpResponse.setContentType(contentType);
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setContentDisposition("qaresult.xml");
                OutputStream outstream = httpResponse.getOutputStream();
                IOUtils.write(strResult, outstream, "UTF-8");
            } catch (IOException e) {
                LOGGER.error("Error getting response outputstream ", e);
                throw new GDEMException("Error getting response outputstream " + e.toString(), e);
            }
        } else {
            result.add(contentType);
            result.add(strResult.getBytes());

            HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromStr(strResult);
            result.add(fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM).getBytes());
            result.add((fbResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM).getBytes()));

        }
        return result;
    }
}
