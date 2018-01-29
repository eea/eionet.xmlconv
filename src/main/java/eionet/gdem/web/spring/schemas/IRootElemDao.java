package eionet.gdem.web.spring.schemas;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Root element DAO.
 * @author Unknown
 * @author George Sofianos
 */
public interface IRootElemDao {

    /**
     * Returns Schema root elements
     * @param schemaId Schema id
     * @return Root elements
     * @throws SQLException If an error occurs.
     */
    Vector getSchemaRootElems(String schemaId) throws SQLException;

    /**
     * find possible schema matching for given root element and namespace.
     *
     * @param rootElem root element name
     * @param namespace namespace
     * @return Vector containing HashMaps with schema info (same as getSchemas)
     * @throws SQLException If an error occurs.
     */
    Vector getRootElemMatching(String rootElem, String namespace) throws SQLException;

    /**
     * Removes the root element mapping from the root element table
     *
     * @param rootElemId root element ID
     * @throws SQLException If an error occurs.
     */
    void removeRootElem(String rootElemId) throws SQLException;

    /**
     * Adds a new root element mapping to the database.
     *
     * @param xmlSchemaID
     *            - xml schema ID
     * @param elemName
     *            - root element name
     * @param namespace
     *            - namespace of the root element
     * @return The ID of the added rootElement
     * @throws SQLException If an error occurs.
     */
    String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException;

}
