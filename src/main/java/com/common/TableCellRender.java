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
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class TableCellRender extends DefaultTableCellRenderer {

    private final JCheckBox check = new JCheckBox();
    private DecimalFormat formatter;

    public TableCellRender() {
        formatter = ProUtil.getDecimalFormat();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (Util1.DARK_MODE) {
            c.setBackground(row % 2 == 0 ? Color.DARK_GRAY : UIManager.getColor("background"));
        } else {
            c.setBackground(row % 2 == 0 ? Global.BG_COLOR : Color.WHITE);
        }
        if (isSelected) {
            c.setBackground(Global.selectionColor);
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
