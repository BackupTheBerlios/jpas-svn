package org.jpas.gui.components;

/*
 * Created on Jun 26, 2005
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.apache.log4j.Logger;
import org.jpas.gui.util.CheckpointDocumentListener;
import org.jpas.gui.util.Checkpointable;

public class CheckpointComboBox extends JComboBox implements Checkpointable
{
    private static Logger defaultLogger = Logger.getLogger(CheckpointComboBox.class);
    
    private boolean valueChanged = false;
    protected static final Color highlightColor = new Color(255, 223, 223);
    private JTextField myEditor;
    
    public CheckpointComboBox(final ComboBoxModel aModel)
    {
        super(aModel);
        init();
    }

    public CheckpointComboBox(final Object[] items)
    {
        super(items);
        init();
    }

    public CheckpointComboBox(final Vector<?> items)
    {
        super(items);
        init();
    }

    public CheckpointComboBox()
    {
        super();
        init();
    }

    private void init()
    {
        setEditor(new CheckpointComboBoxEditor());
        addActionListener(new ActionListener()
        {
            public void actionPerformed(final ActionEvent e)
            {
                setChanged(true);
            }
        });
    }
    
    class CheckpointComboBoxEditor extends BasicComboBoxEditor
    {
        private CheckpointComboBoxEditor()
        {
            super();
            myEditor = editor;
            editor.getDocument().addDocumentListener(new CheckpointDocumentListener(CheckpointComboBox.this));
        }
    }
    
    protected Color getDisabledColor()
    {
        return Color.white;
    }
    
    protected Color getStandardColor()
    {
        return Color.white;
    }
    
    protected Color getHighlightColor()
    {
        return highlightColor;
    }

    public void setChanged(boolean changed)
    {
        defaultLogger.debug("Changed: " + changed);
        this.valueChanged = changed;
        if(valueChanged)
        {
            setBackground(getHighlightColor());
            myEditor.setBackground(getHighlightColor());
        }
        else if(isEditable())
        {
            setBackground(getStandardColor());
            myEditor.setBackground(getStandardColor());
        }
        else
        {
            setBackground(getDisabledColor());
            myEditor.setBackground(getDisabledColor());
        }
    }

    public boolean hasChanged()
    {
        return valueChanged;
    }
}
