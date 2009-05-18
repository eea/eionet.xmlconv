alter table T_STYLESHEET add column DEPENDS_ON int(11);
alter table T_STYLESHEET add foreign key fk_dependson (DEPENDS_ON) references T_STYLESHEET(CONVERT_ID) on delete cascade;