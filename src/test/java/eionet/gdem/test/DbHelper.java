/*
 * Created on 14.03.2008
 */
package eionet.gdem.test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import com.mysql.fabric.xmlrpc.base.Data;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory;
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
    }
    public static void setUpDefaultDatabaseTester(IDatabaseTester databaseTester, String dataset) throws Exception {

        // Needed for backward compatibility.
        //DbHelper.setUpConnectionProperties();
        /*IDatabaseConnection connection = databaseTester.getConnection();
        DatabaseConfig config = connection.getConfig();
        //config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

        databaseTester.getConnection().getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        IDataSet dataSet =
                new FlatXmlDataSetBuilder().build(new File(databaseTester.getClass().getClassLoader().getResource(dataset).getFile()));
        databaseTester.setDataSet(dataSet);

        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setTearDownOperation(DatabaseOperation.NONE);
        databaseTester.onSetup();*/

    }

    private static void setUpSpringContextWithDatabaseTester(String dataset) throws Exception {

        // read the application context file
        //ClassPathXmlApplicationContext ctx =
        //        new ClassPathXmlApplicationContext("/test-spring-app-context.xml", "/test-datasource-context.xml");
        // instantiate spring database tester from the application context
        //IDatabaseTester databaseTester = (IDatabaseTester) ctx.getBean("databaseTester");
        //setUpDatabase(db, dataset);

    }

    public static void setUpConnectionProperties() {
        //GDEMServices.setTestConnection(true);
        //System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, Properties.dbDriver);
        //System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, Properties.dbUrl);
        //System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, Properties.dbUser);
        //System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, Properties.dbPwd);
    }

    public static void setUpDatabase(Object obj, String dataset) throws Exception {

        //GDEMServices.setTestConnection(true);
        //AbstractDatabaseTester databaseTester =
        //        new JdbcDatabaseTester(Properties.dbDriver, Properties.dbUrl, Properties.dbUser, Properties.dbPwd);

        //IDataSet dataSet = new FlatXmlDataSet(obj.getClass().getClassLoader().getResourceAsStream(dataset));
        //databaseTester.setDataSet(dataSet);
        //databaseTester.onSetup();

    }
}
