package org.jpas.util;

import java.io.*;
import java.util.Observable;
import java.util.Properties;

import java.util.prefs.*;

import org.apache.log4j.Logger;

/**
 * <p>Title: JPAS</p>
 * <p>Description: Java based Personal Accounting System</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>License: Distributed under the terms of the GPL v2</p>
 * @author Justin Smith
 * @version 1.0
 */

public class PropertyManager
{
    public static PropertyManagerKey DB_FILE_DIR = new PropertyManagerKey("db.file.dir");
    public static PropertyManagerKey DB_FILE_PREFIX = new PropertyManagerKey("db.file.prefix");
    public static PropertyManagerKey DB_NAME = new PropertyManagerKey("db.name");
    public static PropertyManagerKey DB_MODE = new PropertyManagerKey("db.mode");
    public static PropertyManagerKey DB_IMPL = new PropertyManagerKey("db.impl");
    public static PropertyManagerKey PREFS_USER_FILE = new PropertyManagerKey("prefs.user_file");
    public static PropertyManagerKey PREFS_SYSTEM_FILE = new PropertyManagerKey("prefs.system_file");

    
    private static final Logger defaultLogger = Logger.getLogger(PropertyManager.class);
	private static final PropertyManager instance = new PropertyManager();
    
    public static final PropertyManager getInstance()
    {
        return instance;
    }

	public static class PropertyManagerKey extends Observable
	{
		private final String keyStr;
		private PropertyManagerKey(final String str)
		{
			this.keyStr = str;
		}

		private void broadcastChange(final String value)
		{
			synchronized(this)
			{
				setChanged();
				super.notifyObservers(value);
				clearChanged();
			}
		}
		public void notifyObservers()
		{
			throw new UnsupportedOperationException("notifyObservers not supported!");
		}

		public void notifyObservers(final Object o)
		{
			throw new UnsupportedOperationException("notifyObservers not supported!");
		}
	}

	private final Properties props = new Properties();

	private PropertyManager()
	{
		final Properties systemProps = System.getProperties();
		final String propFileName = systemProps.getProperty(
			"properties.file", "jpas.properties");
	  	try
		{
			final InputStream in = new FileInputStream(propFileName);
			props.load(in);
		}
		catch(final IOException ioe)
		{
			defaultLogger.error("Exception while loading properties!", ioe);
		}

        try
        {
            Preferences.importPreferences(new FileInputStream(getProperty(PREFS_USER_FILE, "userPrefs.xml")));
            Preferences.importPreferences(new FileInputStream(getProperty(PREFS_SYSTEM_FILE, "systemPrefs.xml")));
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
		Runtime.getRuntime().addShutdownHook(new Thread()
			{
				public void run()
				{
					try
					{
                        try
                        {
                            Preferences.userRoot().exportSubtree(new FileOutputStream(getProperty(PREFS_USER_FILE, "userPrefs.xml"), false));
                            Preferences.systemRoot().exportSubtree(new FileOutputStream(getProperty(PREFS_SYSTEM_FILE, "systemPrefs.xml"), false));
                        }
                        catch(final BackingStoreException bse)
                        {
                            defaultLogger.error("Cannot write preferences to file.");
                        }
						props.store(new FileOutputStream(propFileName, false), "JPAS Properties");
					}
					catch(final IOException ioe)
					{
						defaultLogger.error("Cannot write properties to file: " + propFileName);
					}
				}
			});
	}

	public String getProperty(final PropertyManagerKey key)
	{
		synchronized(key)
		{
			return props.getProperty(key.keyStr);
		}
	}

	public String getProperty(final PropertyManagerKey key, final String defaultValue)
	{
		synchronized(key)
		{
			final String value = props.getProperty(key.keyStr);
			if(value == null)
			{
				setProperty(key, defaultValue);
				return defaultValue;
			}
			return value;
		}
	}

	public void setProperty(final PropertyManagerKey key, final String value)
	{
		synchronized(key)
		{
			props.put(key.keyStr, value);
			key.broadcastChange(value);
		}
	}
}
