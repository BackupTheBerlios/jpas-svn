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
public class JpasDataChange
{
	private Object value;
	
	JpasDataChange(final Object value)
	{
		this.value = value;
	}
	
	public Object getValue()
	{
		return value;
	}

	public static class AmountModify extends JpasDataChange
	{
		public AmountModify(final Object value)
		{
			super(value);
		}
	}

	
	public static class Modify extends JpasDataChange
	{
		public Modify(final Object value)
		{
			super(value);
		}
	}

	public static class Delete extends JpasDataChange
	{
		public Delete(final Object value)
		{
			super(value);
		}
	}
	
	public static class Add extends JpasDataChange
	{
		public Add(final Object value)
		{
			super(value);
		}
	}

}
