package com.common;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

    private JTable table;

    public RowHeaderRenderer(JTable table) {
        this.table = table;
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
    }

    @Override
    public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText((value == null) ? "" : value.toString());
        setPreferredSize(null); // Reset preferred size to recalculate it based on the new text
        setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), table.getRowHeight(index)));
        return this;
    }
}
