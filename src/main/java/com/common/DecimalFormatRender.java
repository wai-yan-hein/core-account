/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class DecimalFormatRender extends DefaultTableCellRenderer {

    private DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);

    public DecimalFormatRender() {
        formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);
    }

    public DecimalFormatRender(int format) {
        switch (format) {
            case 0 -> {
                formatter = null;
            }
            case 1 -> {
                formatter = new DecimalFormat(Util1.DECIMAL_FORMAT1);
            }
            case 2 -> {
                formatter = new DecimalFormat(Util1.DECIMAL_FORMAT2);
            }
            case 3 -> {
                formatter = new DecimalFormat(Util1.DECIMAL_FORMAT3);
            }
            default -> {
                formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);
            }
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Float f) {
            String s = formatter == null ? String.valueOf(f) : formatter.format(f);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        if (value instanceof Double d) {
            String s = formatter == null ? String.valueOf(d) : formatter.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        return c;
    }

}
