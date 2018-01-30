package eionet.gdem.services;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.web.spring.scripts.IBackupDao;
import eionet.gdem.web.spring.conversions.IConvTypeDao;
import eionet.gdem.web.spring.hosts.IHostDao;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.conversions.IStyleSheetDao;
import eionet.gdem.web.spring.schemas.IUPLSchemaDao;
import eionet.gdem.web.spring.xmlfile.IUPLXmlFileDao;
import eionet.gdem.web.spring.workqueue.IXQJobDao;

/**
 * MySQL Dao factory.
 *
 * @author Unknown
 */
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
