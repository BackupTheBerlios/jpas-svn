package org.jpas.util;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class JpasObservableImpl implements JpasObservable
{
	private final Set<JpasObserver> observerSet = new HashSet<JpasObserver>();
	private final Set<WeakReference<JpasObserver>> observerWeakSet = new HashSet<WeakReference<JpasObserver>>();
	
	public void addObserver(final JpasObserver o)
	{
		synchronized(observerSet)
		{
			observerSet.add(o);
		}
	}
	
	public void addObserverWeak(JpasObserver ob)
	{
		synchronized(observerSet)
		{
			observerWeakSet.add(new WeakReference<JpasObserver>(ob));
		}
	}
	
	public boolean deleteObserver(final JpasObserver o)
	{
		synchronized(observerSet)
		{
			if(!observerSet.remove(o))
			{
				for(WeakReference<JpasObserver> wr : observerWeakSet)
				{
					final JpasObserver ob = wr.get();
					
					if(ob == null)
					{
						observerWeakSet.remove(wr);
						continue;
					}
					if(ob.equals(o))
					{
						return observerWeakSet.remove(wr);
					}
				}
				return false;
			}
			return true;
		}
	}
	
	public void notifyObservers(JpasDataChange arg)
	{
		synchronized(observerSet)
		{
			for(JpasObserver ob : observerSet)
			{
				ob.update(this, arg);
			}
			for(WeakReference<JpasObserver> wr : observerWeakSet)
			{
				final JpasObserver ob = wr.get();
				
				if(ob == null)
				{
					observerWeakSet.remove(wr);
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
