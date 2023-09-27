package com.common;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT2);
    private int targetRow;
    private int targetColumn;
    private Color targetColor;
    private boolean hasFocus;
    private boolean darkMode = Util1.DARK_MODE;

    public CustomTableCellRenderer(int targetRow, int targetColumn, Color targetColor, boolean hasFocus) {
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
        this.targetColor = targetColor;
        this.hasFocus = hasFocus;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            component.setBackground(Global.selectionColor);
        } else if (row == targetRow && column == targetColumn) {
            component.setForeground(Color.black);
            component.setBackground(targetColor);
        } else {
            component.setBackground(table.getBackground());
            component.setForeground(darkMode ? Color.white : Color.black);
        }
        if (value instanceof Float d) {
            String s = formatter.format(d);
            component = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) component).setHorizontalAlignment(SwingConstants.RIGHT);
        } else if (value instanceof Double d) {
            String s = formatter.format(d);
            component = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) component).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        return component;
    }
}
