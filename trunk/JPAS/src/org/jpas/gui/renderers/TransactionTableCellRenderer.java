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
import org.jpas.model.*;

import com.sun.org.apache.bcel.internal.generic.BALOAD;
import com.toedter.calendar.*;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTableCellRenderer extends JPanel implements TableCellRenderer
{
    final JDateChooser dateChooser = new JDateChooser();
	final JComboBox numList = new JComboBox(new String[]{"TXFR", "ATM", "100"});
    final JComboBox payeeList = new JComboBox(new String[]{"Payee1", "Payee2"});
    final JTextField withdrawField = new JTextField();
    final JTextField depositField = new JTextField();
    final JLabel balanceLabel = new JLabel();
    final JComboBox categoryList = new JComboBox(new String[]{"cat1", "cat2"});
	
	final JLabel label = new JLabel(" ");
    
    /**
     * 
     */
    public TransactionTableCellRenderer()
    {
        this.setOpaque(true);
        this.setBackground(Color.white);
        setLayout(new GridBagLayout());
        init();
    }
    
    private void init()
    {
    	final GridBagConstraints gbc = new GridBagConstraints();
    	gbc.fill = GridBagConstraints.BOTH;
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	
    	add(dateChooser, gbc);
    	gbc.gridx++;
    	
    	add(numList, gbc);
    	gbc.gridx++;
    	gbc.gridwidth = 2;
    	gbc.weightx = 1;
    	
    	add(payeeList, gbc);
    	gbc.gridx++;
    	gbc.gridwidth = 1;
    	gbc.weightx = 0;
    	
    	add(withdrawField, gbc);
    	gbc.gridx++;
    	
    	add(depositField, gbc);
    	gbc.gridx++;
    	
    	add(createEmptyPanel(), gbc);
    	gbc.gridx = 0;
    	gbc.gridy++;
    	
    	add(createEmptyPanel(), gbc);
    	gbc.gridx++;
    	
    	add(createEmptyPanel(), gbc);
    	gbc.gridx++;

    	add(categoryList, gbc);
    	gbc.gridx++;
    	gbc.weightx = 1;
    	
    	add(createEmptyPanel(), gbc);
    	gbc.gridx++;
    	gbc.weightx = 0;

    	add(createEmptyPanel(), gbc);
    	gbc.gridx++;

    	add(createEmptyPanel(), gbc);
    	gbc.gridx++;

    	add(balanceLabel, gbc);
    	gbc.gridx++;
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
        return this;
    }
    
    public static void main(String[] args)
    {
    }
}
