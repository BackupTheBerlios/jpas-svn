/**
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

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @author jsmith
 *
 */
public class JpasObservable<V>
{
	private final Set<WeakReference<JpasObserver<V>>> observerSet = new HashSet<WeakReference<JpasObserver<V>>>();
	
	public void addObserver(final JpasObserver<V> o)
	{
		synchronized(observerSet)
		{
			observerSet.add(new WeakReference<JpasObserver<V>>(o));
		}
	}
	
	public void deleteObserver(final JpasObserver<V> o)
	{
		synchronized(observerSet)
		{
			observerSet.remove(o);
		}
	}
	
	public void notifyObservers(JpasDataChange<V> arg)
	{
		synchronized(observerSet)
		{
			for(WeakReference<JpasObserver<V>> wr : observerSet)
			{
				final JpasObserver<V> ob = wr.get();
				if(ob == null)
				{
					observerSet.remove(wr);
					continue;
				}
				
				ob.update(this, arg);
			}
		}
	}
	
	protected void deleteObservers()
	{
		synchronized(observerSet)
		{
			observerSet.clear();
		}
	}
	
	public int countObservers()
	{
		synchronized(observerSet)
		{
			return observerSet.size();
		}
	}
}
