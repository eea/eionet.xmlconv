package eionet.gdem.services.db.dao.mysql;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.services.db.dao.DCMDaoFactory;
import eionet.gdem.services.db.dao.IBackupDao;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.services.db.dao.IUPLSchemaDao;
import eionet.gdem.services.db.dao.IUPLXmlFileDao;
import eionet.gdem.services.db.dao.IXQJobDao;

public class MySqlDaoFactory extends DCMDaoFactory {

    @Override
    public IStyleSheetDao getStyleSheetDao() {
        return (IStyleSheetDao) SpringApplicationContext.getBean("stylehseetDao");
    }

    @Override
    public IHostDao getHostDao() {
        return (IHostDao) SpringApplicationContext.getBean("hostDao");
    }

    @Override
    public IQueryDao getQueryDao() {
        return (IQueryDao) SpringApplicationContext.getBean("queryDao");
    }

    @Override
    public IRootElemDao getRootElemDao() {
        return (IRootElemDao) SpringApplicationContext.getBean("rootElemDao");
    }

    @Override
    public IUPLSchemaDao getUPLSchemaDao() {
        return (IUPLSchemaDao) SpringApplicationContext.getBean("uplSchemaDao");
    }

    @Override
    public ISchemaDao getSchemaDao() {
        return (ISchemaDao) SpringApplicationContext.getBean("schemaDao");
    }

    @Override
    public IXQJobDao getXQJobDao() {
        return (IXQJobDao) SpringApplicationContext.getBean("xqJobDao");
    }

    @Override
    public IConvTypeDao getConvTypeDao() {
        return (IConvTypeDao) SpringApplicationContext.getBean("convTypeDao");
    }

    @Override
    public IUPLXmlFileDao getUPLXmlFileDao() {
        return (IUPLXmlFileDao) SpringApplicationContext.getBean("uplXmlFileDao");
    }

    @Override
    public IBackupDao getBackupDao() {
        return (IBackupDao) SpringApplicationContext.getBean("backupDao");
    }

}
