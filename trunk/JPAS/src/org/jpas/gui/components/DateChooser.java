/**
 * Created on Oct 26, 2004 - 8:32:43 PM
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
package org.jpas.gui.components;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

import org.jpas.gui.layouts.FlexGridLayout;

import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.awt.event.*;
/**
 * @author Justin W Smith
 *
 */
public class DateChooser extends JPanel
{
    private JTextField textField = new JTextField();
    
    /**
     * 
     */
    public DateChooser()
    {
        setLayout(new FlexGridLayout(new int[]{18}, new int[]{85, 20}));
        final JButton calendarButton = new JButton(createCalendarAction());   
        
        add(new JTextField());
        add(calendarButton);
        calendarButton.setText("");
    }

    private Action createCalendarAction()
    {
        final URL iconURL = JDateChooser.class.getResource("images/JDateChooserIcon.gif");

        return new AbstractAction("Calendar popup", new ImageIcon(iconURL))
        {
            public void actionPerformed(final ActionEvent ae)
            {
                
            }
        };
    }
    
    public void setDate(final Date date)
    {
        if(date == null)
        {
            textField.setText(new Date().toString());
        }
        else
        {
            textField.setText(date.toString());
        }
    }
    
    public static void main(String[] args)
    {
    }
}
