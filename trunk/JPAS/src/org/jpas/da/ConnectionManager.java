package org.jpas.da;

import java.sql.*;

import org.apache.log4j.Logger;
import org.hsqldb.Server;


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

public abstract class ConnectionManager
{
	private static final Logger defaultLogger = Logger.getLogger(ConnectionManager.class);
    
    protected Connection conn;
    
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
	
	public static void main(final String[] args)
	{
	    System.out.println(new Server().getProductVersion());
	}
}
