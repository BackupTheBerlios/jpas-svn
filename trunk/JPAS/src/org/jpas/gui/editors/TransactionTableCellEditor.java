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

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import org.jpas.gui.components.CategoryComboBox;
import org.jpas.gui.components.PayeeComboBox;
import org.jpas.gui.layouts.FlexGridLayout;
import org.jpas.model.Account;

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
	private final JComboBox payeeList;
	private final JTextField withdrawField;
	private final JTextField depositField;
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

    	dateChooser = new JDateChooser();
    	numList = new JComboBox(new String[]{"TXFR", "ATM", "100"});
    	payeeList = new PayeeComboBox(account);
    	withdrawField = new JTextField("0000.00");
    	depositField = new JTextField("0000.00");
    	balanceLabel = new JLabel("0000.00");
    	categoryList = new CategoryComboBox();

        
        init();
    }
    
    private void init()
    {
        final FlexGridLayout layout = new FlexGridLayout(new int[]{18, 18}, new int[]{85, 85, 105, 85, 85, 95});
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
    	cellPanel.add(categoryList);
    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(createEmptyPanel());
//    	cellPanel.add(createEmptyPanel());
    	cellPanel.add(balanceLabel);
}
    
    private JPanel createEmptyPanel()
    {
    	final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
    	return panel;
    }
    
    public Component getTableCellEditorComponent(JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column)
    {
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
