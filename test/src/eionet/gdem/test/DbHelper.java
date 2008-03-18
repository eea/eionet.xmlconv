/*
 * Created on 14.03.2008
 */
package eionet.gdem.test;

import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.sql.Connection;

import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;

/**
 * Helper class for db related stuff - connection, etc
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS
 * DbHelper
 */

public class DbHelper {

	public static void setUpConnectionProperties() {
		GDEMServices.setTestConnection(true);
		System.setProperty(
				PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
				Properties.dbDriver);
		System.setProperty(
				PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
				Properties.dbUrl);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
				Properties.dbUser);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
				Properties.dbPwd);
	}

	/**
	 * exctract data from DB and create xml files
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Connection jdbcConnection = null;
		// database connection
		try {
			Class driverClass = Class.forName(Properties.dbDriver);
			jdbcConnection = DriverManager.getConnection(Properties.dbUrl,
					Properties.dbUser, Properties.dbPwd);
			IDatabaseConnection connection = new DatabaseConnection(
					jdbcConnection);

			// partial database export
			/*
			QueryDataSet partialDataSet = new QueryDataSet(connection);
			partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE COL='VALUE'");
			partialDataSet.addTable("BAR");
			FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));
			 */

			// full database export
			IDataSet fullDataSet = connection.createDataSet();
			FlatXmlDataSet.write(fullDataSet, new FileOutputStream(
			"c:/Projects/xmlconv/full.xml"));

			// dependent tables database export: export table X and all tables that
			// have a PK which is a FK on X, in the right order for insertion
			/*
			String[] depTableNames = 
			  TablesDependencyHelper.getAllDependentTables( connection, "X" );
			IDataSet depDataset = connection.createDataSet( depTableNames );
			FlatXmlDataSet.write(depDataset, new FileOutputStream("dependents.xml"));
			 */
		} finally {
			jdbcConnection.close();
		}

	}

}
