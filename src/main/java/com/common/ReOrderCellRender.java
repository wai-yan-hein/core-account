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
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class ReOrderCellRender extends DefaultTableCellRenderer {

    public ReOrderCellRender() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof String str) {
            switch (str) {
                case "Below-Min" ->
                    ((JLabel) c).setForeground(Color.red);
                case "Over-Min" ->
                    ((JLabel) c).setForeground(Color.green);
                case "Below-Max" ->
                    ((JLabel) c).setForeground(Color.yellow);
                default ->
                    ((JLabel) c).setForeground(Color.blue);
            }
        }
        ((JLabel) c).setFont(Global.lableFont);
        return c;
    }

}
