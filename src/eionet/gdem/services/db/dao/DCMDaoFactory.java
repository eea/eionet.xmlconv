package eionet.gdem.services.db.dao;

import eionet.gdem.services.db.dao.mysql.MySqlDaoFactory;
import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.services.db.dao.IXFormDao;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.services.db.dao.IUPLSchemaDao;
import eionet.gdem.services.db.dao.IXFBrowserDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IXQJobDao;



public abstract class DCMDaoFactory {

   public static final int MYSQL_DB = 1;


   public static DCMDaoFactory getDaoFactory(int whichFactory) {
      switch (whichFactory) {
         case MYSQL_DB:
            return new MySqlDaoFactory();
         default:
            return null;
      }
   }

   public abstract IStyleSheetDao getStyleSheetDao();    
   public abstract IXFormDao getXFormDao();    
   public abstract IConvTypeDao getConvTypeDao();    
   public abstract IHostDao getHostDao();    
   public abstract IQueryDao getQueryDao();    
   public abstract IRootElemDao getRootElemDao();    
   public abstract IUPLSchemaDao getUPLSchemaDao();    
   public abstract IXFBrowserDao getXFBrowserDao();    
   public abstract ISchemaDao getSchemaDao();    
   public abstract IXQJobDao getXQJobDao();    
   public abstract IUPLXmlFileDao getUPLXmlFileDao();    
   public abstract IBackupDao getBackupDao();    
   }
