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
import javax.swing.event.*;

import com.toedter.calendar.*;
import org.jpas.gui.util.*;
import org.jpas.model.*;
import org.jpas.util.*;
import org.jpas.gui.components.*;
/**
 * @author Owner
 *
 */
public class JpasFrame extends JFrame
{
    private final JButton btnReminder = new JButton("Reminders");
    private final CardLayout tableLayout = new CardLayout();
    private final JPanel tablePanel = new JPanel(tableLayout); 
    private final AccountList accountList = new AccountList();
    
    public JpasFrame()
    {
        super("JPAS - Java Personal Accounting Software");
        this.getContentPane().add(createMainPanel());
        setJMenuBar(ActionFactory.getInstance().createJMenuBar());
        initTablePanel();
        initListeners();
        pack();
    }
    
    private void initListeners()
    {
    	accountList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(final ListSelectionEvent e)
			{
				final Account selected = (Account)accountList.getSelectedValue();
				System.out.println("name: " + selected.getName());
				
				tableLayout.show(tablePanel, selected.getName());
				tablePanel.invalidate();
			}
		});
    	
    	Account.getObservable().addObserver(new JpasObserver<Account>()
    			{
    				public void update(final JpasObservable<Account> observable, final JpasDataChange<Account> change ) 
    				{
    					if(change instanceof JpasDataChange.Add || change instanceof JpasDataChange.Delete)
    					{
    						initTablePanel();
    					}
    				}
    			});
    }
    
    public JComponent createMainPanel()
    {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(new JScrollPane(tablePanel));
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
        
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panel.add(accountList, BorderLayout.CENTER);
       
        return panel;
    }

    public JPanel createReminderPanel()
    {
        final JPanel panel = new JPanel();
        panel.add(btnReminder, BorderLayout.CENTER);
        return panel;
    }
    
    
    public void initTablePanel()
    {
        tablePanel.removeAll();
        
    	final Account[] accounts = Account.getAllAccounts();

		for(int i = 0; i < accounts.length; i++)
		{
			System.out.println("adding: " + accounts[i].getName());
			tablePanel.add(new TransactionTable(accounts[i]), accounts[i].getName());
		}
		if(accounts.length > 0)
		{
			tableLayout.show( tablePanel, accounts[0].getName());
		}
		else
		{
			tablePanel.add(new JLabel("No accounts exist!"));
		}
		
    }
    
    
    public static void main(String[] args)
    {
    }
}
