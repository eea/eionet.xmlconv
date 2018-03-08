/*
 * Created on 14.03.2008
 */
package eionet.gdem.test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import liquibase.database.Database;
import org.dbunit.*;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;

import javax.sql.DataSource;

/**
 * TODO: See if it is possible to delete all commented out methods
 * Helper class for db related stuff - connection, etc
 *
 * @author Enriko Käsper, TietoEnator Estonia AS DbHelper
 * @author George Sofianos
 */

public class DbHelper {
    public static void setUpDatabase(DataSource db, String dataset) throws Exception {
        Connection dsConnection = db.getConnection();
        IDatabaseConnection connection = new DatabaseConnection(dsConnection);
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);
        IDataSet dataSet =
                new FlatXmlDataSetBuilder().build(new File(db.getClass().getClassLoader().getResource(dataset).getFile()));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        connection.close();
    }
}
