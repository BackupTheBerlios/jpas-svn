package org.jpas.da;

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


		Connection tempConn = null;
		try
		{
			Class.forName("org.hsqldb.jdbcDriver");

			if(dbMode.equalsIgnoreCase("SERVER"))
			{
				final String dbName = PropertyManager.instance.getProperty(
												PropertyManager.DB_NAME,
												 "jpac_db");

				tempConn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/" + dbName,
													   "sa",
													   "");
			}
			else
			{
				final String dbFileDir = PropertyManager.instance.getProperty(
												PropertyManager.DB_FILE_PREFIX,
												 "db/");
				final String dbFilePrefix = PropertyManager.instance.getProperty(
												PropertyManager.DB_FILE_PREFIX,
												 "jpac_db");

				tempConn = DriverManager
					   .getConnection("jdbc:hsqldb:file:" + dbFileDir + dbFilePrefix,
									   "sa",
									   "");
				Runtime.getRuntime().addShutdownHook(new Thread()
					{
						public void run()
						{
							try
							{
								update("SHUTDOWN");
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
			defaultLogger.error("Unable to open database connection!", sqle);
			System.exit(1);
		}
		conn = tempConn;

		Runtime.getRuntime().addShutdownHook(new Thread()
			{
				public void run()
				{
					try
					{
						conn.close();
					}
					catch(final SQLException sqle)
					{
						defaultLogger.error("Unable to close the database connection!", sqle);
					}
				}
			});
	}

	public ResultSet query(final String sql) throws SQLException
	{
		final Statement st = conn.createStatement();
		try
		{
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
			return st.executeUpdate(sql);
		}
		finally
		{
			st.close();
		}
	}
}
