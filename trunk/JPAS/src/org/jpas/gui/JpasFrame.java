/**
 * Created on Oct 2, 2004 - 11:30:10 PM
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
package org.jpas.gui;

import java.awt.*;
import javax.swing.*;
import com.toedter.calendar.*;
import org.jpas.gui.util.*;
import org.jpas.model.*;
import org.jpas.gui.components.*;
/**
 * @author Owner
 *
 */
public class JpasFrame extends JFrame
{
    private JButton btnReminder = new JButton("Reminders");
    
    public JpasFrame()
    {
        super("JPAS - Java Personal Accounting Software");
        this.getContentPane().add(createMainPanel());
        setJMenuBar(ActionFactory.getInstance().createJMenuBar());
        pack();
    }
    
    public JComponent createMainPanel()
    {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());
        return splitPane;
    }
    
    public JPanel createLeftPanel()
    {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(createAccountListPanel(), BorderLayout.CENTER);
        panel.add(createReminderPanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    public JPanel createAccountListPanel()
    {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new AccountList(), BorderLayout.CENTER);
       
        return panel;
    }

    public JPanel createReminderPanel()
    {
        final JPanel panel = new JPanel();
        panel.add(btnReminder, BorderLayout.CENTER);
        return panel;
    }
    
    
    public JPanel createRightPanel()
    {
        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane( new TransactionTable(Account.getAllAccounts()[0])));
        return panel;
    }
    
    
    public static void main(String[] args)
    {
    }
}
