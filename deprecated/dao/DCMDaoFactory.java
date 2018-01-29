package eionet.gdem.services.db.dao;

import eionet.gdem.qa.IQueryDao;
import eionet.gdem.services.db.dao.mysql.MySqlDaoFactory;
import eionet.gdem.web.spring.conversions.IConvTypeDao;
import eionet.gdem.web.spring.conversions.IStyleSheetDao;
import eionet.gdem.web.spring.hosts.IHostDao;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.schemas.IUPLSchemaDao;
import eionet.gdem.web.spring.scripts.IBackupDao;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.web.spring.xmlfile.IUPLXmlFileDao;

/**
 * DCM Dao Factory.
 * @author Unknown
 * @author George Sofianos
 */
public abstract class DCMDaoFactory {

    public static final int MYSQL_DB = 1;

    /**
     * Returns factory
     * @param whichFactory Factory selector
     * @return Selected factory
     */
    public static DCMDaoFactory getDaoFactory(int whichFactory) {
        switch (whichFactory) {
            case MYSQL_DB:
                return new MySqlDaoFactory();
            default:
                return null;
        }
    }

    /**
     * Gets stylesheet dao.
     * @return stylesheet dao
     */
    public abstract IStyleSheetDao getStyleSheetDao();

    /**
     * Gets conversion type dao.
     * @return conversion type dao.
     */
    public abstract IConvTypeDao getConvTypeDao();

    /**
     * Gets Host Dao
     * @return host dao.
     */
    public abstract IHostDao getHostDao();

    /**
     * Gets query dao.
     * @return Query dao.
     */
    public abstract IQueryDao getQueryDao();

    /**
     * Gets root element dao.
     * @return Root element dao.
     */
    public abstract IRootElemDao getRootElemDao();

    /**
     * Gets uploaded schema dao.
     * @return Uploaded schema dao.
     */
    public abstract IUPLSchemaDao getUPLSchemaDao();

    /**
     * Gets schema dao.
     * @return Schema dao.
     */
    public abstract ISchemaDao getSchemaDao();

    /**
     * Gets XQ Job Dao
     * @return XQ Job Dao.
     */
    public abstract IXQJobDao getXQJobDao();

    /**
     * Gets uploaded XML file dao.
     * @return uploaded XML file dao.
     */
    public abstract IUPLXmlFileDao getUPLXmlFileDao();

    /**
     * Gets backup dao.
     * @return backup dao.
     */
    public abstract IBackupDao getBackupDao();
}
