/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.StockCriteria;
import com.inventory.model.StockFormulaQty;
import com.inventory.model.StockFormulaQtyKey;
import com.inventory.model.StockUnit;
import com.repo.InventoryRepo;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class StockFormulaQtyTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Criteria Name", "Percent (%)", "Qty", "Unit", "Percent Allowed"};
    private JTable parent;
    private List<StockFormulaQty> listDetail = new ArrayList();
    private List<StockFormulaQtyKey> listDel = new ArrayList();
    private SelectionObserver observer;
    private JLabel lblRec;
    private boolean editable = true;
    private String formulaCode;
    private InventoryRepo inventoryRepo;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public JLabel getLblRec() {
        return lblRec;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public SelectionObserver getObserver() {
        return observer;
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
            case 2, 3, 5 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                StockFormulaQty record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode();
                    }
                    case 1 -> {
                        //code
                        return record.getCriteriaName();
                    }
                    case 2 -> {
                        //percent
                        return Util1.toNull(record.getPercent());
                    }
                    case 3 -> {
                        return Util1.toNull(record.getQty());
                    }
                    case 4 -> {
                        return record.getUnit();
                    }
                    case 5 -> {
                        return Util1.toNull(record.getPercentAllow());
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
            StockFormulaQty record = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof StockCriteria s) {
                            record.setUserCode(s.getUserCode());
                            record.setCriteriaCode(s.getKey().getCriteriaCode());
                            record.setCriteriaName(s.getCriteriaName());
                            record.setPercent(1);
                            addNewRow();
                            setSelection(row, 3);
                        }
                    }
                    case 2 -> {
                        //percent
                        if (Util1.getDouble(value) > 0) {
                            record.setPercent(Util1.getDouble(value));
                            setSelection(row, 3);
                        }
                    }
                    //price
                    case 3 -> {
                        record.setQty(Util1.getDouble(value));
                        setSelection(row, 4);
                    }
                    case 4 -> {
                        if (value instanceof StockUnit u) {
                            record.setUnit(u.getKey().getUnitCode());
                            setSelection(row + 1, 0);
                        }

                    }
                    case 5 -> {
                        record.setPercentAllow(Util1.getDouble(value));
                        setSelection(row + 1, 0);
                    }
                }
                record.getKey().setFormulaCode(formulaCode);
                setUniqueId(record, row);
                save(record, row);
                setRecord(listDetail.size() - 1);
                fireTableRowsUpdated(row, row);
                parent.requestFocusInWindow();
            }
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void setUniqueId(StockFormulaQty s, int row) {
        int uniqueId = s.getKey().getUniqueId();
        if (uniqueId == 0) {
            if (row == 0) {
                s.getKey().setUniqueId(1);
            } else {
                StockFormulaQty u = listDetail.get(row - 1);
                s.getKey().setUniqueId(u.getKey().getUniqueId() + 1);
            }
        }
    }

    private void save(StockFormulaQty sf, int row) {
        if (isValidEntry(sf)) {
            inventoryRepo.saveStockFormulaQty(sf).doOnSuccess((t) -> {
                if (t != null) {
                    listDetail.set(row, t);
                }
            }).subscribe();
        }
    }

    private boolean isValidEntry(StockFormulaQty sf) {
        if (sf.getCriteriaCode() == null) {
            return false;
        } else if (sf.getPercent() == 0) {
            return false;
        } else if (sf.getUnit() == null) {
            return false;
        } else if (isDuplicate()) {
            JOptionPane.showMessageDialog(parent, "Duplicate Criteria.");
            return false;
        }
        return true;
    }

    private boolean isDuplicate() {
        Map<String, Integer> stockCodeCountMap = new HashMap<>();
        for (StockFormulaQty detail : listDetail) {
            String code = detail.getCriteriaCode();
            stockCodeCountMap.put(code, stockCodeCountMap.getOrDefault(code, 0) + 1);

            // If the count is 2, return true indicating duplicate stockCode
            if (stockCodeCountMap.get(code) == 2) {
                return true;
            }
        }

        // No duplicates found
        return false;
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void setRecord(int size) {
        if (lblRec != null) {
            lblRec.setText("Records : " + size);
        }
    }

    public void addNewRow() {
//        if (listDetail != null) {
        if (!hasEmptyRow()) {
            StockFormulaQty pd = new StockFormulaQty();
            StockFormulaQtyKey key = new StockFormulaQtyKey();
            key.setCompCode(Global.compCode);
            key.setFormulaCode(formulaCode);
            pd.setKey(key);
            listDetail.add(pd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
//        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (this.listDetail == null) {
            return true;
        }
        if (listDetail.size() >= 1) {
            StockFormulaQty get = listDetail.get(listDetail.size() - 1);
            if (get.getCriteriaCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<StockFormulaQty> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockFormulaQty> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(parent, text);
    }

    public void clearDelList() {
        if (listDel != null) {
            listDel.clear();
        }
    }

    public void delete(int row) {
        StockFormulaQty sdh = listDetail.get(row);
        if (sdh.getKey() != null) {
            listDel.add(sdh.getKey());
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

    public void addObject(StockFormulaQty sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public StockFormulaQty getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            clearDelList();
            fireTableDataChanged();
        }
    }
}
