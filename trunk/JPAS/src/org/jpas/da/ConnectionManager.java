package org.jpas.da;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.jpas.util.PropertyManager;


/**
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004 Justin W Smith
 * @author Justin W Smith
 * @version 1.0
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
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
				defaultLogger.info("Connection to external database established");
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
				defaultLogger.info("Connection to internal database established");
				
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
