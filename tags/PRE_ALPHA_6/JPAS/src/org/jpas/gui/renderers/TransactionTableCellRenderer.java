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

import java.awt.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import org.jpas.gui.components.AmountLabel;
import org.jpas.gui.layouts.FlexGridLayout;
import org.jpas.model.*;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTableCellRenderer extends JPanel implements TableCellRenderer
{
	private static final Color borderColor = Color.gray;
	
	private Account account;
	
	private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	private final JLabel dateLabel = new JLabel();
	private final JLabel numLabel = new JLabel();
	private final JLabel payeeLabel = new JLabel();
	private final AmountLabel withdrawLabel = new AmountLabel();
	private final AmountLabel depositLabel = new AmountLabel();
	private final AmountLabel balanceLabel = new AmountLabel();
	private final JLabel categoryLabel = new JLabel();
	private final JLabel memoLabel = new JLabel();
    
	private final int[] columnWidths;
	private final int[] rowHeights;
	
	private final Dimension dim;
    
    /**
     * 
     */
    public TransactionTableCellRenderer(final int[] colunmWidths, final int[] rowHeights)
    {
        setOpaque(false);
        this.columnWidths = colunmWidths;
        this.rowHeights = rowHeights;
        
        dim = new Dimension(total(columnWidths), total(rowHeights));
        
        init();
    }
    
    public void setAccount(final Account account)
    {
        this.account = account;
    }
    
    public Account getAccount()
    {
        return account;
    }
    
    private int total(int[] values)
    {
    	int total = 0;
    	for(int i = 0; i < values.length; i++)
    	{
    		total += values[i];
    	}
    	return total;
    }
    
    private void init()
    {
        final FlexGridLayout layout = new FlexGridLayout(new int[]{18, 18}, new int[]{105, 85, 125, 85, 85, 95});
        layout.setFlexColumn(2, true);
        setLayout(layout);
    	add(dateLabel);
    	add(numLabel);
    	add(payeeLabel);
    	add(withdrawLabel);
    	add(depositLabel);
    	add(balanceLabel);
    	add(createEmptyPanel());
    	add(createEmptyPanel());
    	add(createSplitPanel(categoryLabel, memoLabel));
    	add(createEmptyPanel());
    	add(createEmptyPanel());
    	add(createEmptyPanel());
    	
        dateLabel.setBorder(BorderFactory.createLineBorder(borderColor));
    	numLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        payeeLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        withdrawLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        depositLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        balanceLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        categoryLabel.setBorder(BorderFactory.createLineBorder(borderColor));
        memoLabel.setBorder(BorderFactory.createLineBorder(borderColor));

        withdrawLabel.setOpaque(true);
        depositLabel.setOpaque(true);
        balanceLabel.setOpaque(true);
        
        withdrawLabel.setBackground(Color.white);
        depositLabel.setBackground(Color.white);
        balanceLabel.setBackground(Color.white);
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

        	dateLabel.setText(format.format(trans.getDate()));
        	numLabel.setText(trans.getNum());
            balanceLabel.setAmount(trans.getBalance());
            memoLabel.setText(trans.getMemo());
            payeeLabel.setText(trans.getPayee());
            
        	if(trans.getAccount().equals(account))
        	{
	            final long amount = trans.getAmount();
	            if(amount >= 0)
	            {
		            withdrawLabel.setAmount(amount);
		            depositLabel.setText("");
	            }
	            else
	            {
		            withdrawLabel.setText("");
		            depositLabel.setAmount(-amount);
	            }
	            
	            final TransactionTransfer[] transfers = ModelFactory.getInstance().getTransfersForTransaction(trans);
	            if(transfers.length == 0)
	            {
	            	categoryLabel.setText("[NONE]");
	            }
	            else if(transfers.length == 1)
	            {
	            	categoryLabel.setText(transfers[0].getCategory().getCategoryName());
	            }
	            else
	            {
	            	categoryLabel.setText("[SPLIT]");
	            }
        	}
        	else
        	{
        	    final Category cat = ModelFactory.getInstance().getCategoryForAccount(account);
	            final long amount = ModelFactory.getInstance().getTransfer(trans, cat).getAmount();

	            if(amount <= 0)
	            {
		            withdrawLabel.setAmount(-amount);
		            depositLabel.setText("");
	            }
	            else
	            {
		            withdrawLabel.setText("");
		            depositLabel.setAmount(amount);
	            }
	            
            	categoryLabel.setText(ModelFactory.getInstance().getCategoryForAccount(trans.getAccount()).getCategoryName());
        	}
        }
        return this;
    }
    
    public Dimension getPreferredSize()
    {
    	return dim;
    }
    
    public Dimension getMinimumSize()
    {
    	return dim;
    }
    
    
    public static void main(String[] args)
    {
    }
}
