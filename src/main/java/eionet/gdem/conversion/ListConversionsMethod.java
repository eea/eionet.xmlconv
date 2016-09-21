/*
 * Created on 19.02.2008
 */
package eionet.gdem.conversion;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;




import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.dcm.remote.RemoteServiceMethod;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.dto.DDDatasetTable;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of listConversions method.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class ListConversionsMethod extends RemoteServiceMethod {

    /** Conversion ID property key in ListConversions method result. */
    public static final String KEY_CONVERT_ID = "convert_id";
    /** URL to XSL stylesheet property in ListConversions method result. */
    public static final String KEY_XSL = "xsl";
    /** Conversion description name property in ListConversions method result. */
    public static final String KEY_DESCRIPTION = "description";
    /** Conversion output content type property in ListConversions method result. */
    public static final String KEY_CONTENTTYPE_OUT = "content_type_out";
    /** Conversion output result type property in ListConversions method result. */
    public static final String KEY_RESULT_TYPE = "result_type";
    /** XML Schema URL property in ListConversions method result. */
    public static final String KEY_XML_SCHEMA = "xml_schema";

    /** */
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertXMLMethod.class);

    /**
     * List available conversions for given schema. If schema is not given as a parameter, then all possible conversions will be
     * returned.
     *
     * @param schema
     *            XML Schema URL for which conversions will be returned.
     * @return List of conversions.
     * @throws XMLConvException
     *             Error occurred on reading data from database or from Data Dictionary.
     */
    public Vector<Hashtable<String, String>> listConversions(String schema) throws XMLConvException {
        Vector<Hashtable<String, String>> v = new Vector<Hashtable<String, String>>();
        List<DDDatasetTable> ddTables = DDServiceClient.getDDTables();
        List<ConversionDto> generatedConversionTypes = Conversion.getConversions();
        Set<String> handcodedConvTypes = new HashSet<String>();

        // retrieving hand-coded conversions
        try {
            Vector<ConversionDto> dbConversions = convTypeDao.listConversions(schema);
            for (ConversionDto conversion : dbConversions) {
                v.add(getMapFromConversionObject(conversion));
                handcodedConvTypes.add(conversion.getResultType() + conversion.getXmlSchema());
            }
        } catch (Exception e) {
            LOGGER.error("Error getting data from the DB", e);
            throw new XMLConvException("Error getting data from the DB " + e.toString(), e);
        }
        // get generated conversions for given DD schema
        if (schema != null && schema.startsWith(Properties.ddURL) && ddTables != null) {
            String tblId = schema.substring(schema.indexOf("id=TBL") + 6, schema.length());
            if (isSchemaExistsInDD(ddTables, tblId)) {
                for (ConversionDto genConversion : generatedConversionTypes) {
                    if (!genConversion.isIgnoreGeneratedIfManualExists()
                            || !handcodedConvTypes.contains(genConversion.getResultType() + schema)) {
                        Hashtable<String, String> h = getMapForDDTable(genConversion, tblId, schema);
                        v.add(h);
                    }
                }
            }
        }
        // get generated conversions for all DD schemas
        if (schema == null && ddTables != null) {
            for (DDDatasetTable ddTable : ddTables) {
                String schemaUrl = Properties.ddURL + "/GetSchema?id=TBL" + ddTable.getTblId();
                for (ConversionDto genConversion : generatedConversionTypes) {
                    if (!genConversion.isIgnoreGeneratedIfManualExists()
                            || !handcodedConvTypes.contains(genConversion.getResultType() + schemaUrl)) {
                        Hashtable<String, String> h = getMapForDDTable(genConversion, ddTable.getTblId(), schemaUrl);
                        v.add(h);
                    }
                }
            }
        }
        return v;
    }

    /**
     * Get a distinct list of XML Schemas returned from listConversions() method.
     *
     * @return List of XML schemas.
     * @throws XMLConvException
     *             Error occurred on reading data from database.
     */
    public Vector<String> getXMLSchemas() throws XMLConvException {
        Vector<Hashtable<String, String>> conv = listConversions(null);
        Vector<String> schemas = new Vector<String>();

        for (int i = 0; i < conv.size(); i++) {
            Hashtable<String, String> schema = conv.get(i);
            if (!schemas.contains(schema.get("xml_schema"))) {
                schemas.add(schema.get("xml_schema"));
            }
        }
        return schemas;
    }

    /**
     * Check if listConversions method contains specified schema.
     *
     * @param xmlSchema XML Schema
     * @return
     * @throws XMLConvException
     *             System error.
     */
    public boolean existsXMLSchema(String xmlSchema) throws XMLConvException {
        List<String> schemas = getXMLSchemas();
        return schemas.contains(xmlSchema);
    }

    /**
     * Checks if the given dataset table ID exists in the list of schemas retrieved from Data Dictionary.
     *
     * @param ddTables
     *            List of DD schemas.
     * @param tblId Table id
     * @return true, if table ID exists in DD.
     */
    public boolean isSchemaExistsInDD(List<DDDatasetTable> ddTables, String tblId) {
        boolean existsInDD = false;
        for (DDDatasetTable ddTable : ddTables) {
            if (ddTable.getTblId() != null && ddTable.getTblId().equalsIgnoreCase(tblId)) {
                existsInDD = true;
                break;
            }
        }
        return existsInDD;
    }

    /**
     * Converts ConversionDto object to Hashtable with correct keys.
     *
     * @param conversionObject
     *            Conversion object contains info about stylesheet, conversion type and XML Shcema.
     * @return Map with correct keys.
     */
    protected Hashtable<String, String> getMapFromConversionObject(ConversionDto conversionObject) {

        if (conversionObject == null) {
            return null;
        }

        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put(KEY_CONVERT_ID, conversionObject.getConvId() == null ? "" : conversionObject.getConvId());
        h.put(KEY_XSL, conversionObject.getStylesheet() == null ? "" : conversionObject.getStylesheet());
        h.put(KEY_XML_SCHEMA, conversionObject.getXmlSchema() == null ? "" : conversionObject.getXmlSchema());
        h.put(KEY_CONTENTTYPE_OUT, conversionObject.getContentType() == null ? "" : conversionObject.getContentType());
        h.put(KEY_RESULT_TYPE, conversionObject.getResultType() == null ? "" : conversionObject.getResultType());
        h.put(KEY_DESCRIPTION, conversionObject.getDescription() == null ? "" : conversionObject.getDescription());

        return h;
    }

    /**
     * Create converion Map object for DD table.
     *
     * @param conversionObject
     *            Conversion object contains info about stylesheet, conversion type and XML Shcema.
     * @param tblId
     *            Data Dictionary table ID
     * @param schemaUrl
     *            URL of XML schema.
     * @return Map with correct keys.
     */
    protected Hashtable<String, String> getMapForDDTable(ConversionDto conversionObject, String tblId, String schemaUrl) {
        if (conversionObject == null) {
            return null;
        }
        Hashtable<String, String> h = getMapFromConversionObject(conversionObject);
        h.put(KEY_CONVERT_ID, "DD_TBL" + tblId + "_CONV" + conversionObject.getConvId());
        h.put(KEY_XSL, Properties.gdemURL + "/do/getStylesheet?id=" + tblId + "&conv=" + conversionObject.getConvId());
        h.put(KEY_XML_SCHEMA, schemaUrl == null ? "" : schemaUrl);

        return h;
    }
}
