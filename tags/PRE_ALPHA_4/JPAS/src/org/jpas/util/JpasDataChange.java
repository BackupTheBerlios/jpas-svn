/*
 * Created on Oct 4, 2004
 *
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
package org.jpas.util;

/**
 * @author jsmith
 *
 */
public class JpasDataChange<V> 
{
	private V value;
	
	JpasDataChange(final V value)
	{
		this.value = value;
	}
	
	public V getValue()
	{
		return value;
	}

	public static class Modify<V> extends JpasDataChange<V>
	{
		public Modify(final V value)
		{
			super(value);
		}
	}

	public static class Delete<V> extends JpasDataChange<V>
	{
		public Delete(final V value)
		{
			super(value);
		}
	}
	
	public static class Add<V> extends JpasDataChange<V>
	{
		public Add(final V value)
		{
			super(value);
		}
	}

}
