package eionet.gdem.web.spring.schemas;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 *
 *
 */
@Repository
public class SchemasJooqDao {

    private DSLContext create;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SchemasJooqDao(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.create = context;
        this.jdbcTemplate = jdbcTemplate;
    }


    public String getSchemaUrl(String schemaId) {
                 String schema = (String) create
                .select(field(SchemaMySqlDao.XML_SCHEMA_FLD))
                .from(table(SchemaMySqlDao.SCHEMA_TABLE))
                .where(field(SchemaMySqlDao.SCHEMA_ID_FLD).eq(schemaId))
                .fetchOne().value1();
                /*
        Schema schema = new Schema();
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                schema.setSchema(resultSet.getString(SchemaMySqlDao.UPL_SCHEMA_FLD));
            }
        });
        return schema.getSchema();*/
        return schema;
    }
}
