/**
 * Created on Oct 4, 2004 - 7:38:45 PM
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
package org.jpas.gui.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * @author Justin W Smith
 *
 */
public class ActionFactory
{
    private static ActionFactory instance = new ActionFactory();
    
    public static ActionFactory getInstance()
    {
        return instance;
    }
    
    /**
     * 
     */
    public ActionFactory()
    {
    }

    public Action createExitAction()
    {
        return new AbstractAction("Exit")
        {
            /**
             * Comment for <code>serialVersionUID</code>
             */
            private static final long serialVersionUID = -2201297787213818018L;

            public void actionPerformed(final ActionEvent ae)
            {
                System.exit(0);
            }
        };
    }
    
    public JMenuBar createJMenuBar()
    {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));
        
        menuBar.add(createFileMenu());
        
        return menuBar;
    }
    
    public JMenu createFileMenu()
    {
        final JMenu menu = new JMenu("File");
        menu.add(createExitAction());
        return menu;
    }
    
    public static void main(String[] args)
    {
    }
}
