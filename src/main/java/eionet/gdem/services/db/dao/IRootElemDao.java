package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Vector;

public interface IRootElemDao extends IDbSchema {

    public Vector getSchemaRootElems(String schemaId) throws SQLException;

    /**
     * find possible schema matching for given root element and namespace.
     *
     * @param rootElem root element name
     * @param namespace
     * @return Vector containing HashMaps with schema info (same as getSchemas)
     */
    public Vector getRootElemMatching(String rootElem, String namespace) throws SQLException;

    /**
     * Removes the root element mapping from the root element table
     *
     * @param rootElemId root element ID
     */
    public void removeRootElem(String rootElemId) throws SQLException;

    /**
     * Adds a new root element mapping to the database.
     *
     * @param xmlSchemaID
     *            - xml schema ID
     * @param elemName
     *            - root element name
     * @param namespcae
     *            - namespace of the root element
     * @return The ID of the added rootElement
     */
    public String addRootElem(String xmlSchemaID, String elemName, String namespace) throws SQLException;

}
