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
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.jpas.gui.util.*;

public class CheckpointTextField extends JTextField implements Checkpointable
{
    private boolean valueAltered = false;
    
    private final DocumentListener docListener = new CheckpointDocumentListener(this);
    
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
        setChanged(false);
    }

    protected DocumentListener getDocumentListener()
    {
        return docListener;
    }

    public void setChanged(final boolean changed)
    {
        valueAltered = changed;
        if(valueAltered)
        {
            setBackground(Appearance.getInstance().getModifyIndicationColor());
        }
        else if(isEditable())
        {
            setBackground(Appearance.getInstance().getTextFieldEditableBackground());
        }
        else
        {
            setBackground(Appearance.getInstance().getTransactionRowColor2());
        }
    }

    public boolean hasChanged()
    {
        return valueAltered;
    }
}
