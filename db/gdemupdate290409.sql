alter table t_stylesheet add column DEPENDS_ON int(11);
alter table t_stylesheet add foreign key fk_dependson (depends_on) references t_stylesheet(convert_id) on delete cascade;