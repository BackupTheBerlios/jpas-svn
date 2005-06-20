/**
 * Created on Nov 8, 2004 - 8:07:07 PM
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
package org.jpas.gui.documents;

import java.text.*;
import javax.swing.text.*;
import org.apache.log4j.*;

/**
 * @author Justin W Smith
 *
 */
public class AmountDocument extends PlainDocument
{
    private static final Logger defaultLogger = Logger.getLogger(AmountDocument.class);
    
    /**
     * 
     */
    public AmountDocument()
    {
    }

    private boolean validate(final CharSequence text)
    {
        boolean pastDecimal = false;
        int digitsPastDecimal = 0;
        for(int i = 0; i < text.length(); i++)
        {
            final char ch = text.charAt(i);
            if(ch == '.')
            {
                if(pastDecimal)
                {
                    return false;
                }
                pastDecimal = true;
            }
            else if(ch == ',')
            {
                if(pastDecimal)
                {
                    return false;
                }
            }
            else if(Character.isDigit(ch))
            {
                if(pastDecimal)
                {
                    if(digitsPastDecimal > 1)
                    {
                        return false;
                    }
                    digitsPastDecimal++;
                }
            }
            else
            {
                return false;
            }
            
        }
        return true;
    }
    
    private boolean canInsertChar(final int offset, final char ch) throws BadLocationException
    {
        final String text = getText(0, getLength());
        final int textLength = text.length(); 
        final StringBuffer buff = new StringBuffer(text.substring(0, offset))
        						.append(ch)
        						.append(text.substring(offset, textLength));
        
        return validate(buff);    
    }

    private boolean canRemoveChar(final int offset) throws BadLocationException
    {
        final String text = getText(0, getLength());
        
        return validate(text.substring(0, Math.max(0, offset-1)).concat(text.substring(offset + 1, text.length())) );
    }

    
    public void insertString(final int offset, final String str, final AttributeSet a) throws BadLocationException
    {
        for(int i = 0; i < str.length(); i++)
        {
            final char ch = str.charAt(i);
            if(canInsertChar(offset + i, ch))
            {
                super.insertString(offset+i, String.valueOf(ch), a);
            }
            else
            {
                break;
            }
        }
    }

    public void remove(final int offset, final int length) throws BadLocationException
    {
        for(int i = 0; i < length; i++)
        {
            if(canRemoveChar(offset))
            {
                super.remove(offset, 1);
            }
            else
            {
                break;
            }
        }
    }

    public void fixText()
    {
        
    }
    
    public void setAmount(final long amount)
    {
        try
        {
            super.remove(0, getLength());
            super.insertString(0, getTextForAmount(amount), null);
        }
        catch(final BadLocationException ble)
        {
            defaultLogger.error("BadLocationException:", ble);
        }
    }
    
    public long getAmount()
    {
        try
        {
        	final String text = getText(0, getLength());
        	long value = 0;
        	boolean pastDecimal = false;
        	int digitsPastDecimal = 0;
        	for(int i = 0; i < text.length(); i++)
        	{
        		if(pastDecimal)
        		{
        			digitsPastDecimal++;
        		}
       		
        	    final char ch = text.charAt(i);
        	    if(Character.isDigit(ch))
        	    {
        	        value = 10*value + (ch - '0');
        	    }
        	    else if(ch == '.')
        	    {
        	    	pastDecimal = true;
        	    }
        	}
        	
        	for(int i = 2; i > digitsPastDecimal; i--)
        	{
        		value *= 10;
        	}
        	return value;
	    }
	    catch(final BadLocationException ble)
	    {
	        defaultLogger.error("BadLocationException:", ble);
	    }
	    return 0L;
    }
    
    public static String getTextForAmount(final long amount)
    {
        return getTextForAmount(amount, false);
    }
    
    public static String getTextForAmount(final long amount, final boolean formatForLabel)
    {
        final StringBuffer buff = new StringBuffer();
        final DecimalFormat dollarFormat = new DecimalFormat("###,###,###,###");
        final DecimalFormat twoFormat = new DecimalFormat("00");
        
        final long absAmount = Math.abs(amount);
        buff.append(dollarFormat.format(absAmount / 100));
        buff.append(".");
        buff.append(twoFormat.format(absAmount % 100));

        if(amount < 0)
        {
            if(formatForLabel)
            {
                buff.insert(0, " (");
                buff.append(")");
            }
            else
            {
                buff.insert(0, "-");
            }
        }
        else
        {
            if(formatForLabel)
            {
                buff.insert(0, " ");
            }
        }

        return buff.toString();
    }
    
    public static void main(String[] args)
    {
    }
}
