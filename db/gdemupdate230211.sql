-- This field contains the upper limit in MB for stopping on-demand QA. 
alter table T_QUERY add column UPPER_LIMIT int(11) default 10 ;