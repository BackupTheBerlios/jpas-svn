/**
 * Created on Jun 22, 2005
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
package org.jpas.gui.components;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.jpas.gui.util.Checkpointable;

public class CheckpointTextField extends JTextField implements Checkpointable
{
    private boolean valueAltered = false;
    protected static final Color highlightColor = new Color(255, 239, 239);
    
    private final DocumentListener docListener = new DocumentListener(){

        public void insertUpdate(DocumentEvent e)
        {
            System.out.println("changed");
            setCheckpoint();
        }
        public void removeUpdate(DocumentEvent e)
        {
            System.out.println("changed");
            setCheckpoint();
        }
        public void changedUpdate(DocumentEvent e)
        {
            setCheckpoint();
        }};
    
    public CheckpointTextField()
    {
        super();
        init();
    }

    public CheckpointTextField(String text)
    {
        super(text);
        init();
    }

    public CheckpointTextField(int columns)
    {
        super(columns);
        init();
    }

    public CheckpointTextField(String text, int columns)
    {
        super(text, columns);
        init();
    }

    public CheckpointTextField(Document doc, String text, int columns)
    {
        super(doc, text, columns);
        init();
    }

    private void init()
    {
        getDocument().removeDocumentListener(getDocumentListener());
        getDocument().addDocumentListener(getDocumentListener());
    }
    
    @Override
    public void setDocument(final Document doc)
    {
        final Document oldDoc = getDocument();
        if(oldDoc != null)
        {
            oldDoc.removeDocumentListener(getDocumentListener());
        }
        super.setDocument(doc);
        doc.addDocumentListener(getDocumentListener());
        resetCheckpoint();
    }

    protected DocumentListener getDocumentListener()
    {
        return docListener;
    }
    
    protected Color getStandardColor()
    {
        return Color.WHITE;
    }
    
    protected Color getHighlightColor()
    {
        return highlightColor;
    }
    
    public void setCheckpoint()
    {
        valueAltered = true;
        setBackground(getHighlightColor());
    }
    
    public void resetCheckpoint()
    {
        valueAltered = false;
        setBackground(getStandardColor());
    }

    public boolean hasChanged()
    {
        // TODO Auto-generated method stub
        return valueAltered;
    }
}
