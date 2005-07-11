package org.jpas.gui.components;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.sun.java.swing.SwingUtilities2;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

public class JpasDateChooser extends JDateChooser
{
    boolean shouldOpen = true;
    
    public JpasDateChooser(String dateFormatString, boolean startEmpty)
    {
        //(JCalendar jcalendar, String dateFormatString, boolean startEmpty, ImageIcon icon)
        super(new JCalendar(), dateFormatString, startEmpty, new ImageIcon(JDateChooser.class.getResource("images/JDateChooserIcon.gif")));
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        System.out.println("actionPerformed");
        int x = calendarButton.getWidth() - (int) popup.getPreferredSize().getWidth();
        int y = calendarButton.getY() + calendarButton.getHeight();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(model.getDate());
        jcalendar.setCalendar(calendar);
        if(shouldOpen)
        {
            popup.show(calendarButton, x, y);
            shouldOpen = false;
        }
        else
        {
            dateSelected = true;
            popup.setVisible(false);
            shouldOpen = true;
        }
        
        dateSelected = false;
    }
    public void propertyChange(PropertyChangeEvent evt) 
    {
        if (evt.getPropertyName().equals("day")) 
        {
            dateSelected = true;
            popup.setVisible(false);
            shouldOpen = true;
            setDate(jcalendar.getCalendar().getTime());
            setDateFormatString(dateFormatString);
        }
        else if (evt.getPropertyName().equals("date")) 
        {
            setDate((Date) evt.getNewValue());
        }
    }
}
