package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Vector;

public interface IXFBrowserDao extends IDbSchema {

    /**
     * returns XForms capable browser types.
     *
     * @return Vector contining all fields from BROWSER table
     */

    public Vector getBrowsers() throws SQLException;

}
