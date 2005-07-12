package org.jpas.gui.components;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

public class JpasDateChooser extends JDateChooser
{
    boolean isOpen = false;
    //boolean afterActionPerformed = false;
    
    public JpasDateChooser(String dateFormatString, boolean startEmpty)
    {
        super(new JCalendar(), dateFormatString, startEmpty, new ImageIcon(JDateChooser.class.getResource("images/JDateChooserIcon.gif")));
        calendarButton.addMouseListener(new MouseAdapter()
        {
            /* (non-Javadoc)
             * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
             */
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(isOpen)
                {
//                    showPopup();
                }
            }
            
        });
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        if(isOpen)
        {
            closePopup();
        }
        else
        {
            showPopup();
        }
    }

    private void showPopup()
    {
        int x = calendarButton.getWidth() - (int) popup.getPreferredSize().getWidth();
        int y = calendarButton.getY() + calendarButton.getHeight();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(model.getDate());
        jcalendar.setCalendar(calendar);
        
        popup.show(calendarButton, x, y);
        dateSelected = false;
        isOpen = true;
    }
    
    public void closePopup()
    {
        dateSelected = true;
        popup.setVisible(false);
        isOpen = false;
    }
    
    public String getText()
    {
        return editor.getTextField().getText();
    }
    
    /**
     * Listens for a "date" property change or a "day" property change event from the JCalendar.
     * Updates the dateSpinner and closes the popup.
     *
     * @param evt the event
     */
    public void propertyChange(PropertyChangeEvent evt) 
    {
        if (evt.getPropertyName().equals("day")) 
        {
            System.out.println("editor has focus: " + this.editor.hasFocus());
            if(popup.isVisible())
            {
                isOpen = false;
            }
            dateSelected = true;
            popup.setVisible(false);
            setDate(jcalendar.getCalendar().getTime());
            setDateFormatString(dateFormatString);
        } 
        else if (evt.getPropertyName().equals("date")) 
        {
            setDate((Date) evt.getNewValue());
        }
    }
}
