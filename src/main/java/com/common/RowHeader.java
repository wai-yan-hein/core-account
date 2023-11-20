/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.inventory.ui.common.RowNumberListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author Lenovo
 */
public class RowHeader {

    private RowNumberListModel rowNumberListModel = new RowNumberListModel();

    public JList createRowHeader(JTable table,int width) {
        addListener(table);
        JList rowHeader = new JList(rowNumberListModel);
        rowHeader.setFixedCellWidth(width);
        rowHeader.setFixedCellHeight(table.getRowHeight()
                + table.getRowMargin());
//                             + table.getIntercellSpacing().height);
        rowNumberListModel.setRowCount(table.getRowCount());
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        return rowHeader;
    }

    private void addListener(JTable table) {
        table.getModel().addTableModelListener((e) -> {
            if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE) {
                rowNumberListModel.setRowCount(table.getRowCount());
            }
        });
    }

}
