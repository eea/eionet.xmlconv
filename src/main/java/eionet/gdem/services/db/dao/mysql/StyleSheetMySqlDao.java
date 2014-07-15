package eionet.gdem.services.db.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.StringUtils;

import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.services.db.dao.IStyleSheetDao;
import eionet.gdem.utils.Utils;

/**
 *
 * DAO for stylesheets.
 *
 * @author Enriko Käsper
 */
@Repository("stylehseetDao")
public class StyleSheetMySqlDao extends MySqlBaseDao implements IStyleSheetDao {

    /**
     * Spring Jdbc template for accessing data storage.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Spring Named Jdbc template for accessing data storage.
     */
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Schema DAO.
     */
    @Autowired
    private ISchemaDao schemaDao;

    /** */
    private static final Log LOGGER = LogFactory.getLog(StyleSheetMySqlDao.class);

    /** SQL for inserting stylesheet record. */
    private static final String INSERT_STYLESHEET = "INSERT INTO " + XSL_TABLE + " ( " + RESULT_TYPE_FLD + ", " + XSL_FILE_FLD
            + ", " + DESCR_FLD + ", " + DEPENDS_ON + ") " + " VALUES (?,?,?,?)";

    /** SQL for getting stylesheet by unique file name. */
    private static final String GET_STYLESHEET_BY_FILENAME = "SELECT " + CNV_ID_FLD + " FROM " + XSL_TABLE + " WHERE "
            + XSL_FILE_FLD + "= ? ";

    /** SQL for updating stylesheet record. */
    private static final String UPDATE_STYLESHEET = "UPDATE  " + XSL_TABLE + " SET " + DESCR_FLD + "= ? " + ", " + ", "
            + RESULT_TYPE_FLD + "= ? " + ", " + DEPENDS_ON + "= ?" + " WHERE " + CNV_ID_FLD + "= ?";

    /** SQL for updating stylesheet record with file name field. */
    private static final String UPDATE_STYLESHEET_FILENAME = "UPDATE  " + XSL_TABLE + " SET " + XSL_FILE_FLD + "= ? " + ", "
            + DESCR_FLD + "= ? " + ", " + RESULT_TYPE_FLD + "= ? " + ", " + DEPENDS_ON + "= ?" + " WHERE " + CNV_ID_FLD + "= ? ";

    /** SQL for deleting stylesheet record. */
    private static final String DELETE_STYLESHEET = "DELETE FROM " + XSL_TABLE + " WHERE " + CNV_ID_FLD + "= ? ";

    /** SQL for querying T_STYLESHEET records without filter. */
    private static final String GET_STYLESHEET_INFO_BASE_SQL = "select xsl.CONVERT_ID, xsl.XSL_FILENAME, xsl.DESCRIPTION, "
            + "xsl.RESULT_TYPE, xsl.DEPENDS_ON from T_STYLESHEET xsl ";

    /** SQL for querying T_STYLESHEET record filtered by FILE_NAME field. */
    private static final String GET_STYLESHEET_BY_FILENAME_SQL = GET_STYLESHEET_INFO_BASE_SQL + " WHERE " + XSL_FILE_FLD + "= ?";
    /** SQL for querying T_STYLESHEET record filtered by CONVERT_ID field. */
    private static final String GET_STYLESHEET_BY_ID_SQL = GET_STYLESHEET_INFO_BASE_SQL + " WHERE " + CNV_ID_FLD + "= ?";

    /** SQL for querying stylesheet schemas. */
    private static final String GET_STYLESHEET_SCHEMAS = "select s.SCHEMA_ID, s.XML_SCHEMA, s.DESCRIPTION, s.SCHEMA_LANG, "
            + "xs.STYLESHEET_SCHEMA_ID from "
            + "T_STYLESHEET_SCHEMA xs inner join T_SCHEMA s on xs.SCHEMA_ID=s.SCHEMA_ID WHERE xs.STYLESHEET_ID = ?";
    /** SQL for counting stylesheets by file name. */
    private static final String COUNT_STYLESHEETS_BY_FILENAME = "SELECT COUNT(*) FROM " + XSL_TABLE + " WHERE " + XSL_FILE_FLD
            + "= ?";
    /** SQL for counting stylesheets by file name and id. */
    private static final String COUNT_STYLESHEETS_BY_FILENAME_AND_ID = "SELECT COUNT(*) FROM " + XSL_TABLE + " WHERE "
            + XSL_FILE_FLD + "= ? and " + CNV_ID_FLD + "= ?";

    /** Get all stylesheets.*/
    private static final String GET_STYLESHEETS_SQL = "select CONVERT_ID, DESCRIPTION, RESULT_TYPE, XSL_FILENAME "
            + "from T_STYLESHEET order by XSL_FILENAME";
    /** SQL for deleting all schema stylesheet relations except the given list of schema Ids. */
    private static final String DELETE_STYLESHEET_SCHEMAS = "DELETE FROM T_STYLESHEET_SCHEMA "
            + "where STYLESHEET_ID = :stylesheetId AND SCHEMA_ID NOT IN ( :schemaIds )";
    /** SQL for inserting new stylehseet schema relation. */
    private static final String INSERT_STYLESHEET_SCHEMA =
            "INSERT IGNORE INTO T_STYLESHEET_SCHEMA (STYLESHEET_ID, SCHEMA_ID) VALUES (?, ?)";

    @Override
    @Transactional
    public String addStylesheet(Stylesheet stylesheet) throws SQLException {

        if (isDebugMode) {
            LOGGER.debug("Query is " + INSERT_STYLESHEET);
        }

        jdbcTemplate.update(INSERT_STYLESHEET, stylesheet.getType(), stylesheet.getXslFileName(), stylesheet.getDescription(), stylesheet.getDependsOn());
        String newStylesheetId =
                jdbcTemplate.queryForObject(GET_STYLESHEET_BY_FILENAME, String.class, stylesheet.getXslFileName());
        stylesheet.setConvId(newStylesheetId);
        updateStylesheetSchemas(stylesheet);

        return newStylesheetId;
    }

    @Override
    @Transactional
    public void deleteStylesheet(String convertId) throws SQLException {

        if (isDebugMode) {
            LOGGER.debug("Query is " + DELETE_STYLESHEET);
        }
        jdbcTemplate.update(DELETE_STYLESHEET, convertId);
    }

    @Override
    public boolean checkStylesheetFile(String xslFileName) throws SQLException {

        if (isDebugMode) {
            LOGGER.debug("Query is " + COUNT_STYLESHEETS_BY_FILENAME);
        }
        Integer countStylesheets = jdbcTemplate.queryForObject(COUNT_STYLESHEETS_BY_FILENAME, Integer.class, xslFileName);
        return (countStylesheets != null && countStylesheets > 0);
    }

    @Override
    public boolean checkStylesheetFile(String xslId, String xslFileName) throws SQLException {
        if (isDebugMode) {
            LOGGER.debug("Query is " + COUNT_STYLESHEETS_BY_FILENAME);
        }
        Integer countStylesheets =
                jdbcTemplate.queryForObject(COUNT_STYLESHEETS_BY_FILENAME_AND_ID, Integer.class, xslFileName, xslId);
        return (countStylesheets != null && countStylesheets > 0);
    }

    @Override
    public List<Stylesheet> getStylesheets() {

        if (isDebugMode) {
            LOGGER.debug("Query is " + GET_STYLESHEETS_SQL);
        }

        final List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();
        jdbcTemplate.query(GET_STYLESHEETS_SQL, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Stylesheet stylesheet = new Stylesheet();
                stylesheet.setConvId(rs.getString("CONVERT_ID"));
                stylesheet.setDescription(rs.getString("DESCRIPTION"));
                stylesheet.setType(rs.getString("RESULT_TYPE"));
                stylesheet.setXslFileName(rs.getString("XSL_FILENAME"));
                stylesheets.add(stylesheet);
            }
        });
        return stylesheets;

    }

    @Override
    public Stylesheet getStylesheet(String convertId) {

        boolean findById = convertId.matches("\\d+");

        String query = (findById ? GET_STYLESHEET_BY_ID_SQL : GET_STYLESHEET_BY_FILENAME_SQL);

        if (isDebugMode) {
            LOGGER.debug("Query is " + query);
        }
        Stylesheet stylesheet = null;
        try {
            stylesheet = jdbcTemplate.queryForObject(query, new String[] { convertId }, new StylesheetRowMapper());
        } catch (EmptyResultDataAccessException e) {
            LOGGER.warn("No stylesheet found with id=" + convertId, e);
        }

        if (stylesheet != null) {
            List<Schema> schemas =
                    jdbcTemplate.query(GET_STYLESHEET_SCHEMAS, new String[] { stylesheet.getConvId() }, new SchemaRowMapper());
            stylesheet.setSchemas(schemas);
            if (schemas != null) {
                List<String> schemaIds = new ArrayList<String>();
                for (Schema schema : schemas) {
                    schemaIds.add(schema.getId());
                }
                stylesheet.setSchemaIds(schemaIds);
            }
        }

        return stylesheet;

    }

    /**
    *
    * Map T_STYLESHEET table fields to Stylesheet object properties.
    *
    * @author Enriko Käsper
    */
    class StylesheetRowMapper implements RowMapper<Stylesheet> {
        @Override
        public Stylesheet mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stylesheet stylesheet = new Stylesheet();
            stylesheet.setConvId(rs.getString("xsl.CONVERT_ID"));
            stylesheet.setDescription(rs.getString("xsl.DESCRIPTION"));
            stylesheet.setType(rs.getString("xsl.RESULT_TYPE"));
            stylesheet.setXslFileName(rs.getString("xsl.XSL_FILENAME"));
            stylesheet.setDependsOn(rs.getString("xsl.DEPENDS_ON"));
            return stylesheet;
        }
    }

    /**
    *
    * Map T_SCHEMA table fields to Schema object properties.
    *
    * @author Enriko Käsper
    */
    class SchemaRowMapper implements RowMapper<Schema> {
        @Override
        public Schema mapRow(ResultSet rs, int rowNum) throws SQLException {
            Schema schema = new Schema();
            schema.setId(rs.getString("s.SCHEMA_ID"));
            schema.setSchema(rs.getString("s.XML_SCHEMA"));
            schema.setDescription(rs.getString("s.DESCRIPTION"));
            schema.setSchemaLang(rs.getString("s.SCHEMA_LANG"));
            schema.setStylesheetSchemaId(rs.getString("xs.STYLESHEET_SCHEMA_ID"));
            return schema;
        }
    }

    @Override
    @Transactional
    public void updateStylesheet(Stylesheet stylesheet) throws SQLException {

        String description = (stylesheet.getDescription() == null ? "" : stylesheet.getDescription());
        boolean isEmptyFileName = (stylesheet.getXslFileName() == null || stylesheet.getXslFileName().equals(""));
        Integer dependsOnValue = Utils.isNullStr(stylesheet.getDependsOn()) ? 0 : Integer.parseInt(stylesheet.getDependsOn());

        if (isEmptyFileName) {
            jdbcTemplate.update(UPDATE_STYLESHEET, description, stylesheet.getType(), dependsOnValue, Integer.parseInt(stylesheet.getConvId()));
        } else {
            jdbcTemplate.update(UPDATE_STYLESHEET_FILENAME, stylesheet.getXslFileName(), description, stylesheet.getType(), dependsOnValue, Integer.parseInt(stylesheet.getConvId()));
        }
        updateStylesheetSchemas(stylesheet);

    }

    /**
     * Update many-to-many relations between stylesheet ans schemas.
     * @param stylesheet Stylesheet dto object
     * @throws SQLException in case of database error
     */
    private void updateStylesheetSchemas(Stylesheet stylesheet) throws SQLException {
        List<String> schemaIds = new ArrayList<String>();

        // add new schemas
        if (stylesheet.getSchemaUrls() != null) {
            for (String schema : stylesheet.getSchemaUrls()) {
                if (!StringUtils.isEmptyOrWhitespaceOnly(schema)) {
                    String schemaId = schemaDao.getSchemaID(schema);
                    if (schemaId == null) {
                        schemaId = schemaDao.addSchema(schema, null);
                    }
                    schemaIds.add(schemaId);
                    jdbcTemplate.update(INSERT_STYLESHEET_SCHEMA, stylesheet.getConvId(), schemaId);
                }
            }
        }
        if (stylesheet.getSchemaIds() != null) {
            schemaIds.addAll(stylesheet.getSchemaIds());
        }
        // delete unneeded schemas
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (schemaIds == null || schemaIds.size() == 0) {
            parameters.addValue("schemaIds", "");
        } else {
            parameters.addValue("schemaIds", schemaIds);
        }
        parameters.addValue("stylesheetId", stylesheet.getConvId());

        namedParameterJdbcTemplate.update(DELETE_STYLESHEET_SCHEMAS, parameters);
    }
}
