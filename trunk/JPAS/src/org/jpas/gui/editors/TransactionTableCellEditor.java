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

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
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
    
	private final DateChooser dateChooser;
	private final JComboBox numList;
	private final JComboBox payeeList;
	private final JTextField withdrawField;
	private final JTextField depositField;
	private final JTextField memoField;
	private final JLabel balanceLabel;
	private final JComboBox categoryList;
	
	//final JLabel label = new JLabel(" ");

    /**
     * 
     */
    public TransactionTableCellEditor(final Account account)
    {
        cellPanel.setOpaque(true);
        cellPanel.setBackground(Color.white);

    	dateChooser = new DateChooser();
    	numList = new JComboBox(new String[]{"TXFR", "ATM", "100"});
    	numList.setEditable(true);
    	payeeList = new PayeeComboBox(account);
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
        final FlexGridLayout layout = new FlexGridLayout(new int[]{18, 18}, new int[]{105, 85, 125, 85, 85, 95});
        layout.setFlexColumn(2, true);
        cellPanel.setLayout(layout);
    	cellPanel.add(dateChooser);
    	cellPanel.add(numList);
    	cellPanel.add(payeeList);
    	cellPanel.add(withdrawField);
    	cellPanel.add(depositField);
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(createSplitPanel());
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(createEmptyPanel());
//    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(balanceLabel);
    }
    
    private JPanel createSplitPanel()
    {
    	final JPanel panel = new JPanel(new GridLayout(1, 2));
    	panel.add(categoryList);
    	panel.add(memoField);
    	return panel;
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
        	dateChooser.setDate(null);
        	numList.setSelectedItem("");
        	payeeList.setSelectedItem("");
        	withdrawField.setText("");
        	depositField.setText("");
        	balanceLabel.setText("");
        	categoryList.setSelectedItem("");
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
            	categoryList.setSelectedItem("");
        	}
        	else if(transfers.length == 1)
        	{
        	    categoryList.setSelectedItem(transfers[0].getCategory());
        	}
        	else
        	{
        	    //categoryList.setEnabled(false);
        	    categoryList.setSelectedItem("[SPLIT]");
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
