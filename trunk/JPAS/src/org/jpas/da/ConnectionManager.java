package org.jpas.da;

import org.hsqldb.jdbcDriver;
import org.jpas.util.PropertyManager;
import org.apache.log4j.*;
import java.sql.*;


/**
 * <p>Title: JPAS</p>
 * <p>Description: Java based Personal Accounting System</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>License: Distributed under the terms of the GPL v2</p>
 * @author Justin Smith
 * @version 1.0
 */

public class ConnectionManager
{
	private static final Logger defaultLogger = Logger.getLogger(ConnectionManager.class);
	private static final ConnectionManager instance = new ConnectionManager();

	public static ConnectionManager getInstance()
	{
		return instance;
	}

	public final Connection conn;

	private ConnectionManager()
	{
		final String dbMode = PropertyManager.instance.getProperty(
									   PropertyManager.DB_MODE,
										"SERVER");
		final String dbDriver = "org.hsqldb.jdbcDriver";
		String dbName = "jdbc:hsqldb:";

		Connection tempConn = null;

		try
		{
			
			Class.forName(dbDriver).newInstance();
			
			if(dbMode.equalsIgnoreCase("SERVER"))
			{
				dbName += "hsql://localhost/" + PropertyManager.instance.getProperty(
													PropertyManager.DB_NAME,
				 									"jpasdb");
				tempConn = DriverManager.getConnection(dbName,
													   "sa",
													   "");
				Runtime.getRuntime().addShutdownHook(new Thread()
						{
							public void run()
							{
								try
								{
									defaultLogger.info("Closing database connection!");
									if(!conn.isClosed())
									{
										conn.close();
									}
								}
								catch(final SQLException sqle)
								{
									defaultLogger.error("Unable to close the database connection!", sqle);
								}
							}
						});
			}
			else
			{
				final String dbFileDir = PropertyManager.instance.getProperty(
												PropertyManager.DB_FILE_PREFIX,
												 "db/");
				final String dbFilePrefix = PropertyManager.instance.getProperty(
												PropertyManager.DB_FILE_PREFIX,
												 "jpasdb");

				dbName += "file:" + dbFileDir + dbFilePrefix;
				tempConn = DriverManager
					   .getConnection(dbName,
									   "sa",
									   "");
				Runtime.getRuntime().addShutdownHook(new Thread()
					{
						public void run()
						{
							try
							{
								defaultLogger.info("Shutting down database!");
								update("SHUTDOWN");
								
								if(!conn.isClosed())
								{
									conn.close();
								}
							}
							catch(final SQLException sqle)
							{
								defaultLogger.error("Unable to close the database connection!", sqle);
							}
						}
					});

			}
		}
		catch(final ClassNotFoundException cnfe)
		{
			defaultLogger.error("Database driver not found in the Classpath!", cnfe);
			System.exit(1);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("Unable to open database connection: \"" + dbName + "\"", sqle);
			System.exit(1);
		}
		catch(final Exception e)
		{
			defaultLogger.error("Problem creating DatabaseManager!", e);
			System.exit(1);
		}
		conn = tempConn;

	}

	public ResultSet query(final String sql) throws SQLException
	{
		final Statement st = conn.createStatement();
		try
		{
		    defaultLogger.debug("Query: " + sql);
			return st.executeQuery(sql);
		}
		finally
		{
			st.close();
		}
	}

	public int update(final String sql) throws SQLException
	{
		final Statement st = conn.createStatement();
		try
		{
		    defaultLogger.debug("Update: " + sql);
			return st.executeUpdate(sql);
		}
		finally
		{
			st.close();
		}
	}
}
