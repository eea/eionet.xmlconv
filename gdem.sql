-- MySQL dump 8.23
--
-- Host: localhost    Database: GDEM
---------------------------------------------------------
-- Server version	3.23.58

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
-- Dumping data for table `T_FILE`
--


INSERT INTO T_FILE VALUES (1,'xform104.xhtml','Water methods','xform','xml_schema',24);
INSERT INTO T_FILE VALUES (2,'xform104.xhtml','Water measurements','xform','xml_schema',1);
INSERT INTO T_FILE VALUES (3,'xform104.xhtml','Water stations','xform','xml_schema',25);
INSERT INTO T_FILE VALUES (6,'xform30_table.xhtml','Groundwater table','xform','xml_schema',28);
INSERT INTO T_FILE VALUES (10,'xform1637.xhtml','Groundwater, CGxxx','xform','xml_schema',34);
INSERT INTO T_FILE VALUES (11,'xform1646.xhtml.xml','SaltIntSiteboundaries','xform','xml_schema',35);

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
-- Dumping data for table `T_ROOT_ELEM`
--


INSERT INTO T_ROOT_ELEM VALUES (1,'measurements','',1);
INSERT INTO T_ROOT_ELEM VALUES (12,'tmx','',20);
INSERT INTO T_ROOT_ELEM VALUES (14,'report','',14);

--
-- Table structure for table `T_SCHEMA`
--

CREATE TABLE T_SCHEMA (
  SCHEMA_ID int(11) NOT NULL auto_increment,
  XML_SCHEMA varchar(255) NOT NULL default '',
  DESCRIPTION varchar(255) default NULL,
  DTD_PUBLIC_ID varchar(255) default NULL,
  PRIMARY KEY  (SCHEMA_ID),
  UNIQUE KEY XML_SCHEMA (XML_SCHEMA)
) TYPE=MyISAM;

--
-- Dumping data for table `T_SCHEMA`
--


INSERT INTO T_SCHEMA VALUES (1,'http://roddev.eionet.eu.int/waterdemo/water_measurements.xsd','Mel Gibson','');
INSERT INTO T_SCHEMA VALUES (20,'http://www.lisa.org/tmx/tmx14.dtd','','');
INSERT INTO T_SCHEMA VALUES (14,'http://roddev.eionet.eu.int/eper/eper_examples.xsd','','');
INSERT INTO T_SCHEMA VALUES (24,'http://roddev.eionet.eu.int/waterdemo/water_methods.xsd',NULL,NULL);
INSERT INTO T_SCHEMA VALUES (25,'http://roddev.eionet.eu.int/waterdemo/water_stations.xsd',NULL,NULL);
INSERT INTO T_SCHEMA VALUES (27,'http://dd.eionet.eu.int/GetSchema?comp_id=1752&amp;comp_type=TBL','','null');
INSERT INTO T_SCHEMA VALUES (28,'http://dd.eionet.eu.int/GetSchema?comp_id=1752&comp_type=TBL','','null');
INSERT INTO T_SCHEMA VALUES (29,'http://dd.eionet.eu.int/GetSchema?comp_id=1752&comp_type=TBL2','','null');
INSERT INTO T_SCHEMA VALUES (30,'http://dd.eionet.eu.int/GetSchema?comp_id=1752&comp_type=TBL3','','null');
INSERT INTO T_SCHEMA VALUES (33,'http://195.250.186.33:8080/datadict/public/GetSchema?id=TBL1752','','null');
INSERT INTO T_SCHEMA VALUES (34,'http://213.35.204.178:81/datadict/public/GetSchema?id=TBL1637','','null');
INSERT INTO T_SCHEMA VALUES (35,'http://213.35.204.178:81/datadict/public/GetSchema?id=TBL1646','','null');

--
-- Table structure for table `T_STYLESHEET`
--

CREATE TABLE T_STYLESHEET (
  CONVERT_ID int(11) NOT NULL auto_increment,
  DESCRIPTION varchar(255) default NULL,
  RESULT_TYPE enum('EXCEL','HTML','PDF','XML') NOT NULL default 'EXCEL',
  XSL_FILENAME varchar(255) NOT NULL default '',
  SCHEMA_ID int(11) NOT NULL default '0',
  PRIMARY KEY  (CONVERT_ID),
  UNIQUE KEY XSL_FILENAME (XSL_FILENAME),
  UNIQUE KEY CONVERT_ID (CONVERT_ID)
) TYPE=MyISAM;

--
-- Dumping data for table `T_STYLESHEET`
--


INSERT INTO T_STYLESHEET VALUES (1,'Simple table','HTML','simpletablehtml.xsl',1);
INSERT INTO T_STYLESHEET VALUES (52,'simple rows','HTML','simplerowshtml_1.xsl',1);
INSERT INTO T_STYLESHEET VALUES (39,'Average yearly pH value per station','HTML','averagephhtml.xsl',1);
INSERT INTO T_STYLESHEET VALUES (70,'Excel without header','EXCEL','CountryNamesExcelForAccess.xsl',20);
INSERT INTO T_STYLESHEET VALUES (43,'convert EPER data report for Ireland to HTML format','HTML','eper2html.xsl',14);
INSERT INTO T_STYLESHEET VALUES (42,'convert EPER data report for Ireland to PDF format','PDF','eper2pdf.xsl',14);
INSERT INTO T_STYLESHEET VALUES (54,'Country names HTML table with one column per language','HTML','CountryNames.xsl',20);
INSERT INTO T_STYLESHEET VALUES (40,'Simple excel table','EXCEL','water_excel.xsl',1);
INSERT INTO T_STYLESHEET VALUES (41,'Intermediate XML file format for Excel conversion','XML','water_xls_xml.xsl',1);
INSERT INTO T_STYLESHEET VALUES (68,'Country names intermediate XML for Excel','XML','CountryNamesExcel.xsl',20);
INSERT INTO T_STYLESHEET VALUES (69,'Country names Excel table','EXCEL','CountryNamesExcel_1.xsl',20);
INSERT INTO T_STYLESHEET VALUES (71,'simple table width table layout','HTML','simpletablehtml_tableLayout.xsl',1);

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
  PRIMARY KEY  (JOB_ID)
) TYPE=MyISAM;

--
-- Dumping data for table `T_XQJOBS`
--



