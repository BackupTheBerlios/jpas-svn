/*
 * Created on Oct 26, 2004
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
package org.jpas.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.JpasObserver;

/**
 * @author jsmith
 *
 */
public abstract class JpasListModel <T> extends AbstractListModel implements ComboBoxModel 
{
	private T selected = null;
	
	private List<T> data = new ArrayList<T>();

    final JpasObserver<T> observer = new JpasObserver<T>()
    {
        public void update(final JpasObservable<T> obs, final JpasDataChange<T> change)
        {
            populateData();

            fireContentsChanged(this, 0, data.size());
        }
    };

	
	public JpasListModel()
	{
		initObserver(observer);
	}
	
	protected abstract void initObserver(final JpasObserver<T> observer);
	protected abstract T[] loadData();
	
	public void reload()
	{
		populateData();
	}
	
	private void populateData()
	{
		data.clear();
		for(final T value: loadData())
		{
			data.add(value);
		}
		fireContentsChanged(this, 0, data.size());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(final Object anItem) 
	{
		this.selected = (T)anItem;
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem() 
	{
		return selected;
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() 
	{
		return data.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) 
	{
		return data.get(index);
	}

    public static void main(String[] args)
    {
    }

}
