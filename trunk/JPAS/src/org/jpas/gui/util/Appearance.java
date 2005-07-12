/**
 * Created on Jun 28, 2005
 *
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2005 Justin W Smith
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

import java.awt.Color;
import java.util.prefs.*;

public abstract class Appearance
{
    private static final Appearance instance = new Appearance()
    {
        private final Color transactionRowColor1 = new Color(Preferences.userRoot().getInt("/gui/color/transaction/row1", new Color(231, 255, 231).getRGB()));
        private final Color transactionRowColor2 = new Color(Preferences.userRoot().getInt("/gui/color/transaction/row2", new Color(229, 227, 208).getRGB()));
        private final Color modifyIndicationColor = new Color(Preferences.userRoot().getInt("/gui/color/indicator/modify", new Color(255, 223, 223).getRGB()));
        public Color getTransactionRowColor1()
        {
            return transactionRowColor1;
        }

        public Color getTransactionRowColor2()
        {
            return transactionRowColor2;
        }

        public Color getModifyIndicationColor()
        {
            return modifyIndicationColor;
        }
        
        public Color getTextFieldEditableBackground()
        {
            return Color.white;
        }
    };
    
    public static Appearance getInstance()
    {
        return instance;
    }
    
    
    public abstract Color getTransactionRowColor1();
    public abstract Color getTransactionRowColor2();
    public abstract Color getModifyIndicationColor();
    public abstract Color getTextFieldEditableBackground();
}
