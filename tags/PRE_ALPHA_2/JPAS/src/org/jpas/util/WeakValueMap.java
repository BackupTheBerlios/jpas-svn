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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jsmith
 *
 */
public class WeakValueMap<K, V>
{
	private static volatile long lastCleanTimeStamp = 0;
	private static final long cleanInterval = 15000;
	private final Map<K, WeakReference<V>> map = new HashMap<K, WeakReference<V>>();
	
	public WeakValueMap()
	{
		
	}
	
	public V remove(final K key)
	{
		synchronized(map)
		{
			clean();
			final WeakReference<V> wr = map.remove(key);
			return wr.get();
		}
	}
	
	public V put(final K key, V value)
	{
		synchronized(map)
		{
			clean();
			final WeakReference<V> wr = map.get(key);
			map.put(key, new WeakReference<V>(value));
			
			return wr == null ? null : wr.get();
		}
	}
	
	public V get(final K key)
	{
		synchronized(map)
		{
			clean();
			final WeakReference<V> wr = map.get(key);
			return wr == null ? null : wr.get();
		}
	}
	
	private void clean()
	{
		final long currentTime = System.currentTimeMillis();
		if((currentTime - lastCleanTimeStamp) > cleanInterval)
		{
			final Iterator<K> iter = map.keySet().iterator();
			while(iter.hasNext())
			{
				final WeakReference<V> wr = map.get(iter.next());
				if(wr != null || wr.get() == null)
				{
					iter.remove();
				}
			}
			lastCleanTimeStamp = System.currentTimeMillis();
		}
	}
	
	public void removeOldValues()
	{
		synchronized(map)
		{
			clean();
		}
	}
	
	public int size()
	{
		synchronized(map)
		{
			clean();
			return map.size();
		}
	}
}
