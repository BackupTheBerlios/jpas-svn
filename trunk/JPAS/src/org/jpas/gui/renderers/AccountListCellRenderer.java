/**
 * Created on Oct 4, 2004 - 6:45:23 PM
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
import java.awt.GridLayout;
import org.jpas.model.*;
import javax.swing.*;

/**
 * @author Justin W Smith
 *
 */
public class AccountListCellRenderer extends JPanel implements ListCellRenderer
{
    private final boolean includeAmount;
    
    private final StringBuffer buff = new StringBuffer();
    
    private final JButton accountName = new JButton();
    private final JEditorPane accountAmount = new JEditorPane("text/html", "");
    
    private final Color highlightColor = new Color(255, 255, 196);
    private final Color backgroundColor = new Color(239, 239, 239);
    
    /**
     * 
     */
    public AccountListCellRenderer(final boolean includeAmount)
    {
        this.includeAmount = includeAmount;
        setLayout(new GridLayout(1, 1));
        
        final JPanel inner = new JPanel(new GridLayout(1, 2));

        //accountName.setBorder(BorderFactory.createRaisedBevelBorder());
        inner.add(accountName);
/*        
        final JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(accountAmount, BorderLayout.EAST);
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));
*/      
        accountAmount.setOpaque(true);
        inner.add(accountAmount);
        
        add(inner);

    }
    
    
    public Component getListCellRendererComponent(final JList list, final Object value,
            final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        final Account a = (Account)value;
        accountName.setText(a.getName());
        accountAmount.setText(includeAmount ?  createAmountText(a.getBalance()) : "");
        if(isSelected)
        {
            accountAmount.setBackground(highlightColor);
        }
        else
        {
            accountAmount.setBackground(backgroundColor);
        }
        return this;
    }

    public String createAmountText(long value)
    {
        buff.setLength(0);
        buff.append("<html><body><p align=\"right\"><b>");
        if(value < 0)
        {
            buff.append('-');
            value = Math.abs(value);
        }
        buff.append(value / 100);
        buff.append('.');
        buff.append(value % 100);
        buff.append("</b></p></body></html>");
        return buff.toString();        
    }
    
    public static void main(String[] args)
    {
    }
}
