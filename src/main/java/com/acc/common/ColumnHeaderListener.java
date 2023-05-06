/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Lenovo
 */
public class ColumnHeaderListener extends MouseAdapter {

    private JTable table;

    public ColumnHeaderListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TableColumnModel columnModel = table.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
        int column = table.convertColumnIndexToModel(viewColumn);
        if (e.getClickCount() == 1 && column != -1) {
            // Get the preferred width of the column based on the cell data
            int preferredWidth = getPreferredColumnWidth(column);

            // Set the preferred width of the column
            TableColumn tableColumn = columnModel.getColumn(viewColumn);
            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    private int getPreferredColumnWidth(int column) {
        int maxWidth = 0;
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        TableCellRenderer cellRenderer = tableColumn.getCellRenderer();
        if (cellRenderer == null) {
            cellRenderer = new DefaultTableCellRenderer();
        }
        for (int row = 0; row < table.getRowCount(); row++) {
            Component cell = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, column), false, false, row, column);
            maxWidth = Math.max(maxWidth, cell.getPreferredSize().width);
        }
        return maxWidth + table.getIntercellSpacing().width;
    }
}
