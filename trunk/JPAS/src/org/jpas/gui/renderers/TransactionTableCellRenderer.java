/**
 * Created on Oct 11, 2004 - 6:53:53 PM
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
package org.jpas.gui.renderers;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import org.jpas.gui.layouts.FlexGridLayout;
import org.jpas.model.*;

import com.toedter.calendar.*;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTableCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
{
    final JPanel cellPanel = new JPanel();
    
    final JDateChooser dateChooser = new JDateChooser();
	final JComboBox numList = new JComboBox(new String[]{"TXFR", "ATM", "100"});
    final JComboBox payeeList = new JComboBox(new String[]{"Payee1", "Payee2"});
    final JTextField withdrawField = new JTextField("0000.00", 8);
    final JTextField depositField = new JTextField("0000.00", 8);
    final JLabel balanceLabel = new JLabel("0000.00");
    final JComboBox categoryList = new JComboBox(new String[]{"cat1", "cat2"});
	
	final JLabel label = new JLabel(" ");

    /**
     * 
     */
    public TransactionTableCellRenderer()
    {
        cellPanel.setOpaque(true);
        cellPanel.setBackground(Color.white);
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
    

    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column)
    {
        if(value == null)
        {
//            label.setText(" ");
        }
        else
        {
            //label.setText(((Transaction)value).getPayee());
        }
        return cellPanel;
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
