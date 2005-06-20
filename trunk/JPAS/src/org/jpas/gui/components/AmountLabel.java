package org.jpas.gui.components;

import java.awt.*;
import javax.swing.Icon;
import javax.swing.JLabel;

import org.jpas.gui.documents.AmountDocument;;

public class AmountLabel extends JLabel
{

    /**
     * 
     */
    public AmountLabel()
    {
        super();
    }

    /**
     * @param image
     * @param horizontalAlignment
     */
    public AmountLabel(Icon image, int horizontalAlignment)
    {
        super(image, horizontalAlignment);
    }

    /**
     * @param image
     */
    public AmountLabel(Icon image)
    {
        super(image);
    }

    /**
     * @param text
     * @param icon
     * @param horizontalAlignment
     */
    public AmountLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
    }

    /**
     * @param text
     * @param horizontalAlignment
     */
    public AmountLabel(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
    }

    /**
     * @param text
     */
    public AmountLabel(String text)
    {
        super(text);
    }
    
    public void setAmount(final long amount)
    {
        if(amount < 0)
        {
            setForeground(Color.red);
        }
        else
        {
            setForeground(Color.black);
        }
        setText(AmountDocument.getTextForAmount(amount, true));
    }
}
