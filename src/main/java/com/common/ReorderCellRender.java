/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.inventory.model.ReorderLevel;
import com.inventory.ui.common.ReorderTableModel;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class ReorderCellRender extends DefaultTableCellRenderer {

    private final JCheckBox check = new JCheckBox();
    private final Color bgColor = new Color(213, 235, 226);
    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);

    public ReorderCellRender() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ReorderTableModel model = (ReorderTableModel) table.getModel();
        if (model != null) {
            ReorderLevel order = model.getReorder(table.convertRowIndexToModel(row));
            float minQty = Util1.getFloat(order.getMinSmallQty());
            float maxQty = Util1.getFloat(order.getMaxSmallQty());
            float balQty = Util1.getFloat(order.getBalSmallQty());
            if (balQty < minQty) {
                c.setBackground(Color.RED);
            }
            if (balQty > maxQty) {
                c.setBackground(Color.GREEN);
            }
        }
        return c;
    }

}
