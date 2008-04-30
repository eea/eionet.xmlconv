-- Use to update previous version of GDEM database


-- Add SCHEMA_URL field to T_UPL_SCHEMA table


 ALTER TABLE T_UPL_SCHEMA ADD COLUMN FK_SCHEMA_ID INT (11) UNSIGNED  NOT NULL default 0;
 ALTER TABLE T_SCHEMA ADD COLUMN SCHEMA_LANG varchar(255) NOT NULL default 'XSD';
