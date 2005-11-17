-- Database: GDEM
---------------------------------------------------------

--
-- Table structure for table `T_FILE`
--

CREATE TABLE T_FILE (
  FILE_ID int(11) NOT NULL auto_increment,
  FILE_NAME varchar(255) NOT NULL default '',
  TITLE varchar(255) default '',
  TYPE varchar(10) NOT NULL default '',
  PARENT_TYPE varchar(20) NOT NULL default '',
  PARENT_ID int(11) NOT NULL default '0',
  PRIMARY KEY  (FILE_ID)
) TYPE=MyISAM;

--
-- Table structure for table `T_ROOT_ELEM`
--

CREATE TABLE T_ROOT_ELEM (
  ROOTELEM_ID int(11) unsigned NOT NULL auto_increment,
  ELEM_NAME varchar(255) NOT NULL default '',
  NAMESPACE varchar(255) default NULL,
  SCHEMA_ID int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (ROOTELEM_ID)
) TYPE=MyISAM;

--
-- Table structure for table `T_SCHEMA`
--

CREATE TABLE T_SCHEMA (
  SCHEMA_ID int(11) NOT NULL auto_increment,
  XML_SCHEMA varchar(255) NOT NULL default '',
  DESCRIPTION varchar(255) default NULL,
  DTD_PUBLIC_ID varchar(255) default NULL,
  VALIDATE enum('0','1') default '0',
  PRIMARY KEY  (SCHEMA_ID),
  UNIQUE KEY XML_SCHEMA (XML_SCHEMA)
) TYPE=MyISAM;

--
-- Table structure for table `T_STYLESHEET`
--

CREATE TABLE T_STYLESHEET (
  CONVERT_ID int(11) NOT NULL auto_increment,
  DESCRIPTION varchar(255) default NULL,
  RESULT_TYPE varchar(50) NOT NULL default 'HTML',
  XSL_FILENAME varchar(255) NOT NULL default '',
  SCHEMA_ID int(11) NOT NULL default '0',
  PRIMARY KEY  (CONVERT_ID),
  UNIQUE KEY XSL_FILENAME (XSL_FILENAME),
  UNIQUE KEY CONVERT_ID (CONVERT_ID)
) TYPE=MyISAM;


--
-- Table structure for table `T_XFBROWSER`
--
CREATE TABLE T_XFBROWSER (
  BROWSER_ID int(11) NOT NULL auto_increment,
  BROWSER_TYPE varchar(100) NOT NULL default '',
  BROWSER_TITLE varchar(255) NOT NULL default '',
  STYLESHEET varchar(255) default NULL,
  PRIORITY int(11) NOT NULL default '0',
  PRIMARY KEY  (BROWSER_ID),
  UNIQUE KEY BROWSER_TYPE (BROWSER_TYPE),
  UNIQUE KEY BROWSER_ID (BROWSER_ID)
) TYPE=MyISAM;

--
-- Dumping data for table `T_XFBROWSER`
--


INSERT INTO T_XFBROWSER VALUES (1,'HTML','Simple HTML',NULL,1);
INSERT INTO T_XFBROWSER VALUES (2,'JSCRIPT','HTML with JavaScript',NULL,2);
INSERT INTO T_XFBROWSER VALUES (3,'XFORM-FP','formsPlayer - XForms capable client',NULL,3);
INSERT INTO T_XFBROWSER VALUES (4,'XFORM-XSMILES','X-Smiles - XForms capable client',NULL,4);

--
-- Table structure for table `T_XQJOBS`
--

CREATE TABLE T_XQJOBS (
  JOB_ID int(10) NOT NULL auto_increment,
  URL varchar(255) default NULL,
  XQ_FILE varchar(255) default NULL,
  RESULT_FILE varchar(255) default NULL,
  STATUS int(1) default NULL,
  TIME_STAMP datetime default NULL,
  N_STATUS int(10) default NULL,
  QUERY_ID int(11) default '0',
  SRC_FILE varchar(255) default NULL, 
  PRIMARY KEY  (JOB_ID)
) TYPE=MyISAM;


--
-- Table structure for table `T_HOST`
--

CREATE TABLE T_HOST (
  HOST_ID int(11) unsigned NOT NULL auto_increment,
  HOST_NAME varchar(255) NOT NULL default '',
  USER varchar(255) NOT NULL default '',
  PWD varchar(255) NOT NULL default '',
  PRIMARY KEY  (HOST_ID)
) TYPE=MyISAM;


--
-- Table structure for table `T_CONV_TYPE`
--



CREATE TABLE T_CONVTYPE ( 
  CONV_TYPE varchar(50) NOT NULL default '', 
  CONTENT_TYPE varchar(100) NOT NULL default 'text/plain', 
  FILE_EXT varchar(20) NOT NULL default 'txt', 
  DESCRIPTION varchar (255) default '', 
  UNIQUE KEY CONV_TYPE (CONV_TYPE)
) TYPE=MyISAM;

--
-- Dumping data for table `T_CONV_TYPE`
--


INSERT INTO T_CONVTYPE VALUES ('HTML','text/html','html','');
INSERT INTO T_CONVTYPE VALUES ('XML','text/xml','xml','');
INSERT INTO T_CONVTYPE VALUES ('EXCEL','application/vnd.ms-excel','xls','');
INSERT INTO T_CONVTYPE VALUES ('PDF','application/pdf','pdf','');
INSERT INTO T_CONVTYPE VALUES ('SQL','text/plain','sql','');


--
-- Table structure for table `UPL_SCHEMA`
--

CREATE TABLE T_UPL_SCHEMA (
SCHEMA_ID INT (11) UNSIGNED  NOT NULL AUTO_INCREMENT, 
SCHEMA VARCHAR (255) DEFAULT '' NOT NULL, 
DESCRIPTION varchar(255) NOT NULL default '',
PRIMARY KEY(SCHEMA_ID), 
UNIQUE(SCHEMA), 
INDEX(SCHEMA_ID,SCHEMA)
)  TYPE=MyISAM;


--
-- Table structure for table `T_QUERY`
--

CREATE TABLE T_QUERY (
  QUERY_ID int(11) NOT NULL auto_increment,
  DESCRIPTION varchar(255) default NULL,
  SHORT_NAME varchar(255) NOT NULL default 'Query',
  QUERY_FILENAME varchar(255) NOT NULL default '',
  SCHEMA_ID int(11) NOT NULL default '0',
  RESULT_TYPE varchar(50) default 'HTML',
  PRIMARY KEY  (QUERY_ID),
  UNIQUE KEY QUERY_FILENAME (QUERY_FILENAME),
  UNIQUE KEY QUERY_ID (QUERY_ID)
) TYPE=MyISAM;