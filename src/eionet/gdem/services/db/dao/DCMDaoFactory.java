package eionet.gdem.services.db.dao;

import eionet.gdem.services.db.dao.mysql.MySqlDaoFactory;



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
