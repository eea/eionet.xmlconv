alter table T_QUERY add column SCRIPT_TYPE varchar(50);

update T_QUERY set SCRIPT_TYPE='xquery' where SCRIPT_TYPE='';

CREATE TABLE T_BACKUP (
  BACKUP_ID int(11) NOT NULL auto_increment,
  OBJECT_ID int(11) NOT NULL default '0',
  F_TIMESTAMP timestamp NULL default NULL,
  FILE_NAME varchar(255) default '',
  USER varchar(50) default '',
  PRIMARY KEY  (BACKUP_ID)
) TYPE=MyISAM;
