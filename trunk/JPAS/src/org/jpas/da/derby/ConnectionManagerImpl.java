package org.jpas.da.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        String dbName = "jdbc:derby:";

        Connection tempConn = null;

        try
        {
            if(dbMode.equalsIgnoreCase("SERVER"))
            {
                final String dbDriver = "org.apache.derby.jdbc.ClientDriver";

                Class.forName(dbDriver).newInstance();
                
                dbName += "//localhost/" + PropertyManager.getInstance().getProperty(
                                                    PropertyManager.DB_NAME,
                                                    "jpasdb");
                tempConn = DriverManager.getConnection(dbName,
                                                       "sa",
                                                       "sa");
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
                final String dbDriver = "org.apache.derby.jdbc.EmbeddedDriver";

                Class.forName(dbDriver).newInstance();
                
                final String dbFileDir = PropertyManager.getInstance().getProperty(
                                                PropertyManager.DB_FILE_DIR,
                                                 "jpasdb");

                dbName += "directory::" + dbFileDir;
                tempConn = DriverManager
                       .getConnection(dbName,
                                       "sa",
                                       "sa");
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
