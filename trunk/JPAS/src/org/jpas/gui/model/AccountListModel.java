/**
 * Created on Oct 4, 2004 - 6:35:57 PM
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

import javax.swing.*;
import java.util.*;
import org.jpas.model.*;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.JpasObserver;

/**
 * @author Justin W Smith
 *
 */
public class AccountListModel extends AbstractListModel
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -4606379655223716167L;

    final List<Account> data = new ArrayList<Account>();
    
    final JpasObserver<Account> observer = new JpasObserver<Account>()
    {
        public void update(JpasObservable<Account> obs, JpasDataChange<Account> change)
        {
            synchronized(AccountListModel.this)
            {
                data.clear();
                populateData();
            }
            final Runnable runnable = new Runnable()
            {
                public void run()
                {
                    AccountListModel.this.fireContentsChanged(AccountListModel.this, 0, data.size());
                }
            };
            if(SwingUtilities.isEventDispatchThread())
            {
                runnable.run();
            }
            else
            {
                SwingUtilities.invokeLater(runnable);
            }
        }
    };
    /**
     * 
     */
    public AccountListModel()
    {
        populateData();
        Account.getObservable().addObserver(observer);
    }
    
    private void populateData()
    {
        for(Account a : Account.getAllAccounts())
        {
            data.add(a);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public synchronized int getSize()
    {
        return data.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public synchronized Object getElementAt(int index)
    {
        return data.get(index);
    }

    public static void main(String[] args)
    {
    }
}
