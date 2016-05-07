/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is XMLCONV - Converters and QA Services
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s): Enriko Käsper
 */

package eionet.gdem.dcm.business;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.exceptions.DCMException;
import org.apache.log4j.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Content Registry SPARQL endpoint client. The class executes SPARQL queries in CR eg. search XML files by XML Schema from CR.
 *
 * @author Enriko Käsper
 */
public class CrServiceSparqlClient {

    /**
     * Content Registry SPARQL endpoint URL.
     */
    private static String endpointURL = Properties.crSparqlEndpoint;

    private static final Logger LOGGER = Logger.getLogger(CrServiceSparqlClient.class);

    /**
     * List of xml files for testing purposes
     */
    private static List<CrFileDto> mockXmlFiles = null;

    /**
     * Search XML files through CR SPARQL endpoint.
     *
     * @param schema
     *            XML schema
     * @return the list of CR file objects
     * @throws DCMException If an error occurs
     */
    public static List<CrFileDto> getXmlFilesBySchema(String schema) throws DCMException {

        List<CrFileDto> result = new ArrayList<CrFileDto>();

        RepositoryConnection conn = null;
        SPARQLRepository crEndpoint = new SPARQLRepository(endpointURL);
        try {
            crEndpoint.initialize();

            conn = crEndpoint.getConnection();
            String query = getXmlFilesBySchemaQuery(schema);
            TupleQuery q = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult bindings = q.evaluate();
            for (int row = 0; bindings.hasNext(); row++) {
                BindingSet pairs = bindings.next();

                String file = pairs.getValue("file") == null ? "" : pairs.getValue("file").stringValue();
                String lastModified = pairs.getValue("lastModified") == null ? "" : pairs.getValue("lastModified").stringValue();

                CrFileDto crFile = new CrFileDto();
                crFile.setUrl(file);
                crFile.setLastModified(lastModified);

                result.add(crFile);
            }
        } catch (Exception e) {

            LOGGER.error("Operation failed while searching XML files from Content Registry. The following error was reported:\n"
                    + e.toString());
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL, "Error getting data from Content Registry " + e.toString());
        } finally {
            try {
                conn.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Gets SPARQL Query for fetching xml files by schema
     * @param schema XML Schema
     * @return Query result
     */
    private static String getXmlFilesBySchemaQuery(String schema) {
        StringBuilder query =
                new StringBuilder("PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> "
                        + "SELECT DISTINCT ?file, ?lastModified WHERE { ?file cr:xmlSchema <");
        query.append(schema);
        query.append("> . OPTIONAL { ?file cr:contentLastModified ?lastModified } } ORDER BY ?file");

        return query.toString();
    }

    /**
     * The testing purposes
     * TODO check possibility of replacing this.
     * @param schemaUrl Schema URL
     * @return Result XML files
     */
    public static List<CrFileDto> getMockXmlFilesBySchema(String schemaUrl) {
        return mockXmlFiles;
    }

    public static void setMockXmlFilesBySchema(List<CrFileDto> mockXmlFiles) {
        CrServiceSparqlClient.mockXmlFiles = mockXmlFiles;
    }
}
