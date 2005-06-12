/*
 * Created on Oct 29, 2004
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

import org.jpas.gui.layouts.FlexGridLayout;
import org.jpas.model.*;

import javax.swing.*;

/**
 * @author jsmith
 *
 */
public class AccountTotalPanel extends JPanel
{
    private Account account;
    
    private final JLabel todayLabel = new JLabel("Balance(Today):");
    private final JLabel totalLabel = new JLabel("Balance:");
    
    private final JLabel balanceLabel = new JLabel();
    private final JLabel todayBalanceLabel = new JLabel();
    
    private final int[] columnWidths = {0, 80, 80, 80, 80};
    
    public AccountTotalPanel()
    {
        final FlexGridLayout fgl = new FlexGridLayout(new int[]{25}, columnWidths);
        fgl.setFlexColumn(0, true);
        
        setLayout(fgl);
        add(new JPanel());
        add(todayLabel);
        add(todayBalanceLabel);
        add(totalLabel);
        add(balanceLabel);
    }

    public void setAccount(final Account account)
    {
        this.account = account;
    }
    
    public Account getAccount()
    {
        return account;
    }
    
}
