package eionet.gdem.services.db.dao.mysql;


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
import eionet.gdem.services.db.dao.IXFBrowserDao;
import eionet.gdem.services.db.dao.IXFormDao;
import eionet.gdem.services.db.dao.IXQJobDao;





public class MySqlDaoFactory extends DCMDaoFactory{

	
	
	public IStyleSheetDao getStyleSheetDao(){
		return new StyleSheetMySqlDao();
	}    

	public IXFormDao getXFormDao(){
		return new XFormMySqlDao();
	}
	
	public IHostDao getHostDao(){
		return new HostMySqlDao();
	}
	
	public IQueryDao getQueryDao(){
		return new QueryMySqlDao();
	}
    
	public IRootElemDao getRootElemDao(){
		return new RootElemMySqlDao();
	}
	
	public IUPLSchemaDao getUPLSchemaDao(){
		return new UPLSchemaMySqlDao();
	}
	
	public IXFBrowserDao getXFBrowserDao(){
		return new XFBrowserMySqlDao();
	}
	
	public ISchemaDao getSchemaDao(){
		return new SchemaMySqlDao();
	}
	
	public IXQJobDao getXQJobDao(){
		return new XQJobMySqlDao();
	}

	public IConvTypeDao getConvTypeDao(){
		return new ConvTypeMySqlDao();
	}

	public IUPLXmlFileDao getUPLXmlFileDao() {
		return new UplXmlFileMySqlDao();
	}	

	public IBackupDao getBackupDao() {
		return new BackupMySqlDao();
	}	
	
	
	
}
