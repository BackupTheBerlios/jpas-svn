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

import java.awt.Color;

import javax.swing.JList;

import org.jpas.gui.model.AccountListModel;
import org.jpas.gui.model.JpasListModel;
import org.jpas.gui.renderers.AccountListCellRenderer;

/**
 * @author Justin W Smith
 *
 */
public class AccountList extends JList
{
    //final ListSelectionModel model = new DefaultListSelectionModel();
    
 
	public JpasListModel model = new AccountListModel();



    public AccountList()
    {
        super();
        setModel(model);
        model.reload();
        setCellRenderer(new AccountListCellRenderer(true));
        setBackground(new Color(239, 239, 239));
        setSelectedIndex(0);
    }
    
    
    
    public static void main(String[] args)
    {
    }
}
