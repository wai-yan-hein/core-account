/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Stock;
import com.inventory.model.ConsignHisDetail;
import com.inventory.model.ConsignHisDetailKey;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author pann
 */
@Slf4j
public class StockIssueRecTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Stock Name", "Moisture", "Hard Rice", "Weight", "Bag", "Total Weight"};
    private JTable parent;
    private List<ConsignHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<ConsignHisDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;

    public JDateChooser getVouDate() {
        return vouDate;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 6 ->
                false;
            default ->
                true;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                ConsignHisDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode() == null ? record.getStockCode() : record.getUserCode();
                    }
                    case 1 -> {
                        return record.getStockName();
                    }
                    case 2 -> {
                        return Util1.toNull(record.getWet());
                    }
                    case 3 -> {
                        return Util1.toNull(record.getRice());
                    }
                    case 4 -> {
                        return Util1.toNull(record.getWeight());
                    }
                    case 5 -> {
                        return Util1.toNull(record.getBag());
                    }
                    case 6 -> {
                        return Util1.toNull(record.getTotalWeight());
                    }
                    default -> {
                        return new Object();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (value != null) {
                ConsignHisDetail pd = listDetail.get(row);
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            pd.setStockCode(s.getKey().getStockCode());
                            pd.setStockName(s.getStockName());
                            pd.setUserCode(s.getUserCode());
                            pd.setWeight(Util1.getDouble(s.getWeight()));
                            pd.setQty(1.0);
                        }
                        addNewRow();
                        parent.setColumnSelectionInterval(2, 2);
                    }
                    case 2 -> {
                        double wet = Util1.getDouble(value);
                        if (wet > 0) {
                            pd.setWet(wet);
                        }
                    }
                    case 3 -> {
                        double rice = Util1.getDouble(value);
                        if (rice > 0) {
                            pd.setRice(rice);
                        }
                    }
                    case 4 -> {
                        double wt = Util1.getDouble(value);
                        if (wt > 0) {
                            pd.setWeight(wt);
                        }
                    }
                    case 5 -> {
                        double bag = Util1.getDouble(value);
                        if (bag > 0) {
                            pd.setBag(bag);
                        }
                    }
                }
                calculateAmount(pd);
                fireTableRowsUpdated(row, row);
                observer.selected("CAL-TOTAL", "CAL-TOTAL");
                parent.requestFocus();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }
    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                ConsignHisDetail pd = new ConsignHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            ConsignHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<ConsignHisDetail> getListDetail() {
        List<ConsignHisDetail> filter = listDetail.stream().filter((t) -> !Util1.isNullOrEmpty(t.getStockCode())).toList();
        return filter;
    }

    public void setListDetail(List<ConsignHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    private void calculateAmount(ConsignHisDetail pur) {
        pur.setTotalWeight(pur.getWeight() * pur.getBag());
    }

    public boolean isValidEntry() {
        for (ConsignHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (Util1.getDouble(sdh.getBag()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Bag.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    public List<ConsignHisDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        ConsignHisDetail sdh = listDetail.get(row);
        if (sdh.getKey() != null) {
            deleteList.add(sdh.getKey());
        }
        listDetail.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
        parent.requestFocus();
    }

    public void addStockIssRec(ConsignHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public ConsignHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

    public ConsignHisDetail getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }
}
