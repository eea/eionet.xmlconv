-- Use to update previous version of GDEM database

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


