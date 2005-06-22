package org.jpas.da.hsqldb;

import java.sql.*;

import org.apache.log4j.Logger;
import org.jpas.da.ConnectionManager;
import org.jpas.util.PropertyManager;

public class ConnectionManagerImpl extends ConnectionManager
{
    private static final Logger defaultLogger = Logger.getLogger(ConnectionManagerImpl.class);

    public ConnectionManagerImpl()
    {
        final String dbMode = PropertyManager.getInstance().getProperty(
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
                dbName += "hsql://localhost/" + PropertyManager.getInstance().getProperty(
                                                    PropertyManager.DB_NAME,
                                                    "jpasdb");
                tempConn = DriverManager.getConnection(dbName,
                                                       "sa",
                                                       "");
                defaultLogger.info("Connection to external database established: \""
                        + dbName + "\"");
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
                final String dbFileDir = PropertyManager.getInstance().getProperty(
                                                PropertyManager.DB_FILE_DIR,
                                                 "db/");
                final String dbFilePrefix = PropertyManager.getInstance().getProperty(
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
}
