/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.ProcessHisDetail;
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
public class ProcessHisDetailTableModel extends AbstractTableModel {

    private List<ProcessHisDetail> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Stock Code", "Stock Name", "Location", "Qty", "Unit", "Price", "Amount"};

    public ProcessHisDetailTableModel() {
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
        switch (column) {
            case 5,7,8 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            ProcessHisDetail med = listDetail.get(row);

            return switch (column) {
                case 0 ->
                    med.getVouDate();
                case 1 ->
                    med.getVouDate();
                case 2 ->
                    med.getStockUsrCode();
                case 3 ->
                    med.getStockName();
                case 4 ->
                    med.getLocName();
                case 5 ->
                    med.getQty();
                case 6 ->
                    med.getUnit();
                case 7 ->
                    med.getPrice();
                case 8 ->
                    med.getAmount();
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

    public List<ProcessHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<ProcessHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public ProcessHisDetail getObject(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(ProcessHisDetail stock) {
        if (listDetail != null) {
            listDetail.add(stock);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);

        }
    }

    public void setObject(int row, ProcessHisDetail stock) {
        if (listDetail != null) {
            listDetail.set(row, stock);
            fireTableRowsUpdated(row, row);
        }
    }

    public void deleteObject(int row) {
        if (listDetail != null) {
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
