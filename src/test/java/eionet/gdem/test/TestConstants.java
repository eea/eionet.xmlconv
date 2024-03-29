/*
 * Created on 18.03.2008
 */
package eionet.gdem.test;

import eionet.gdem.Properties;
import org.junit.Ignore;

/**
 * The class holds different constant values used in test cases.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS TestConstants
 */

@Ignore
public class TestConstants {

    // SEED FILES with test data
    public static final String SEED_DATASET_CONVERSIONS_XML = "seed-dataset-conversions.xml";
    public static final String SEED_DATASET_UPL_SCHEMAS_XML = "seed-dataset-upl_schemas.xml";
    public static final String SEED_DATASET_QA_XML = "seed-dataset-qa.xml";
    public static final String SEED_DATASET_UPLXML_XML = "seed-dataset-uplxml.xml";
    public static final String SEED_DATASET_QAJOBS_XML = "seed-dataset-qajobs.xml";
    public static final String SEED_DATASET_HOSTS_XML = "seed-hosts.xml";
    public static final String SEED_DATASET_JOB_HISTORY_XML = "seed-dataset-jobHistory.xml";
    public static final String SEED_DATASET_INTERNAL_STATUS_XML = "seed-dataset-internalStatus.xml";
    public static final String SEED_DATASET_JOB_EXECUTOR_XML = "seed-dataset-jobExecutor.xml";
    public static final String SEED_DATASET_WORKER_HEART_BEAT_XML = "seed-dataset-worker-heart-beat.xml";
    public static final String SEED_DATASET_QUERY_METADATA_XML = "seed-dataset-query-metadata.xml";
    public static final String SEED_DATASET_PROPERTIES_XML = "seed-dataset-properties.xml";
    public static final String SEED_DATASET_SCRIPT_RULES_XML = "seed-dataset-script-rules.xml";
    public static final String SEED_ALERTS_XML = "seed-alerts.xml";
    public static final String SEED_SCHEMAS_XML = "seed-dataset-schemas.xml";

    // SEED FILES for conversions
    public static final String SEED_READER_XLS = "seed-reader.xls";
    public static final String SEED_RIVERS_XLS = "seed-rivers.xls";
    public static final String SEED_DATES_XLS = "seed-dates.xls";
    public static final String SEED_MULTIVALUES_XLS = "seed-multivalues.xls";
    public static final String SEED_FORMULAS_XLS = "seed-formulas.xls";
    public static final String SEED_VALIDATION_WARNINGS_XLS = "seed-validation-warnings.xls";
    public static final String SEED_MULTIVALUES_XLSX = "seed-multivalues07.xlsx";
    public static final String SEED_FORMULAS_XLSX = "seed-formulas07.xlsx";

    public static final String AQD_SCHEMALOCATION = "xmlfile/aqd-schemalocation.xml";
    public static final String SEED_MULTIVALUES_ODS = "seed-multivalues.ods";
    public static final String SEED_FORMULAS_ODS = "seed-formulas.ods";
    public static final String SEED_GENERAL_REPORT_XML = "seed-general-report.xml";
    public static final String SEED_GENERAL_REPORT_ZIP = "seed-general-report.zip";
    public static final String SEED_OZONE_STATION_XML = "seed-ozone-station.xml";
    public static final String SEED_GW_VALID_XML = "seed-gw-valid.xml";
    public static final String SEED_GW_INVALID_XML = "seed-gw-invalid.xml";
    public static final String SEED_GW_SCHEMA = "schema/seed-gw-schema.xsd";
    public static final String SEED_GW_CONTAINER_SCHEMA = "schema/seed-gw-schema-container.xsd";
    public static final String SEED_XLIFF_DTD = "schema/xliff.dtd";
    public static final String SEED_XLIFF_XML = "seed-xliff.xml";
    public static final String SEED_XLIFF2_XML = "seed-xliff-2.xml";
    public static final String SEED_GENERALREPORT_SCHEMA = "seed-generalreport.xsd";
    public static final String SEED_GENERALREPORT_SCHEMA_UPD = "seed-generalreportUPD.xsd";
    public static final String SEED_QASCRIPT_XQUERY = "seed-qascript.xquery";
    public static final String SEED_QASCRIPT_XQUERY2 = "seed-qascript2.xquery";
    public static final String SEED_QASCRIPT_TEST = "test.xquery";
    public final static String SEED_FEEDBACKANALYZE_TEST  = "seed-feedback-error.html";
    public static final String SEED_XSLSCRIPT_TEST = "seed-art17-general.xsl";

    // TEST RESULT PROPERTIES
    public final static String HTML_CONTENTYPE_RESULT = "text/html;charset=UTF-8";
    public final static String TEXT_CONTENTYPE_RESULT = "text/plain";
    public final static String EXCEL_CONTENTYPE_RESULT = "application/vnd.ms-excel";
    public final static String ZIP_CONTENTYPE_RESULT = "application/x-zip-compressed";
    public final static String XML_CONTENTYPE_RESULT = "text/xml";
    public final static String GR_HTML_FILENAME_RESULT = "seed-general-report.html";
    public final static String OZ_HTML_FILENAME_RESULT = "seed-ozone-station.html";
    public final static String OZ_SQL_FILENAME_RESULT = "seed-ozone-station.sql";
    public final static String STRCONTENT_RESULT = "Conversion works!";

    // USER PROPERTIES
    public final static String TEST_USER = "tester";
    public final static String TEST_ADMIN_USER = "roug";
    public static final String ADMIN_USER = "roug";
    public static final String NORMAL_USER = "test";

    public static final String SESSION_USER = "user";

    //on demand jobs handle results properties
    public static final String ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_STATUS = "FATAL ERROR";
    public static final String ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_MESSAGE = "Script execution failed, e.g. due to memory problems or unresponsive third party web service.";
    public static final String ON_DEMAND_JOBS_EMPTY_CONTENT_FEEDBACK_CONTENT = "";


}
