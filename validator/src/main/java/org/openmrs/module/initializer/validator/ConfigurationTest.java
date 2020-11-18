package org.openmrs.module.initializer.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.openmrs.module.initializer.validator.Validator.ARG_CIEL_PATH;
import static org.openmrs.module.initializer.validator.Validator.ARG_CONFIG_DIR;
import static org.openmrs.module.initializer.validator.Validator.cmdLine;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.dbunit.DatabaseUnitException;
import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLDialect;
import org.hsqldb.cmdline.SqlFile;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.test.TestUtil;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

public class ConfigurationTest extends DomainBaseModuleContextSensitiveTest {
	
	private String configDirPath;
	
	protected String getAppDataDirPath() {
		return Paths.get(configDirPath).getParent().toString();
	}
	
	protected void setMariaDB4jProps(Properties props) throws ManagedProcessException {
		DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
		config.setPort(0); // 0 => autom. detect free port
		
		DB db = DB.newEmbeddedDB(config.build());
		db.start();
		db.createDB("openmrs");
		
		props.setProperty(Environment.DIALECT, MySQLDialect.class.getName());
		//		props.setProperty(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
		props.setProperty(Environment.URL, config.getURL("openmrs"));
		props.setProperty(Environment.USER, "root");
		props.setProperty(Environment.PASS, "");
		
		// automatically create the tables defined in the hbm files
		props.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
	}
	
	@Override
	public Properties getRuntimeProperties() {
		
		if (runtimeProperties == null) {
			runtimeProperties = TestUtil.getRuntimeProperties(getWebappName());
		}
		
		try {
			setMariaDB4jProps(runtimeProperties);
		}
		catch (ManagedProcessException e) {
			log.error("MariaDB4j could not be setup, reverting to defaults.", e);
			runtimeProperties = super.getRuntimeProperties();
		}
		
		return runtimeProperties;
	}
	
	@Override
	public void setAutoIncrementOnTablesWithNativeIfNotAssignedIdentityGenerator() {
	}
	
	/*
	 * Exact copy override to rely on our own setupDatabaseConnection
	 */
	@Override
	public void executeDataSet(IDataSet dataset) {
		try {
			Connection connection = getConnection();
			
			IDatabaseConnection dbUnitConn = setupDatabaseConnection(connection);
			
			//Do the actual update/insert:
			//insert new rows, update existing rows, and leave others alone
			DatabaseOperation.REFRESH.execute(dbUnitConn, dataset);
		}
		catch (DatabaseUnitException | SQLException e) {
			throw new DatabaseUnitRuntimeException(e);
		}
	}
	
	/*
	 * This should be an @Override from the protected signature of the superclass
	 */
	private IDatabaseConnection setupDatabaseConnection(Connection connection) throws DatabaseUnitException {
		IDatabaseConnection dbUnitConn = new DatabaseConnection(connection);
		DatabaseConfig config = dbUnitConn.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		return dbUnitConn;
	}
	
	/*
	 * This should be an @Override from the protected signature of the superclass
	 */
	private void turnOnDBConstraints(Connection connection) throws SQLException {
		String constraintsOnSql = "SET FOREIGN_KEY_CHECKS=1;";
		PreparedStatement ps = connection.prepareStatement(constraintsOnSql);
		ps.execute();
		ps.close();
	}
	
	/*
	 * This should be an @Override from the protected signature of the superclass
	 */
	private void turnOffDBConstraints(Connection connection) throws SQLException {
		String constraintsOffSql = "SET FOREIGN_KEY_CHECKS=0;";
		PreparedStatement ps = connection.prepareStatement(constraintsOffSql);
		ps.execute();
		ps.close();
	}
	
	@Override
	public void deleteAllData() {
	}
	
	public ConfigurationTest() {
		super();
		
		assertThat("No arguments were provided to the configuration validator.", cmdLine.getOptions(), not(emptyArray()));
		assertThat("The path to an OpenMRS configuration directory should be provided.", cmdLine.hasOption(ARG_CONFIG_DIR),
		    is(true));
		
		configDirPath = Validator.cmdLine.getOptionValue(ARG_CONFIG_DIR);
	}
	
	@Before
	public void prepare() throws Exception {
		if (cmdLine.hasOption(ARG_CIEL_PATH)) {
			Connection connection = getConnection();
			SqlFile sqlFile = new SqlFile(Validator.trimCielSqlFile(new File(cmdLine.getOptionValue(ARG_CIEL_PATH))));
			sqlFile.setConnection(connection);
			turnOffDBConstraints(connection);
			sqlFile.execute();
			turnOnDBConstraints(connection);
		}
	}
	
	@Test
	public void loadConfiguration() {
		for (Loader loader : getService().getLoaders()) {
			loader.load();
		}
	}
}
