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
public class TransactionTableCellRenderer extends JPanel implements TableCellRenderer
{
	private static final Color borderColor = Color.gray;
	
    final JLabel dateLabel = new JLabel();
	final JLabel numLabel = new JLabel();
    final JLabel payeeLabel = new JLabel();
    final JLabel withdrawLabel = new JLabel();
    final JLabel depositLabel = new JLabel();
    final JLabel balanceLabel = new JLabel();
    final JLabel categoryLabel = new JLabel();
    final JLabel memoLabel = new JLabel();
    
    /**
     * 
     */
    public TransactionTableCellRenderer()
    {
        setOpaque(true);
        setBackground(Color.white);
        init();
    }
    
    private void init()
    {
        final FlexGridLayout layout = new FlexGridLayout(new int[]{18, 18}, new int[]{85, 85, 105, 85, 85, 95});
        layout.setFlexColumn(2, true);
        setLayout(layout);
    	add(dateLabel);
    	add(numLabel);
    	add(payeeLabel);
    	add(withdrawLabel);
    	add(depositLabel);
    	add(createEmptyPanel());
    	add(createEmptyPanel());
    	add(createEmptyPanel());
    	add(createSplitPanel(categoryLabel, memoLabel));
    	add(createEmptyPanel());
    	add(createEmptyPanel());
//    	add(createEmptyPanel());
    	add(balanceLabel);
    	
    	
        dateLabel.setBorder(BorderFactory.createLineBorder(borderColor));
    	numLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        payeeLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        withdrawLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        depositLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        balanceLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        categoryLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        memoLabel.setBorder(BorderFactory.createLineBorder(borderColor));

}
    
    private JPanel createEmptyPanel()
    {
    	final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
    	return panel;
    }
    
    private JPanel createSplitPanel(final JComponent comp1, final JComponent comp2)
    {
    	final JPanel panel = new JPanel(new GridLayout(1, 2));
    	panel.add(comp1);
    	panel.add(comp2);
    	
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
            dateLabel.setText("");
        	numLabel.setText("");
            payeeLabel.setText("");
            withdrawLabel.setText("");
            depositLabel.setText("");
            balanceLabel.setText("");
            categoryLabel.setText("");
            memoLabel.setText("");
        }
        else
        {
        	final Transaction trans = (Transaction)value;
            dateLabel.setText(trans.getDate().toLocaleString());
        	numLabel.setText(trans.getNum());
            payeeLabel.setText(trans.getPayee());
            final long amount = trans.getAmount();
            if(amount >= 0)
            {
	            withdrawLabel.setText(String.valueOf(amount));
	            depositLabel.setText("");
            }
            else
            {
	            withdrawLabel.setText("");
	            depositLabel.setText(String.valueOf(amount));
            }
            balanceLabel.setText("");
            final TransactionTransfer[] transfers = trans.getTransfers();
            if(transfers.length == 0)
            {
            	categoryLabel.setText("[NONE]");
            }
            else if(transfers.length == 1)
            {
            	categoryLabel.setText(transfers[0].getCategory().getName());
            }
            else
            {
            	categoryLabel.setText("[SPLIT]");
            }
            memoLabel.setText(trans.getMemo());
        }
        return this;
    }
    
    public static void main(String[] args)
    {
    }
}
