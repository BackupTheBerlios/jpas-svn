/**
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;
import org.jpas.gui.components.*;
import org.jpas.gui.data.TransactionData;
import org.jpas.gui.documents.AmountDocument;
import org.jpas.gui.layouts.FlexGridLayout;
import org.jpas.model.*;




/**
 * @author jsmith
 *
 * An object of this class can only be used on one table at a time.
 */
public class TransactionTableCellEditor extends AbstractCellEditor implements TableCellEditor 
{
    private static final Logger defaultLogger = Logger.getLogger(TransactionTableCellEditor.class);
    
	private final JPanel cellPanel = new JPanel();
    
	private final JpasDateChooser dateChooser;
	private final CheckpointComboBox numList;
	private final PayeeComboBox payeeList;
	private final CheckpointTextField withdrawField;
	private final AmountDocument withdrawDoc = new AmountDocument(); 
	private final CheckpointTextField depositField;
	private final AmountDocument depositDoc = new AmountDocument();
	private final CheckpointTextField memoField;
	private final AmountLabel balanceLabel;
	private final CategoryComboBox categoryList;
	private final JLabel categoryLabel = new JLabel("[SPLIT]");
	private final JButton btnEnter = new JButton("Enter");
	private final JButton btnSplit = new JButton("Split");
    private final JComboBox editMenu = new JComboBox(new EditComboBoxModel());
	
	private final JPanel splitPanel = new JPanel(new GridLayout(1, 2));
	
	private Category[] splitCategories;
	
	private final int[] columnWidths;
	private final int[] rowHeights;
	
	private Account account;

    private static final String actionDelete = "Delete";

    private Transaction currentTrans;
    
    private static class EditComboBoxModel extends AbstractListModel implements ComboBoxModel
    {
        String selectedAction = null;
        final String[] commands = new String[]{actionDelete};
        
        public void setSelectedItem(Object anItem)
        {
            selectedAction = (String)anItem;
        }
        public String getSelectedAction()
        {
            return selectedAction;
        }
        
        public Object getSelectedItem()
        {
            return "           Edit";
        }
        public int getSize()
        {
            return commands.length;
        }
        public Object getElementAt(int index)
        {
            return commands[index];
        }
    }
    
    /**
     * 
     */
    public TransactionTableCellEditor(final int[] columnWidths, final int[] rowHeights)
    {
        this.columnWidths = columnWidths;
        this.rowHeights = rowHeights;

        
        cellPanel.setOpaque(false);
    	dateChooser = new JpasDateChooser("MM/dd/yyyy", false);
		// TODO create a "real" combo box for the "num" field
    	numList = new CheckpointComboBox(new String[]{"TXFR", "ATM", "100"});
    	numList.setEditable(true);
    	payeeList = new PayeeComboBox();
    	payeeList.setEditable(true);
    	withdrawField = new CheckpointTextField();
    	depositField = new CheckpointTextField();
    	memoField = new CheckpointTextField();
        
    	balanceLabel = new AmountLabel();
    	categoryList = new CategoryComboBox();
    	
        init();
    }
	
	public void initListeners()
	{
		btnEnter.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent ae)
			{
                final Date newDate = dateChooser.getDate();
                if(currentTrans != null)
                {
                    final Date oldDate = currentTrans.getDate();
                    final int dayChangeMax = Preferences.userRoot().getInt("trans/max_date_change", 7);
                    if(Math.abs(newDate.getTime() - oldDate.getTime()) > (1000 * 60 * 60 * 24 * dayChangeMax))
                    {
                        int reply = JOptionPane.showConfirmDialog(cellPanel, "The date of this transaction has been altered by more than a week.  Continue with save?", "Date change exceeds threshold", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(reply == JOptionPane.NO_OPTION)
                        {
                            return;
                        }
                    }
                }

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run() 
					{
                        TransactionTableCellEditor.super.stopCellEditing();
					}
				});
			}
		});
        editMenu.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final EditComboBoxModel model = (EditComboBoxModel)editMenu.getModel();
                final String action = model.getSelectedAction();
                if(action.equals(actionDelete))
                {
                    deleteTransaction();
                }
            }
        });
        addMouseListener(memoField);
        addMouseListener(withdrawField);
        addMouseListener(depositField);
        addMouseListener((JTextField)numList.getEditor().getEditorComponent());
        addMouseListener((JTextField)payeeList.getEditor().getEditorComponent());
        
        addFocusListener(numList);
        addFocusListener(payeeList);
        addFocusListener(withdrawField);
        addFocusListener(depositField);
        addFocusListener(memoField);
        addFocusListener(categoryList);
        addFocusListener(btnEnter);
        addFocusListener(btnSplit);
        addFocusListener(editMenu);

	}
    
    private void addFocusListener(final JComponent comp)
    {
        comp.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                dateChooser.closePopup();
            }
        });
    }
    
    private void addFocusListener(final JComboBox combo)
    {
        if(combo.isEditable())
        {
            combo.getEditor().getEditorComponent().addFocusListener(new FocusAdapter()
            {
                @Override
                public void focusGained(FocusEvent e)
                {
                    dateChooser.closePopup();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            combo.setPopupVisible(true);
                        }
                    });
                }
            });
        }
        else
        {
            combo.addFocusListener(new FocusAdapter()
            {
                @Override
                public void focusGained(FocusEvent e)
                {
                    dateChooser.closePopup();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            combo.setPopupVisible(true);
                        }
                    });
                }
            });
        }
    }
    
    private void deleteTransaction()
    {
        final int option = JOptionPane.showConfirmDialog(cellPanel, "This item will be permanently deleted. Continue?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION)
        {
            if(currentTrans.getAccount().equals(account))
            {
                currentTrans.delete();
                currentTrans.commit();
            }
            else
            {
                final ModelFactory modelFactory = ModelFactory.getInstance();
                final TransactionTransfer transfer = modelFactory.getTransfer(currentTrans, modelFactory.getCategoryForAccount(account));
                assert(transfer != null);
                transfer.delete();
                transfer.commit();
            }
        }
    }
   
    private void addMouseListener(final JTextField tf)
    {
        tf.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(tf.isEditable() && !tf.hasFocus())
                {
                    tf.requestFocus();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            tf.setSelectionStart(0);
                            tf.setSelectionEnd(tf.getText().length());
                        }
                    });
                }
            }
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if(tf.isEditable() && !tf.hasFocus())
                {
                    tf.requestFocus();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            tf.setSelectionStart(0);
                            tf.setSelectionEnd(tf.getText().length());
                        }
                    });
                }
            }
        });
    }
    
    @Override
    public boolean stopCellEditing()
    {
        if(valueHasChanged())
        {
            final int response = JOptionPane.showConfirmDialog(cellPanel, "This transaction has not been saved.  Any changes will be lost if continued.  Continue?", "Unsaved changes", JOptionPane.YES_NO_OPTION);
    
            return response == JOptionPane.YES_OPTION;
        }
        
        return true;
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
    	cellPanel.add(editMenu);
    	
    	withdrawField.setDocument(withdrawDoc);
    	depositField.setDocument(depositDoc);
    
    	createClearDocListener(withdrawField, depositField);
    	createClearDocListener(depositField, withdrawField);
		initListeners();
    }
    
    private void createClearDocListener(final CheckpointTextField tf, final CheckpointTextField opposite)
    {
        tf.getDocument().addDocumentListener(new DocumentListener()
        {
    		public void insertUpdate(DocumentEvent e)
    		{
                opposite.setText("");
                opposite.setChanged(false);
    		}
    		public void removeUpdate(DocumentEvent e)
    		{
    		}
    		public void changedUpdate(DocumentEvent e)
    		{
    		}
        });        
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
    	splitCategories = null;
        splitPanel.removeAll();
        splitPanel.add(categoryList);
        splitPanel.add(memoField);
        splitPanel.invalidate();
        withdrawField.setEditable(true);
        depositField.setEditable(true);
        withdrawField.setToolTipText(null);
        depositField.setToolTipText(null);
    }
    
    private void setSplitPanel(final Category[] categories)
    {
    	splitCategories = categories;
        splitPanel.removeAll();
        splitPanel.add(categoryLabel);
        splitPanel.add(memoField);
        splitPanel.invalidate();
        withdrawField.setEditable(false);
        depositField.setEditable(false);
        withdrawField.setToolTipText("Select \"Split\" to modify amount.");
        depositField.setToolTipText("Select \"Split\" to modify amount.");
    }
    
    private JPanel createEmptyPanel()
    {
    	final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
    	return panel;
    }
    
    private void setPanelEnabled(final boolean enable)
    {
    	dateChooser.setEnabled(enable);
    	numList.setEnabled(enable);
    	payeeList.setEnabled(enable);
    	withdrawField.setEnabled(enable);
    	depositField.setEnabled(enable);
    	memoField.setEnabled(enable);
    	balanceLabel.setEnabled(enable);
    	categoryList.setEnabled(enable);
    	categoryLabel.setEnabled(enable);
    	btnEnter.setEnabled(enable);
    	btnSplit.setEnabled(enable);
        editMenu.setEnabled(enable);
    }
    
    
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
    {
        currentTrans = (Transaction)value;
        setPanelEnabled(false);
        
        if(currentTrans == null)
        {
        	dateChooser.setDate(new java.util.Date());
        	numList.setSelectedItem("");
        	payeeList.setSelectedItem("");
        	withdrawField.setText("");
        	depositField.setText("");
            balanceLabel.setText("");
        	memoField.setText("");
        	categoryList.getModel().setSelectedItem(null);
    	    setCategoryPanel();
        }
        else
        {
	        dateChooser.setDate(currentTrans.getDate());
        	numList.setSelectedItem(currentTrans.getNum());
        	payeeList.setSelectedItem(currentTrans.getPayee());
        	
        	//TODO set balance value appropriately
        	balanceLabel.setAmount(currentTrans.getBalance());
        	memoField.setText(currentTrans.getMemo());
        	
        	if(currentTrans.getAccount().equals(account))
        	{
	        	final long amount = currentTrans.getAmount();
	        	if(amount >= 0)
	        	{
		        	depositField.setText("");
		        	withdrawDoc.setAmount(currentTrans.getAmount());
	        	}
	        	else
	        	{
		        	withdrawField.setText("");
		        	depositDoc.setAmount(-currentTrans.getAmount());
	        	}
	        	
	        	final TransactionTransfer[] transfers = ModelFactory.getInstance().getTransfersForTransaction(currentTrans);
	        	if(transfers.length == 0)
	        	{
	        	    setCategoryPanel();
	        	    // TODO Deal with no category on save.
	            	categoryList.setSelectedItem(null);
	        	}
	        	else if(transfers.length == 1)
	        	{
	        	    setCategoryPanel();
	        	    categoryList.setSelectedItem(transfers[0].getCategory());
	        	}
	        	else
	        	{
	        		final List<Category> list = new LinkedList<Category>();
	        		for(int i = 0; i < transfers.length; i++)
	        		{
	        			list.add(transfers[i].getCategory());
	        		}
	        		
	        	    setSplitPanel(list.toArray(new Category[list.size()]));
	        	    //categoryList.setSelectedItem("[SPLIT]");
	        	    //categoryList.setEnabled(false);
	        	}
        	}
        	else
        	{
        	    final Category cat = ModelFactory.getInstance().getCategoryForAccount(account);
	            final long amount = ModelFactory.getInstance().getTransfer(currentTrans, cat).getAmount();

	            if(amount <= 0)
	            {
		            withdrawField .setText(AmountDocument.getTextForAmount(-amount));
		            depositField.setText("");
	            }
	            else
	            {
	            	withdrawField.setText("");
		            depositField.setText(AmountDocument.getTextForAmount(amount));
	            }

        	    setCategoryPanel();
        	    categoryList.getModel().setSelectedItem(ModelFactory.getInstance().getCategoryForAccount(currentTrans.getAccount()));
        	}
        }
        resetCheckpoints();
        
        SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setPanelEnabled(true);
			}
		});
        
        return cellPanel;
    }
    
    private boolean valueHasChanged()
    {
        return withdrawField.hasChanged()
            || depositField.hasChanged()
            || memoField.hasChanged()
            || categoryList.hasChanged()
            || payeeList.hasChanged()
            || numList.hasChanged();
    }
    
    private void resetCheckpoints()
    {
        withdrawField.setChanged(false);
        depositField.setChanged(false);
        memoField.setChanged(false);
        categoryList.setChanged(false);
        payeeList.setChanged(false);
        numList.setChanged(false);
    }
    
    public Object getCellEditorValue()
    {
        return new TransactionData(
        		dateChooser.getDate(),
        		(String)numList.getEditor().getItem(),
                (String)payeeList.getEditor().getItem(),
                withdrawDoc.getAmount(),
                depositDoc.getAmount(),
                splitCategories == null ?
                new Category[]{(Category)categoryList.getModel().getSelectedItem()}
        		: splitCategories,
                memoField.getText());
        
    }
    
    public static void main(String[] args)
    {
    }
}
