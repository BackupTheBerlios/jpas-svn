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
package org.jpas.gui.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.sql.Date;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

import org.jpas.gui.components.*;
import org.jpas.gui.layouts.FlexGridLayout;
import org.jpas.model.*;

import com.toedter.calendar.JDateChooser;

/**
 * @author jsmith
 *
 */
public class TransactionTableCellEditor extends AbstractCellEditor implements TableCellEditor 
{
	private final JPanel cellPanel = new JPanel();
    
	private final JDateChooser dateChooser;
	private final JComboBox numList;
	private final PayeeComboBox payeeList;
	private final JTextField withdrawField;
	private final JTextField depositField;
	private final JTextField memoField;
	private final JLabel balanceLabel;
	private final JComboBox categoryList;
	private final JLabel categoryLabel = new JLabel("[SPLIT]");
	private final JButton btnEnter = new JButton("Enter");
	private final JButton btnSplit = new JButton("Split");
	private final JButton btnDelete = new JButton("Delete");
	
	private final JPanel splitPanel = new JPanel(new GridLayout(1, 2));
	
	
	private final int[] columnWidths;
	private final int[] rowHeights;
	
	private Account account;

    /**
     * 
     */
    public TransactionTableCellEditor(final int[] columnWidths, final int[] rowHeights)
    {
        this.columnWidths = columnWidths;
        this.rowHeights = rowHeights;
        cellPanel.setOpaque(false);
        cellPanel.addMouseListener(new MouseAdapter()
		        {
		    		public void mouseClicked(final MouseEvent me)
		    		{
		    		    me.consume();
		    		}
		        });
    	dateChooser = new JDateChooser("MM/dd/yyyy", false);
    	numList = new JComboBox(new String[]{"TXFR", "ATM", "100"});
    	numList.setEditable(true);
    	payeeList = new PayeeComboBox();
    	payeeList.setEditable(true);
    	withdrawField = new JTextField();
    	depositField = new JTextField();
    	memoField = new JTextField();
    	balanceLabel = new JLabel();
    	categoryList = new CategoryComboBox();
    	

        init();
    }
    
    private void init()
    {
        final FlexGridLayout layout = new FlexGridLayout(rowHeights, columnWidths);
        layout.setFlexColumn(2, true);
        cellPanel.setLayout(layout);
    	cellPanel.add(dateChooser);
    	cellPanel.add(numList);
    	cellPanel.add(payeeList);
    	cellPanel.add(withdrawField);
    	cellPanel.add(depositField);
    	cellPanel.add(balanceLabel);
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(splitPanel);
    	cellPanel.add(btnEnter);
    	cellPanel.add(btnSplit);
    	cellPanel.add(btnDelete);
    }
    
    public void setAccount(final Account account)
    {
        assert(account != null);
        this.account = account;
        payeeList.setAccount(account);
    }
    
    public Account getAccount()
    {
        return account;
    }

    private void setCategoryPanel()
    {
        splitPanel.removeAll();
        splitPanel.add(categoryList);
        splitPanel.add(memoField);
        splitPanel.invalidate();
    }
    
    private void setSplitPanel()
    {
        splitPanel.removeAll();
        splitPanel.add(categoryLabel);
        splitPanel.add(memoField);
        splitPanel.invalidate();
    }
    
    private JPanel createEmptyPanel()
    {
    	final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
    	return panel;
    }
    
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
    {
        if(value == null)
        {
        	dateChooser.setDate(new java.util.Date());
        	numList.setSelectedItem("");
        	payeeList.setSelectedItem("");
        	withdrawField.setText("");
        	depositField.setText("");
        	balanceLabel.setText("");
        	categoryList.getModel().setSelectedItem(null);
    	    setCategoryPanel();
        }
        else
        {
	        final Transaction trans = (Transaction)value;
	        
	        dateChooser.setDate(trans.getDate());
        	numList.setSelectedItem(trans.getNum());
        	payeeList.setSelectedItem(trans.getPayee());
        	
        	final long amount = trans.getAmount();
        	if(amount >= 0)
        	{
	        	withdrawField.setText(String.valueOf(trans.getAmount()));
	        	depositField.setText("");
        	}
        	else
        	{
	        	withdrawField.setText("");
	        	depositField.setText(String.valueOf(-trans.getAmount()));
        	}
        	balanceLabel.setText("");
        	
        	final TransactionTransfer[] transfers = trans.getAllTransfers();
        	if(transfers.length == 0)
        	{
        	    setCategoryPanel();
            	categoryList.setSelectedItem("");
        	}
        	else if(!trans.getAccount().equals(account))
        	{
        	    System.out.println(trans.getAccount().getName());
        	    System.out.println(account.getName());
        	    setCategoryPanel();
        	    categoryList.getModel().setSelectedItem(Category.getCategoryForAccount(trans.getAccount()).getName());
        	}
        	else if(transfers.length == 1)
        	{
        	    setCategoryPanel();
        	    categoryList.setSelectedItem(transfers[0].getCategory());
        	}
        	else
        	{
        	    setSplitPanel();
        	    //categoryList.setSelectedItem("[SPLIT]");
        	    //categoryList.setEnabled(false);
        	}

        }
        return cellPanel;
    }
    
    public Object getCellEditorValue()
    {
        return null;
    }
    
    public static void main(String[] args)
    {
    }
}
