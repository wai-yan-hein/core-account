/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class FontCellRender extends DefaultTableCellRenderer {

    public FontCellRender() {
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
            c.setBackground(UIManager.getDefaults().getColor("Table.selectionBackground"));
        }
        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel) c).setFont(Global.lableFont);
        return c;
    }

}
