
package eionet.gdem.services.db.dao;


public interface IDbSchema {


  /**
  * Table for storing file info in repository
  */
  public static final String FILE_TABLE="T_FILE";
  /**
  * Table for XForms browsers
  */
  public static final String XFBROWSER_TABLE="T_XFBROWSER";

  /**
  * Table for XQuery Workqueue
  */
  public static final String WQ_TABLE="T_XQJOBS";

  /**
  * Table for stylesheets in the DB
  */
  public static final String XSL_TABLE="T_STYLESHEET";

  /**
  * Table for queries in the DB
  */
  public static final String QUERY_TABLE="T_QUERY";

  /**
  * Table for xml schemas in the DB
  */
  public static final String SCHEMA_TABLE="T_SCHEMA";

  /**
   * Table for uploaded xml schemas in the DB
   */
   public static final String UPL_SCHEMA_TABLE="T_UPL_SCHEMA";
  
  
  /**
  * Table for root element mappings for schemas in the DB
  */
  public static final String ROOTELEM_TABLE="T_ROOT_ELEM";

  /**
  * Table for hosts with usernames and passwords in the DB
  */
  public static final String HOST_TABLE="T_HOST";

  /**
  * Table for conversion types
  */
  public static final String CONVTYPE_TABLE="T_CONVTYPE";
  /**
   * Table for backup files
   */
   public static final String BACKUP_TABLE="T_BACKUP";
  /**
  * Field names in XSL table
  */
  public static final String CNV_ID_FLD="CONVERT_ID";
  public static final String XSL_SCHEMA_ID_FLD="SCHEMA_ID";
  public static final String DESCR_FLD="DESCRIPTION";
  public static final String RESULT_TYPE_FLD="RESULT_TYPE";
  public static final String XSL_FILE_FLD="XSL_FILENAME"; 
  public static final String DEPENDS_ON="DEPENDS_ON"; 

  /**
  * Field names in QUERY table
  */
  public static final String QUERY_ID_FLD="QUERY_ID";
  public static final String SHORT_NAME_FLD="SHORT_NAME";
  public static final String QUERY_FILE_FLD="QUERY_FILENAME";  
  public static final String QUERY_RESULT_TYPE="RESULT_TYPE";
  public static final String QUERY_SCRIPT_TYPE="SCRIPT_TYPE";

  /**
  * Field names in SCHEMA table
  */
  public static final String SCHEMA_ID_FLD="SCHEMA_ID";
  public static final String XML_SCHEMA_FLD="XML_SCHEMA";
  public static final String SCHEMA_DESCR_FLD="DESCRIPTION";
  public static final String DTD_PUBLIC_ID_FLD="DTD_PUBLIC_ID";
  public static final String SCHEMA_VALIDATE_FLD="VALIDATE";
  public static final String SCHEMA_LANG_FLD="SCHEMA_LANG";

  /**
   * Field names in UPL_SCHEMA table
   */
   public static final String UPL_SCHEMA_ID_FLD="SCHEMA_ID";
   public static final String UPL_SCHEMA_FLD="SCHEMA_NAME";
   public static final String UPL_SCHEMA_DESC="DESCRIPTION";
   public static final String UPL_FK_SCHEMA_ID="FK_SCHEMA_ID";
  
  
  /**
  * Field names in ROOT ELEMENTS table
  */
  public static final String ROOTELEM_ID_FLD="ROOTELEM_ID";
  public static final String ELEM_SCHEMA_ID_FLD="SCHEMA_ID";
  public static final String NAMESPACE_FLD="NAMESPACE";
  public static final String ELEM_NAME_FLD="ELEM_NAME";

  /*
  * T_XQJOBS
  */
  public static final String JOB_ID_FLD="JOB_ID";
  public static final String URL_FLD="URL";
  public static final String RESULT_FILE_FLD="RESULT_FILE";
  public static final String XQ_FILE_FLD="XQ_FILE";
  public static final String STATUS_FLD="N_STATUS";
  public static final String TIMESTAMP_FLD="TIME_STAMP";
  public static final String XQ_ID_FLD="QUERY_ID";
  public static final String SRC_FILE_FLD="SRC_FILE";
  
  /**
  * Field names in FILE table
  */
  public static final String FILE_ID_FLD="FILE_ID";
  public static final String FILE_NAME_FLD="FILE_NAME";
  public static final String FILE_TITLE_FLD="TITLE";
  public static final String FILE_TYPE_FLD="TYPE";
  public static final String FILE_PARENTTYPE_FLD="PARENT_TYPE";
  public static final String FILE_PARENTID_FLD="PARENT_ID";
  public static final String FILE_DESCRIPTION_FLD="DESCRIPTION";
  public static final String FILE_DEFAULT_FLD="F_DEFAULT";

/**
 * TYPE values in FILE table
 */
  public static final String XFORM_FILE_TYPE="xform";
  public static final String CSS_FILE_TYPE="css";
  public static final String IMAGE_FILE_TYPE="image";
  public static final String XML_FILE_TYPE="xml";
/**
 * PARENT_TYPE values in FILE table
 */
  public static final String SCHEMA_FILE_PARENT="xml_schema";


  /**
  * Field names in XFBROWSER table
  */
  public static final String BROWSER_ID_FLD="BROWSER_ID";
  public static final String BROWSER_TYPE_FLD="BROWSER_TYPE";
  public static final String BROWSER_TITLE_FLD="BROWSER_TITLE";
  public static final String BROWSER_STYLESHEET_FLD="STYLESHEET";
  public static final String BROWSER_PRIORITY_FLD="PRIORITY";
  
  
  /**
  * Field names in HOSTS table
  */
  public static final String HOST_ID_FLD="HOST_ID";
  public static final String HOST_NAME_FLD="HOST_NAME";
  public static final String USER_FLD="USER";
  public static final String PWD_FLD="PWD";

  /**
  * Field names in CONVTYPE table
  */
  public static final String CONV_TYPE_FLD="CONV_TYPE";
  public static final String CONTENT_TYPE_FLD="CONTENT_TYPE";
  public static final String FILE_EXT_FLD="FILE_EXT";
  public static final String CONVTYPE_DESCRIPTION_FLD="DESCRIPTION";

  /**
   * Field names in BACKUP table
   */
   public static final String BACKUP_ID_FLD="BACKUP_ID";
   public static final String BACKUP_OBJECT_ID_FLD="OBJECT_ID";
   public static final String BACKUP_FILENAME_FLD="FILE_NAME";
   public static final String BACKUP_TIMESTAMP_FLD="F_TIMESTAMP";
   public static final String BACKUP_USER_FLD="USER";

}
