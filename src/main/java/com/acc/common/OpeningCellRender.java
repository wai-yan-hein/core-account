/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.common.ProUtil;
import com.common.Util1;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class OpeningCellRender extends DefaultTableCellRenderer {

    private DecimalFormat formatter = new DecimalFormat("###,##0;(###,##0)");

    public OpeningCellRender() {
        int place = ProUtil.getDecimalPalace();
        formatter = switch (place) {
            case 1 ->
                new DecimalFormat(Util1.DECIMAL_FORMAT1);
            case 2 ->
                new DecimalFormat(Util1.DECIMAL_FORMAT4);
            default ->
                new DecimalFormat(Util1.DECIMAL_FORMAT1);
        };
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            c.setBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        }
        if (value instanceof Double d) {
            String s = formatter.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);

        }
        return c;
    }

}
