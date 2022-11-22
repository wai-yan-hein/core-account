/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.common.Global;
import com.common.Util1;
import java.awt.Color;
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
public class GLTableCellRender extends DefaultTableCellRenderer {

    private final Color bgColor = new Color(213, 235, 226);
    private final Color zeroColor = new Color(204, 242, 196);
    private double drAmt = 0.0;
    private double crAmt = 0.0;
    private int row1;
    private int row2;
    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);

    public GLTableCellRender() {
    }

    public GLTableCellRender(int row1, int row2) {
        this.row1 = row1;
        this.row2 = row2;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(row % 2 == 0 ? bgColor : Color.WHITE);
        drAmt = Util1.getDouble(table.getValueAt(row, row1));
        crAmt = Util1.getDouble(table.getValueAt(row, row2));
        if ((drAmt + crAmt) == 0) {
            c.setBackground(zeroColor);
        }
        if (isSelected) {
            c.setBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        }
        String s;
        if (value instanceof Double d) {
            s = formatter.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setFont(Global.lableFont);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }

        return c;
    }

}
