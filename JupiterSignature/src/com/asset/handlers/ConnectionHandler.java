package com.asset.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.comm.CommDriver;

import com.asset.utilities.Constants;

public class ConnectionHandler {
	private static Object object = new Object();
	private String db_Name;
	private String schema_Name;
	private String userName;
	private String password;
	private String server_ip;
	private String port;
	private String db_driver;
	private String connectionUrl;
	private String[] tables;
	private String[] coulmns;
	private Connection con;
	static String drivername = "com.sun.comm.Win32Driver";

	private ConnectionHandler() {

	}

	public ConnectionHandler(String db_driver, String server_ip, String port,
			String db_Name, String schema_Name, String userName,
			String password, String[] tables, String[] coulmns) {
		// Create a variable for the connection string.
		this.setDb_Name(db_Name);
		this.setDb_driver(db_driver);
		this.setUserName(userName);
		this.setPassword(password);
		this.setServer_ip(server_ip);
		this.setPort(port);
		this.setSchema_Name(schema_Name);
		this.setConnection_url(db_driver);
		this.setTables(tables);
		this.setCoulmns(coulmns);
	}

//	public static ConnectionHandler getInstance(String db_driver,
//			String server_ip, String port, String db_Name, String schema_Name,
//			String userName, String password, String[] tables, String[] coulmns) {
//		if (connectionhandler == null) {
//			synchronized (object) {
//				if (connectionhandler == null) {
//					connectionhandler = new ConnectionHandler(db_driver,
//							server_ip, port, db_Name, schema_Name, userName,
//							password, tables, coulmns);
//				}
//			}
//		}
//		return connectionhandler;
//	}

	public boolean getConnection() {
		Statement stmt = null;
		Connection con = null;
		try {
			if (this.getDb_driver().equals(Constants.sqlserver)) {

				try {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				} catch (ClassNotFoundException e) {
					System.out.println("Missing SQL Server Driver?");
					e.printStackTrace();
					return false;
				}
				System.out.println("Create connection");

				con = DriverManager.getConnection(this
						.getConnectionUrl());
				if (con != null) {

					System.out.println("Connected to SQL Server");
					String testSQL = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ;";
					Statement st = con.createStatement();
					ResultSet rs = st.executeQuery(testSQL);
					while (rs.next()) {
						System.out.println(rs.getString("TABLE_NAME"));
					}
					this.con = con;
					return true;

				} else {
					System.out
							.println("Unable to connect to SQL Server database");
					return false;

				}

			} else if (this.getDb_driver()
					.equals(Constants.oracle)) {

				System.out.println("-------- Oracle JDBC Connection  ------");

				try {

					Class.forName("oracle.jdbc.driver.OracleDriver");

				} catch (ClassNotFoundException e) {

					System.out.println("Missing Oracle JDBC Driver?");
					e.printStackTrace();
					return false;

				}
				// Create the connection
				System.out.println("Oracle JDBC Driver Registered!");

				System.out.println("Create connection");
				con = DriverManager.getConnection(
						this.getConnectionUrl(),
						this.getUserName(),
						this.getPassword());
				// "jdbc:oracle:thin:@localhost:1521:xe","system","oracle"
				// con = DriverManager.getConnection(
				// "jdbc:oracle:thin:@10.0.30.8:1521:UTF8","jupiter4b50","jupiter4b50");
				// //jdbc:oracle:thin:@10.0.30.8:1521:UTF8,JUPITER4B50,JUPITER4B50
				if (con != null) {
					System.out.println("Connected to ORACLE Server");
					String testSQL = "SELECT FIELD0,FIELD2_1 FROM JUPITER4B50.BINARY_FIELD_TEST ";
					Statement st = con.createStatement();
					ResultSet rs = st.executeQuery(testSQL);// ResultSet
															// rs=stmt.executeQuery("select * from emp");
					while (rs.next()) {
						System.out.println(rs.getString("FIELD0") + "  "
								+ rs.getString("FIELD2_1"));
					}
					this.con = con;
					if (rs != null)
						try {
							rs.close();
						} catch (Exception e) {
						}
					if (st != null)
						try {
							st.close();
						} catch (Exception e) {
						}
					return true;

				} else {
					System.out
							.println("Unable to connect to SQL Server database");
					return false;

				}
			} else {
				return false;
			}
		}

		// Handle any errors that may have occurred.
		catch (Exception e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return false;

		}

		finally {

			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
		}
	}

	public void start_generating_SQLScript(String[] tables, String[] coulmns) {

		try {
			StringBuilder sql_statement = new StringBuilder();
			SignatureHandler signatureHandler = new SignatureHandler();
			CommDriver driver = (CommDriver) Class.forName(drivername)
					.newInstance();
			driver.initialize();
			if (this.getDb_driver().equals(Constants.sqlserver)) {
				for (int i = 0; i < coulmns.length; i++) {
					sql_statement.append(Constants.select + " FIELD0");
					sql_statement.append(", " + coulmns[i] + " ");
					sql_statement.append(Constants.from + " "
							+ this.getDb_Name() + "." + tables[i]
							+ " ");
					sql_statement.append(Constants.where + " " + coulmns[i]
							+ " IS NOT NULL ");
					ResultSet rs = this.runQuery(sql_statement.toString());
					signatureHandler.convert(rs, tables[i], coulmns[i]);
				}
			} else if (this.getDb_driver()
					.equals(Constants.oracle)) {
				for (int i = 0; i < coulmns.length; i++) {
					sql_statement.append(Constants.select + " FIELD0");
					sql_statement.append(", " + coulmns[i] + " ");
					sql_statement
							.append(Constants.from + " " + tables[i] + " ");
					sql_statement.append(Constants.where + " " + coulmns[i]
							+ " IS NOT NULL ");
					ResultSet rs = this.runQuery(sql_statement.toString());
					signatureHandler.convert(rs, tables[i], coulmns[i]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}


	public ResultSet runQuery(String query) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = this.con.createStatement();
			rs = stmt.executeQuery(query);

			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getConnectionUrl() {
		return this.connectionUrl;
	}

	private void setConnection_url(String db_driver) {
		if (db_driver.equals(Constants.sqlserver)) {
			this.connectionUrl = Constants.sql_conn
					+ this.server_ip + ":"
					+ this.port + ";user="
					+ this.userName + ";password="
					+ this.password;
		} else if (db_driver.equals(Constants.oracle)) {
			this.connectionUrl = Constants.oracle_conn + "@"
					+ this.server_ip + ":"
					+ this.port + ":" + this.db_Name;// jdbc:oracle:thin:@10.0.30.8:1521:UTF8
		}
	}

	private void setConnectionUrl(String ConnectionUrl) {
		this.connectionUrl = ConnectionUrl;
	}

	private String getDb_driver() {
		return db_driver;
	}

	private void setDb_driver(String db_driver) {
		this.db_driver = db_driver;
	}

	private String getDb_Name() {
		return db_Name;
	}

	private void setDb_Name(String db_Name) {
		this.db_Name = db_Name;
	}

	private String getUserName() {
		return userName;
	}

	private void setUserName(String userName) {
		this.userName = userName;
	}

	private String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	private String getServer_ip() {
		return server_ip;
	}

	private void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}

	private String getPort() {
		return port;
	}

	private void setPort(String port) {
		this.port = port;
	}

	public String[] getTables() {
		return tables;
	}

	public void setTables(String[] tables) {
		this.tables = tables;
	}

	public String[] getCoulmns() {
		return coulmns;
	}

	public void setCoulmns(String[] coulmns) {
		this.coulmns = coulmns;
	}

	private String getSchema_Name() {
		return schema_Name;
	}

	private void setSchema_Name(String schema_Name) {
		this.schema_Name = schema_Name;
	}

}
