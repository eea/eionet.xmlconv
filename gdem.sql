-- MySQL dump 9.09
--
-- Host: localhost    Database: GDEM
-- ------------------------------------------------------
-- Server version	4.0.16-standard

--
-- Table structure for table `T_SCHEMA`
--

CREATE TABLE T_SCHEMA (
  SCHEMA_ID int(11) NOT NULL auto_increment,
  XML_SCHEMA varchar(255) NOT NULL default '',
  DESCRIPTION varchar(255) default NULL,
  PRIMARY KEY  (SCHEMA_ID),
  UNIQUE KEY XML_SCHEMA (XML_SCHEMA)
) TYPE=MyISAM;

--
-- Dumping data for table `T_SCHEMA`
--

INSERT INTO T_SCHEMA VALUES (1,'http://roddev.eionet.eu.int/waterdemo/water_measurements.xsd',NULL);
INSERT INTO T_SCHEMA VALUES (14,'http://roddev.eionet.eu.int/eper/eper_examples.xsd','');

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
  UNIQUE KEY CONVERT_ID (CONVERT_ID),
  UNIQUE KEY XSL_FILENAME (XSL_FILENAME)
) TYPE=MyISAM;

--
-- Dumping data for table `T_STYLESHEET`
--

INSERT INTO T_STYLESHEET VALUES (1,'Simple table','HTML','simpletablehtml.xsl',1);
INSERT INTO T_STYLESHEET VALUES (39,'Average yearly pH value per station','HTML','averagephhtml.xsl',1);
INSERT INTO T_STYLESHEET VALUES (43,'convert EPER data report for Ireland to HTML format','HTML','eper2html.xsl',14);
INSERT INTO T_STYLESHEET VALUES (42,'convert EPER data report for Ireland to PDF format','PDF','eper2pdf.xsl',14);
INSERT INTO T_STYLESHEET VALUES (40,'Simple excel table','EXCEL','water_excel.xsl',1);
INSERT INTO T_STYLESHEET VALUES (41,'Intermediate XML file format for Excel conversion','XML','water_xls_xml.xsl',1);

--
-- Table structure for table `T_XQHISTORY`
--

CREATE TABLE T_XQHISTORY (
  JOB_ID int(10) default NULL,
  STATUS int(1) default NULL,
  TIME_STAMP time default NULL,
  UNIQUE KEY JOB_ID (JOB_ID,STATUS)
) TYPE=MyISAM;

--
-- Dumping data for table `T_XQHISTORY`
--


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
  PRIMARY KEY  (JOB_ID)
) TYPE=MyISAM;


