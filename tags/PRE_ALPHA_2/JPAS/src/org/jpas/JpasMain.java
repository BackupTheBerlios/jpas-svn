/**
 * Created on Oct 2, 2004 - 11:22:55 PM
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
package org.jpas;

import java.awt.event.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.jpas.gui.*;
import javax.swing.*;

/**
 * @author Owner
 *
 */
public class JpasMain
{
	/**
     * Installs the Kunststoff and Plastic Look And Feels if available in classpath.
     */
    private static void initializeLookAndFeels() {
    	// if in classpath thry to load JGoodies Plastic Look & Feel
        try {
            UIManager.installLookAndFeel("JGoodies Plastic 3D",
                "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel");
            UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.Plastic3DLookAndFeel");
        } catch (Throwable t) {
        	try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}  catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
	
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        initializeLookAndFeels();
        
    	final JFrame frame = new JpasFrame(); 
        
        frame.addWindowListener(new WindowAdapter()
        {
    		public void windowClosing(final WindowEvent we)
    		{
    		    System.exit(0);
    		}
        });
        
        frame.setVisible(true);
    }
}
