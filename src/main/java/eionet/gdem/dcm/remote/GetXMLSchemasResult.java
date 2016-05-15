/*
 * Created on 12.02.2008
 */
package eionet.gdem.dcm.remote;

import java.util.List;

/**
 * Converts the listConversions method result as XML.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 */

public class GetXMLSchemasResult extends XMLResultStrategy {

    public static final String SCHEMA_TAG = "schema";

    private List schemas = null;

    /**
     * Default constructor
     */
    public GetXMLSchemasResult() {
    }

    /**
     * Set the data
     *
     * @param list XML schema list
     */
    public void setResult(List list) {
        schemas = list;
    }

    /**
     * write data into XML
     */
    @Override
    protected void writeElements() throws Exception {
        if (schemas == null) {
            return;
        }
        for (int i = 0; i < schemas.size(); i++) {
            String schema = (String) schemas.get(i);
            writeSimpleElement(SCHEMA_TAG, schema);
        }
    }

}
