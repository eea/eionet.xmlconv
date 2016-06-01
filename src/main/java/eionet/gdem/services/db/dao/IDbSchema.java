package eionet.gdem.services.db.dao;

/**
 * DB Schema interface.
 * TODO: interfaces should have methods
 * @author Unknown
 */
public interface IDbSchema {

    /**
     * Table for storing file info in repository.
     */
    String FILE_TABLE = "T_FILE";
    /**
     * Table for XForms browsers.
     */
    String XFBROWSER_TABLE = "T_XFBROWSER";

    /**
     * Table for XQuery Workqueue.
     */
    String WQ_TABLE = "T_XQJOBS";

    /**
     * Table for stylesheets in the DB.
     */
    String XSL_TABLE = "T_STYLESHEET";

    /**
     * Table for schema stylesheets many-to-many in the DB.
     */
    String XSL_SCHEMA_TABLE = "T_STYLESHEET_SCHEMA";

    /**
     * Table for queries in the DB.
     */
    String QUERY_TABLE = "T_QUERY";

    /**
     * Table for xml schemas in the DB.
     */
    String SCHEMA_TABLE = "T_SCHEMA";

    /**
     * Table for uploaded xml schemas in the DB.
     */
    String UPL_SCHEMA_TABLE = "T_UPL_SCHEMA";

    /**
     * Table for root element mappings for schemas in the DB.
     */
    String ROOTELEM_TABLE = "T_ROOT_ELEM";

    /**
     * Table for hosts with usernames and passwords in the DB.
     */
    String HOST_TABLE = "T_HOST";

    /**
     * Table for conversion types.
     */
    String CONVTYPE_TABLE = "T_CONVTYPE";
    /**
     * Table for backup files.
     */
    String BACKUP_TABLE = "T_BACKUP";
    /**
     * Field names in XSL table.
     */
    String CNV_ID_FLD = "CONVERT_ID";
    String XSL_SCHEMA_ID_FLD = "SCHEMA_ID";
    String DESCR_FLD = "DESCRIPTION";
    String RESULT_TYPE_FLD = "RESULT_TYPE";
    String XSL_FILE_FLD = "XSL_FILENAME";
    String DEPENDS_ON = "DEPENDS_ON";

    /**
     * Field names in QUERY table.
     */
    String QUERY_ID_FLD = "QUERY_ID";
    String SHORT_NAME_FLD = "SHORT_NAME";
    String QUERY_FILE_FLD = "QUERY_FILENAME";
    String QUERY_RESULT_TYPE = "RESULT_TYPE";
    String QUERY_SCRIPT_TYPE = "SCRIPT_TYPE";
    String UPPER_LIMIT_FLD = "UPPER_LIMIT";
    String QUERY_URL_FLD    = "URL";
    String ACTIVE_FLD = "ACTIVE";

    /**
     * Field names in SCHEMA table.
     */
    String SCHEMA_ID_FLD = "SCHEMA_ID";
    String XML_SCHEMA_FLD = "XML_SCHEMA";
    String SCHEMA_DESCR_FLD = "DESCRIPTION";
    String DTD_PUBLIC_ID_FLD = "DTD_PUBLIC_ID";
    String SCHEMA_VALIDATE_FLD = "VALIDATE";
    String SCHEMA_LANG_FLD = "SCHEMA_LANG";
    String EXPIRE_DATE_FLD = "EXPIRE_DATE";
    String SCHEMA_BLOCKER_FLD = "BLOCKER";

    /**
     * Field names in UPL_SCHEMA table.
     */
    String UPL_SCHEMA_ID_FLD = "SCHEMA_ID";
    String UPL_SCHEMA_FLD = "SCHEMA_NAME";
    String UPL_SCHEMA_DESC = "DESCRIPTION";
    String UPL_FK_SCHEMA_ID = "FK_SCHEMA_ID";

    /**
     * Field names in ROOT ELEMENTS table.
     */
    String ROOTELEM_ID_FLD = "ROOTELEM_ID";
    String ELEM_SCHEMA_ID_FLD = "SCHEMA_ID";
    String NAMESPACE_FLD = "NAMESPACE";
    String ELEM_NAME_FLD = "ELEM_NAME";

    /*
     * T_XQJOBS.
     */
    String JOB_ID_FLD = "JOB_ID";
    String URL_FLD = "URL";
    String RESULT_FILE_FLD = "RESULT_FILE";
    String XQ_FILE_FLD = "XQ_FILE";
    String STATUS_FLD = "N_STATUS";
    String TIMESTAMP_FLD = "TIME_STAMP";
    String XQ_ID_FLD = "QUERY_ID";
    String SRC_FILE_FLD = "SRC_FILE";
    String XQ_TYPE_FLD = "XQ_TYPE";

    /**
     * Field names in FILE table.
     */
    String FILE_ID_FLD = "FILE_ID";
    String FILE_NAME_FLD = "FILE_NAME";
    String FILE_TITLE_FLD = "TITLE";
    String FILE_TYPE_FLD = "TYPE";
    String FILE_PARENTTYPE_FLD = "PARENT_TYPE";
    String FILE_PARENTID_FLD = "PARENT_ID";
    String FILE_DESCRIPTION_FLD = "DESCRIPTION";
    String FILE_DEFAULT_FLD = "F_DEFAULT";

    /**
     * TYPE values in FILE table.
     */
    String XFORM_FILE_TYPE = "xform";
    String CSS_FILE_TYPE = "css";
    String IMAGE_FILE_TYPE = "image";
    String XML_FILE_TYPE = "xml";
    /**
     * PARENT_TYPE values in FILE table.
     */
    String SCHEMA_FILE_PARENT = "xml_schema";

    /**
     * Field names in XFBROWSER table.
     */
    String BROWSER_ID_FLD = "BROWSER_ID";
    String BROWSER_TYPE_FLD = "BROWSER_TYPE";
    String BROWSER_TITLE_FLD = "BROWSER_TITLE";
    String BROWSER_STYLESHEET_FLD = "STYLESHEET";
    String BROWSER_PRIORITY_FLD = "PRIORITY";

    /**
     * Field names in HOSTS table.
     */
    String HOST_ID_FLD = "HOST_ID";
    String HOST_NAME_FLD = "HOST_NAME";
    String USER_FLD = "USER";
    String PWD_FLD = "PWD";

    /**
     * Field names in CONVTYPE table.
     */
    String CONV_TYPE_FLD = "CONV_TYPE";
    String CONTENT_TYPE_FLD = "CONTENT_TYPE";
    String FILE_EXT_FLD = "FILE_EXT";
    String CONVTYPE_DESCRIPTION_FLD = "DESCRIPTION";

    /**
     * Field names in BACKUP table.
     */
    String BACKUP_ID_FLD = "BACKUP_ID";
    String BACKUP_OBJECT_ID_FLD = "OBJECT_ID";
    String BACKUP_FILENAME_FLD = "FILE_NAME";
    String BACKUP_TIMESTAMP_FLD = "F_TIMESTAMP";
    String BACKUP_USER_FLD = "USER";

    /**
     * Field names in STYLESHEET_SCHEMA table.
     */
    String STYLESHEET_ID_FLD = "STYLESHEET_ID";

}
