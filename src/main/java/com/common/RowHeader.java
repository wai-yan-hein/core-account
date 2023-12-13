/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.inventory.ui.common.RowNumberListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RowHeader {

    private RowNumberListModel rowNumberListModel = new RowNumberListModel();

    public JList createRowHeader(JTable table, int width) {
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
            switch (e.getType()) {
                case TableModelEvent.INSERT -> rowNumberListModel.setRowCount(table.getRowCount() + 1);
                case TableModelEvent.DELETE -> rowNumberListModel.setRowCount(table.getRowCount());
                case TableModelEvent.UPDATE -> rowNumberListModel.setRowCount(table.getRowCount());
                default -> {
                }
            }
        });
    }

}
