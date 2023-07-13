package com.common;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);
    private int targetRow;
    private int targetColumn;
    private Color targetColor;

    public CustomTableCellRenderer(int targetRow, int targetColumn, Color targetColor) {
        this.targetRow = targetRow;
        this.targetColumn = targetColumn;
        this.targetColor = targetColor;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (row == targetRow && column == targetColumn) {
            component.setForeground(Color.BLACK);
            component.setBackground(targetColor);
        } else {
            // Reset the background color for other cells
            component.setBackground(UIManager.getColor("background"));
        }
        if (value instanceof Float d) {
            String s = formatter.format(d);
            component = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) component).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        return component;
    }
}