package eionet.gdem.web.spring.conversions;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.dto.ConversionDto;

/**
 * Conversion type Dao.
 * @author Unknown
 * @author George Sofianos
 */
public interface IConvTypeDao {

    /**
     * returns all records from t_STYLESHEET WHERE XML_SCHEMA=xmlSchema.
     */
    Vector<ConversionDto> listConversions(String xmlSchema) throws SQLException;

    /**
     * Gets conversion types
     * @return conversion types.
     * @throws SQLException If an error occurs.
     */
    Vector getConvTypes() throws SQLException;

    /**
     * returns one row of conversion type from database.
     *
     * @param conv_type
     *            as string
     * @return HashMap containing all fields as HashMap from T_CONVTYPE table
     * @throws SQLException If an error occurs.
     */

    Hashtable getConvType(String conv_type) throws SQLException;

}
