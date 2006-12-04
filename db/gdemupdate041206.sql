-- Use to update previous version of GDEM database


-- Rename SCHEMA field to SCHEMA_NAME, because SCHEMA is a reserved word in Mysql 5



 ALTER TABLE `T_UPL_SCHEMA` CHANGE COLUMN `SCHEMA` `SCHEMA_NAME` VARCHAR(255) NOT NULL,
 DROP INDEX `SCHEMA`,
 ADD UNIQUE `SCHEMA_NAME` (`SCHEMA_NAME`),
 DROP INDEX `SCHEMA_ID`,
 ADD INDEX `SCHEMA_ID` (`SCHEMA_ID`, `SCHEMA_NAME`);