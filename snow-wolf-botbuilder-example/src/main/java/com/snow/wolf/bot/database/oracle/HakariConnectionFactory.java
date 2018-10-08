/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snow.wolf.bot.database.oracle;


import com.mservice.common.util.Utils;
import com.mservice.conf.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.driver.OracleDriver;
import oracle.ucp.jdbc.ValidConnection;

import java.sql.Connection;
import java.util.Properties;

public class HakariConnectionFactory {

	private static String schema;
	private static HikariDataSource datasource;
	private static HikariConfig config;

	public static void init(DatabaseConfig configJson) throws Exception {

		schema = configJson.getSchema();
		if (Utils.isEmpty(schema)) {
			throw new Exception("Please config the database schema");
		}
		if (datasource != null) {
			datasource.close();
		}
		datasource = createPool("test", configJson);
	}

	public static String getSchema() {
		return schema;
	}

	private static HikariDataSource createPool(String serviceCode, DatabaseConfig databaseConfig) throws Exception {

		Properties proc = new Properties();
		proc.put("driverType", "thin");
		proc.put("portNumber", 1521);

		config = new HikariConfig();

		config.setDriverClassName(OracleDriver.class.getName());
		config.setJdbcUrl(databaseConfig.getUrlDB());
		config.setUsername(databaseConfig.getUsernameDB());
		config.setPassword(databaseConfig.getPasswordDB());
		config.setInitializationFailFast(false);
		config.setConnectionTestQuery("select 1 from dual");
		config.setMaximumPoolSize(databaseConfig.getMaxPoolSize());
		config.setAutoCommit(true);
		config.setDataSourceProperties(proc);

		HikariDataSource localDatasource = new HikariDataSource(config);
		return localDatasource;
	}

	public static HikariDataSource getPoolDataSource() throws Exception {
		return datasource;
	}

	public static Connection getConnection() throws Exception {
		Connection connection;
		connection = getPoolDataSource().getConnection();
		return connection;
	}

	public static Connection getConnection(String serviceCode, String requestId) throws Exception {
		Connection connection;
		do {
			connection = getPoolDataSource().getConnection();
		} while (connection == null || !((ValidConnection) connection).isValid());
		return connection;
	}

	public static void close(Connection conn) {
		close(conn, null, null);
	}

	public static void close(Connection conn, String serviceCode, String requestId) {
		try {
			((ValidConnection) conn).setInvalid();
			Utils.close(conn);
			conn = null;
		} catch (Exception ex) {
			conn = null;
		}

	}

	public static void close(Connection conn, Object object) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
