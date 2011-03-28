package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import eionet.gdem.services.db.dao.IXFBrowserDao;


public class XFBrowserMySqlDao extends MySqlBaseDao implements IXFBrowserDao {



/*	public Vector getBrowsers() throws SQLException {
        String sql = "SELECT " + BROWSER_ID_FLD + "," + BROWSER_TYPE_FLD + ", " + BROWSER_TITLE_FLD + ", " + BROWSER_STYLESHEET_FLD + ", " + BROWSER_PRIORITY_FLD + " FROM " + XFBROWSER_TABLE + " ORDER BY " + BROWSER_PRIORITY_FLD;

        String[][] r = _executeStringQuery(sql);

        Vector v = new Vector();

        for (int i = 0; i < r.length; i++) {

            HashMap h = new HashMap();
            h.put("browser_id", r[i][0]);
            h.put("browser_type", r[i][1]);
            h.put("browser_title", r[i][2]);
            h.put("stylesheet", r[i][3]);
            h.put("priority", r[i][4]);

            v.add(h);
        }
        return v;
    }
*/


    private static final String qBrowsers = "SELECT "
                                            + BROWSER_ID_FLD + ","
                                            + BROWSER_TYPE_FLD + ", "
                                            + BROWSER_TITLE_FLD + ", "
                                            + BROWSER_STYLESHEET_FLD + ", "
                                            + BROWSER_PRIORITY_FLD
                                            + " FROM " + XFBROWSER_TABLE
                                            + " ORDER BY " + BROWSER_PRIORITY_FLD;

    public XFBrowserMySqlDao(){}


    public Vector getBrowsers() throws SQLException
    {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs =null;
        Vector v = null;

        if (isDebugMode){ logger.debug("Query is " + qBrowsers);}

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qBrowsers);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {

                HashMap h = new HashMap();
                h.put("browser_id", r[i][0]);
                h.put("browser_type", r[i][1]);
                h.put("browser_title", r[i][2]);
                h.put("stylesheet", r[i][3]);
                h.put("priority", r[i][4]);

                v.add(h);
            }
        }
        finally {
            closeAllResources(rs,pstmt,conn);
        }

        return v;
    }
}
