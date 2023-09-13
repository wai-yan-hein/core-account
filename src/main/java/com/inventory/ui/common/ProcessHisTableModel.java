/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Util1;
import com.inventory.model.ProcessHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class ProcessHisTableModel extends AbstractTableModel {

    private List<ProcessHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Code", "Stock Name", "Location", "Process Type"};

    public ProcessHisTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            ProcessHis med = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    Util1.convertToLocalStorage(med.getVouDateTime());
                case 1 ->
                    med.getStockUsrCode() == null ? med.getStockCode() : med.getStockUsrCode();
                case 2 ->
                    med.getStockName();
                case 3 ->
                    med.getLocName();
                case 4 ->
                    med.getPtName();
                default ->
                    null;
            }; //Code
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ProcessHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<ProcessHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public ProcessHis getObject(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(ProcessHis stock) {
        if (listDetail != null) {
            listDetail.add(stock);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);

        }
    }

    public void setObject(int row, ProcessHis stock) {
        if (listDetail != null) {
            listDetail.set(row, stock);
            fireTableRowsUpdated(row, row);
        }
    }

    public void deleteObject(int row) {
        if (row >= 0) {
            if (!listDetail.isEmpty()) {
                listDetail.remove(row);
                fireTableDataChanged();
            }
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
