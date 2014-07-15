package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.List;

import eionet.gdem.dto.Stylesheet;

public interface IStyleSheetDao extends IDbSchema {

    /**
     * Updates stylesheet properties in database.
     * @param stylehseet Stylesheet object.
     * @throws SQLException in case of database exception
     */
    public void updateStylesheet(Stylesheet stylehseet) throws SQLException;

    /**
     * Adds new stylesheet into database.
     * @param stylehseet Stylesheet object.
     * @throws SQLException in case of database exception
     */
    public String addStylesheet(Stylesheet stylehseet) throws SQLException;

    /**
     * Deletes the stylesheet from the stylesheets table.
     *
     * @param convertId - The identifier of the stylesheet.
     * @throws SQLException in case of database exception
     */
    public void deleteStylesheet(String convertId) throws SQLException;

    /**
     * Check if stylesheet file with given name exists in the database.
     * @param xslFileName Stylesheet file name
     * @return true if file exists
     * @throws SQLException in case of database exception
     */
    public boolean checkStylesheetFile(String xslFileName) throws SQLException;

    /**
     * Check if stylesheet file with given name and id exists in the database.
     * @param xslId Stylesheet ID in database table.
     * @param xslFileName Stylesheet file name
     * @return true if file exists
     * @throws SQLException in case of database exception
     */
    public boolean checkStylesheetFile(String xslId, String xslFileName) throws SQLException;

    /**
     * Get all stylesheets stored in database.
     * @return List of Stylesheet objects.
     */
    public List<Stylesheet> getStylesheets();

    /**
     * Get stylesheet object by unique numeric ID.
     * @param convertId stylesheet ID.
     * @return Stylehseet DTo object.
     */
    public Stylesheet getStylesheet(String convertId);
}
