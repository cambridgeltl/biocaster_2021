/*
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.gui;

/**
 *
 * @author John McCrae, National Institute of Informatics
 */
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Color;

public class MultiLineCellRenderer extends JTextArea implements
        TableCellRenderer {

    protected static Border noFocusBorder;
    private Color unselectedForeground;
    private Color unselectedBackground;

    public MultiLineCellRenderer() {
        super();
        noFocusBorder = new EmptyBorder(1, 2, 1, 2);
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setBorder(noFocusBorder);
    }

    public void setForeground(Color c) {
        super.setForeground(c);
        unselectedForeground = c;
    }

    public void setBackground(Color c) {
        super.setBackground(c);
        unselectedBackground = c;
    }

    public void updateUI() {
        super.updateUI();
        setForeground(null);
        setBackground(null);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {



        if (isSelected) {
            // YAY!!! For Hacks!!!
            Color c = new Color(table.getSelectionForeground().getRGB()+1);
            super.setForeground(c);
            c = new Color(table.getSelectionBackground().getRGB()+1);
            super.setBackground(c);
        } else {
            super.setForeground((unselectedForeground != null) ? unselectedForeground
                    : table.getForeground());
            super.setBackground((unselectedBackground != null) ? unselectedBackground
                    : table.getBackground());
        }

        setFont(table.getFont());

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                super.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        } else {
            setBorder(noFocusBorder);
        }

        setValue(value);
        /*int rowHeight = (int) getPreferredSize().getHeight();
        if (table.getRowHeight() != rowHeight) {
            table.setRowHeight(rowHeight);
        }*/

        return this;
    }

    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }


  public static class UIResource extends MultiLineCellRenderer implements javax.swing.plaf.UIResource {
  }
}
