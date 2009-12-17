-- Use to update previous version of GDEM database


-- Adds EXPIRE_DATE field into T_SCHEMA table
ALTER TABLE T_SCHEMA  ADD COLUMN EXPIRE_DATE datetime default NULL;
