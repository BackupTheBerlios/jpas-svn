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
package org.jpas.gui.models;

import org.jpas.model.Account;
import org.jpas.util.JpasObserver;
/**
 * @author jsmith
 *
 */
public class PayeeListModel extends JpasListModel<String>
{
	private Account account = null;
	
	public PayeeListModel()
	{
	}
	
	public void setAccount(final Account account)
	{
	    this.account = account;
	    populateData();
	}
	
	/* (non-Javadoc)
	 * @see org.jpas.gui.model.JpasListModel#initObserver(null)
	 */
	protected void initObserver(final JpasObserver observer) 
	{
	}

	/* (non-Javadoc)
	 * @see org.jpas.gui.model.JpasListModel#loadData()
	 */
	protected String[] loadData() 
	{
	    if(account == null)
	    {
	        return new String[0];
	    }
		return account.getAllPayees();
	}
	
	public void setSelectedItem(final Object anItem) 
	{
		super.setSelectedItem((String)anItem);
	}
}
