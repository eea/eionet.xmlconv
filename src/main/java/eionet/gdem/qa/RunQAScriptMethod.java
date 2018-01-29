package eionet.gdem.qa;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.validation.JaxpValidationService;
import org.apache.commons.io.IOUtils;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.RemoteServiceMethod;
import eionet.gdem.dto.Schema;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import eionet.gdem.validation.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of run ad-hoc QA script methods.
 *
 * @author Enriko Käsper, TripleDev
 */
public class RunQAScriptMethod extends RemoteServiceMethod {

    /**
     * Query ID property key in ListQueries method result.
     */
    public static final String KEY_QUERY_ID = "query_id";
    /**
     * Query file property key in ListQueries method result.
     */
    public static final String KEY_QUERY = "query";
    /**
     * Query short name property key in ListQueries method result.
     */
    public static final String KEY_SHORT_NAME = "short_name";
    /**
     * Query description property key in ListQueries method result.
     */
    public static final String KEY_DESCRIPTION = "description";
    /**
     * Schema ID property key in ListQueries method result.
     */
    public static final String KEY_SCHEMA_ID = "schema_id";
    /**
     * Schema URL property key in ListQueries method result.
     */
    public static final String KEY_XML_SCHEMA = "xml_schema";
    /**
     * Type property key in ListQueries method result.
     */
    public static final String KEY_TYPE = "type";
    /**
     * Output content type property key in ListQueries method result.
     */
    public static final String KEY_CONTENT_TYPE_OUT = "content_type_out";
    /**
     * Output content type ID property key in ListQueries method result.
     */
    public static final String KEY_CONTENT_TYPE_ID = "content_type_id";
    /**
     * XML file upper limit property key in ListQueries method result.
     */
    public static final String KEY_UPPER_LIMIT = "upper_limit";
    /**
     * Upper limit for xml file size to be sent to manual QA.
     */
    public static final int VALIDATION_UPPER_LIMIT = Properties.qaValidationXmlUpperLimit;

    /**
     * QA script default output content type.
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/html";

    /**
     * Business logic class for XML Schemas.
     */
    private SchemaManager schManager = new SchemaManager();
    /**
     * DAO for getting query info.
     */
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(RunQAScriptMethod.class);

    /**
     * Remote method for running the QA script on the fly.
     *
     * @param sourceUrl URL of the source XML
     * @param scriptId  XQueryScript ID or -1 (XML Schema validation) to be processed
     * @return Vector of 2 fields: content type and byte array
     * @throws XMLConvException in case of business logic error
     */
    public Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException {
        Vector result = new Vector();
        String fileUrl;
        String contentType = DEFAULT_QA_CONTENT_TYPE;
        String strResult;
        LOGGER.debug("==xmlconv== runQAScript: id=" + scriptId + " file_url=" + sourceUrl + "; ");
        try {
            if (scriptId.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                ValidationService vs = new JaxpValidationService();
                //vs.setTicket(getTicket());
                strResult = vs.validate(sourceUrl);
            } else {
                fileUrl = HttpFileManager.getSourceUrlWithTicket(getTicket(), sourceUrl, isTrustedMode());
                String[] pars = new String[1];
                pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + fileUrl;
                try {
                    HashMap hash = queryDao.getQueryInfo(scriptId);
                    String xqScript = "";
                    // If the script type is not FME, the script content is retrieved.
                    if (!XQScript.SCRIPT_LANG_FME.equals((String) hash.get(QaScriptView.SCRIPT_TYPE))) {
                        xqScript = queryDao.getQueryText(scriptId);
                    } else {
                        xqScript = XQScript.SCRIPT_LANG_FME; // Dummy value
                    }
                    String schemaId = (String) hash.get(QaScriptView.SCHEMA_ID);
                    Schema schema = null;
                    // check because ISchemaDao.getSchema(null) returns first schema
                    if (schemaId != null) {
                        schema = schManager.getSchema(schemaId);
                    }

                    if (Utils.isNullStr(xqScript) || hash == null) {
                        String errMess = "Could not find QA script with id: " + scriptId;
                        LOGGER.error(errMess);
                        throw new XMLConvException(errMess, new Exception());
                    } else {
                        if (!Utils.isNullStr((String) hash.get(QaScriptView.META_TYPE))) {
                            contentType = (String) hash.get(QaScriptView.META_TYPE);
                        }
                        LOGGER.debug("Script: " + xqScript);
                        XQScript xq = new XQScript(xqScript, pars, (String) hash.get(QaScriptView.CONTENT_TYPE));
                        xq.setScriptType((String) hash.get(QaScriptView.SCRIPT_TYPE));
                        xq.setSrcFileUrl(fileUrl);
                        xq.setSchema(schema);

                        if (XQScript.SCRIPT_LANG_FME.equals(xq.getScriptType())) {
                            xq.setScriptSource((String) hash.get(QaScriptView.URL));
                        }

                        strResult = xq.getResult();
                    }
                } catch (SQLException sqle) {
                    throw new XMLConvException("Error getting data from DB: " + sqle.toString());
                } catch (Exception e) {
                    String errMess = "Could not execute runQAMethod";
                    LOGGER.error(errMess + "; " + e.toString(), e);
                    throw new XMLConvException(errMess, e);
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
                    throw new XMLConvException("Error getting response outputstream " + e.toString(), e);
                }
            } else {
                result.add(contentType);
                result.add(strResult.getBytes());

                HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromStr(strResult);
                result.add(fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM).getBytes());
                result.add((fbResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM).getBytes()));

            }
        } catch (DCMException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }
}
