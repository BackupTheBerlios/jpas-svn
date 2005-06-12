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
import org.jpas.gui.layouts.*;
import org.jpas.util.*;
import org.jpas.gui.components.*;
/**
 * @author Owner
 *
 */
public class JpasFrame extends JFrame
{
    private final JButton btnReminder = new JButton("Reminders");
    private final TransactionTable table = new TransactionTable();
    private final AccountTotalPanel totalPanel = new AccountTotalPanel();
    private final AccountList accountList = new AccountList();
    
    public JpasFrame()
    {
        super("JPAS - Java Personal Accounting Software");
        this.getContentPane().add(createMainPanel());
        setJMenuBar(ActionFactory.getInstance().createJMenuBar());
        initListeners();
        setSize(800, 550);
    }
    
    private void initListeners()
    {
    	accountList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(final ListSelectionEvent e)
			{
				final Account selected = (Account)accountList.getSelectedValue();
				System.out.println("name: " + selected.getName());
				
				table.setAccount(selected);
				totalPanel.setAccount(selected);
			}
		});
    	accountList.setSelectedIndex(0);
    	//accountList.getSelectionModel().setSelectionInterval(0, 0);
    }
    
    public JComponent createMainPanel()
    {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(createLeftPanel(), BorderLayout.WEST);
        panel.add(createRightPanel(), BorderLayout.CENTER);
        return panel;
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
    
    
    private JComponent createRightPanel()
    {
        final FlexGridLayout fgl = new FlexGridLayout(new int[]{0, 30}, new int[]{table.getPreferredSize().width});
        fgl.setFlexColumn(0, true);
        fgl.setFlexRow(0, true);
        final JPanel panel = new JPanel(fgl);
        
        final JViewport vp = new JViewport()
        {
            protected LayoutManager createLayoutManager()
            {
                return new ViewportLayout()
                {
                    public void layoutContainer(final Container parent)
                    {
                    	JViewport vp = (JViewport)parent;
                    	Component view = vp.getView();

                    	Dimension vpSize = vp.getSize();
                    	Dimension extentSize = vp.toViewCoordinates(vpSize);
                    	Dimension viewSize = new Dimension(view.getPreferredSize());

           	            viewSize.width = vpSize.width;
            	        
            	        if(vpSize.height > viewSize.height)
            	        {
            	            viewSize.height = vpSize.height;
            	        }
            	        
            	        Point viewPosition = vp.getViewPosition();

                    	if (vp.getParent().getComponentOrientation().isLeftToRight()) 
                    	{
                    	    if ((viewPosition.x + extentSize.width) > viewSize.width) 
                    	    {
                    	        viewPosition.x = Math.max(0, viewSize.width - extentSize.width);
                    	    }
                    	} 
                    	else 
                    	{
                    	    if (extentSize.width > viewSize.width) 
                    	    {
                    	        viewPosition.x = viewSize.width - extentSize.width;
                    	    } 
                    	    else 
                    	    {
                    	        viewPosition.x = Math.max(0, Math.min(viewSize.width - extentSize.width, viewPosition.x));
                    	    }
                    	}

                    	if ((viewPosition.y + extentSize.height) > viewSize.height) 
                    	{
                    	    viewPosition.y = Math.max(0, viewSize.height - extentSize.height);
                    	}

                    	vp.setViewPosition(viewPosition);
                    	vp.setViewSize(viewSize);
                    }
                };
                
            }
        };
        vp.setView(table);

        final JScrollPane tableScroll = new JScrollPane();
        tableScroll.setViewport(vp);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(tableScroll);
        panel.add(totalPanel);

        final JScrollPane panelScroll = new JScrollPane(panel);
        panelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        return panelScroll;
    }
    
    public static void main(String[] args)
    {
    }
}
