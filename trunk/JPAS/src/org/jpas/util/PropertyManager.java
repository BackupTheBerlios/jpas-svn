package org.jpas.util;

import java.util.*;
import java.io.*;
import org.apache.log4j.*;

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
	private static final Logger defaultLogger = Logger.getLogger(PropertyManager.class);
	public static final PropertyManager instance = new PropertyManager();

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

	public static PropertyManagerKey DB_FILE_DIR = new PropertyManagerKey("DB_FILE_DIR");
	public static PropertyManagerKey DB_FILE_PREFIX = new PropertyManagerKey("DB_FILE_PREFIX");
	public static PropertyManagerKey DB_NAME = new PropertyManagerKey("DB_NAME");
	public static PropertyManagerKey DB_MODE = new PropertyManagerKey("DB_MODE");

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

		Runtime.getRuntime().addShutdownHook(new Thread()
			{
				public void run()
				{
					try
					{
						props.store(new FileOutputStream(propFileName, false), "JPAS Properties");
					}
					catch(final IOException ioe)
					{
						defaultLogger.warn("Cannot write properties to file: " + propFileName);
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
