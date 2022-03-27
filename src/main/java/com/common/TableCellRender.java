/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class TableCellRender extends DefaultTableCellRenderer {

    private final JCheckBox check = new JCheckBox();
    private final Color bgColor = new Color(213, 235, 226);
    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);

    public TableCellRender() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(row % 2 == 0 ? bgColor : Color.WHITE);
        if (isSelected) {
            c.setBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        }
        String s;
        if (value instanceof Double d) {
            s = formatter.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);

        }
        if (value instanceof Float d) {
            s = formatter.format(d);
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
