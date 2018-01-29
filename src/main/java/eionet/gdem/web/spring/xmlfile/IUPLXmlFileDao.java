/*
 * Created on 16.11.2007
 */
package eionet.gdem.web.spring.xmlfile;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Database object interface for uploaded XML files.
 *
 * @author Enriko Käsper (TietoEnator)
 *
 */
public interface IUPLXmlFileDao {
    /**
     * returns all uploaded xml files
     *
     * @return Vector containing all fields as HashMap from UPL_XMLFILE table
     */

    Vector getUplXmlFile() throws SQLException;

    /**
     * Adds a new uploaded xml file to the database
     *
     * @param name
     *            - xml file name
     * @return The ID of the added xml file
     */
    String addUplXmlFile(String name, String description) throws SQLException;

    /**
     * Removes the uploaded xml file from the uploaded xml file table
     *
     * @param uplXmlFileId
     *            - file Id
     */
    void removeUplXmlFile(String uplXmlFileId) throws SQLException;

    /**
     * returns xml file name for requested xml file id
     *
     * @param uplXmlFileId
     * @return
     * @throws SQLException
     */
    String getUplXmlFileName(String uplXmlFileId) throws SQLException;

    /**
     * returns a UplXmlFile object with specified ID
     *
     * @param uplXmlFileId
     * @return
     * @throws SQLException
     */
    Hashtable getUplXmlFileById(String uplXmlFileId) throws SQLException;

    /**
     * updates XML file object properties
     *
     * @param uplXmlFileId
     * @param title
     * @param fileName
     * @throws SQLException
     */
    void updateUplXmlFile(String uplXmlFileId, String title, String fileName) throws SQLException;

    /**
     * check if there are any XML files stored in DB with specified name. XML file name should be unique.
     *
     * @param xmlFileName
     * @return
     * @throws SQLException
     */
    boolean checkUplXmlFile(String xmlFileName) throws SQLException;
}
