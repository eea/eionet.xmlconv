package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

public interface IConvTypeDao extends IDbSchema {

    /**
     * returns all records from t_STYLESHEET WHERE XML_SCHEMA=xmlSchema
     */
    public Vector listConversions(String xmlSchema) throws SQLException;

    public Vector getConvTypes() throws SQLException;

    /**
     * returns one row of conversion type from database
     * 
     * @param conv_type
     *            as string
     * @return HashMap containing all fields as HashMap from T_CONVTYPE table
     */

    public Hashtable getConvType(String conv_type) throws SQLException;

}
