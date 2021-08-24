/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class TableCellRender extends DefaultTableCellRenderer {

    private final JCheckBox check = new JCheckBox();
    private final Color bgColor = new Color(245, 245, 245);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(row % 2 == 0 ? bgColor : Color.WHITE);
        /* if (table.isColumnSelected(column)) {
        c.setBackground(new Color(38, 117, 191));
        }*/
        if (isSelected) {
            c.setBackground(new Color(38, 117, 191));
        }

        String s;
        if (value instanceof Double double1) {
            DecimalFormat dFormat = new DecimalFormat("#,##0.###");
            Double d = double1;
            s = dFormat.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);

        }
        if (value instanceof Float float1) {
            DecimalFormat dFormat = new DecimalFormat("#,##0.###");
            Float d = float1;
            s = dFormat.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        if (value instanceof Integer) {
            JLabel lblInt = (JLabel) c;
            lblInt.setHorizontalAlignment(SwingConstants.RIGHT);
            lblInt.setBackground(c.getBackground());
        }

        if (value instanceof Boolean boolean1) {
            check.setSelected(boolean1);
            check.setHorizontalAlignment(SwingConstants.CENTER);
            check.setBackground(c.getBackground());
            c = check;
        }

        return c;
    }

}
