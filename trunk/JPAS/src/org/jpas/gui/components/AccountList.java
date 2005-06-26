/**
 * Created on Oct 4, 2004 - 6:27:58 PM
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
package org.jpas.gui.components;

import java.awt.*;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jpas.model.*;

import org.jpas.gui.models.AccountListModel;
import org.jpas.gui.models.JpasListModel;
import org.jpas.gui.renderers.AccountListCellRenderer;
import org.jpas.gui.util.*;
/**
 * @author Justin W Smith
 *
 */
public class AccountList extends JList implements Checkpointable
{
    private boolean selectionChanged = false;
    
	public JpasListModel<Account> model = new AccountListModel();

    public AccountList()
    {
        super();
        setModel(model);
        model.reload();
        setCellRenderer(new AccountListCellRenderer(true));
        setBackground(new Color(239, 239, 239));
        
        getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            public void valueChanged(ListSelectionEvent e)
            {
                selectionChanged = true;
            }
        });
    }
    
    public Dimension getPreferredSize()
    {
        final Dimension dim = getCellRenderer().getListCellRendererComponent(this, "WWWWWWWWWWWW", 0, false, false).getPreferredSize();
        dim.height *= getModel().getSize();
        return dim;
    }
    
    public static void main(String[] args)
    {
    }

    public void resetCheckpoint()
    {
        selectionChanged = false;
    }

    public boolean hasChanged()
    {
        return selectionChanged;
    }
}
