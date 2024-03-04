/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.GradeDetail;
import com.inventory.entity.GradeDetailKey;
import com.inventory.entity.MessageType;
import com.inventory.entity.Stock;
import com.repo.InventoryRepo;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
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
public class GradeDetailTableModel extends AbstractTableModel {

    private String[] columnNames = {"Grade Stock", "Min (%)", "Max (%)"};
    private JTable parent;
    private List<GradeDetail> listDetail = new ArrayList();
    private List<GradeDetailKey> listDel = new ArrayList();
    private SelectionObserver observer;
    private JLabel lblRec;
    private boolean editable = true;
    private String formulaCode;
    private String criteriaCode;
    private InventoryRepo inventoryRepo;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
    }

    public void setCriteriaCode(String criteriaCode) {
        this.criteriaCode = criteriaCode;
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
            case 0 ->
                String.class;
            default ->
                Double.class;
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
                GradeDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //percent
                        return record.getStockName();
                    }
                    case 1 -> {
                        //code
                        return record.getMinPercent();
                    }
                    case 2 -> {
                        //code
                        return record.getMaxPercent();
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
            GradeDetail record = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0 -> {
                        if (value instanceof Stock s) {
                            record.setGradeStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                        }
                    }
                    case 1 -> {
                        if (Util1.getDouble(value) >= 0) {
                            //Code
                            record.setMinPercent(Util1.getDouble(value));
                        }
                    }
                    case 2 -> {
                        //percent
                        if (Util1.getDouble(value) >= 0) {
                            //Code
                            record.setMaxPercent(Util1.getDouble(value));
                        }
                    }
                    //price

                }

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

    private void setUniqueId(GradeDetail s, int row) {
        s.getKey().setFormulaCode(formulaCode);
        s.getKey().setCriteriaCode(criteriaCode);
        int uniqueId = s.getKey().getUniqueId();
        if (uniqueId == 0) {
            if (row == 0) {
                s.getKey().setUniqueId(1);
            } else {
                GradeDetail u = listDetail.get(row - 1);
                s.getKey().setUniqueId(u.getKey().getUniqueId() + 1);
            }
        }
    }

    private void save(GradeDetail sf, int row) {
        if (isValidEntry(sf)) {
            inventoryRepo.saveGradeDetail(sf).doOnSuccess((t) -> {
                if (t != null) {
                    listDetail.set(row, t);
                    addNewRow();
                    sendMessage(t.getStockName());
                }
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.GRADE_DETAIL, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private boolean isValidEntry(GradeDetail sf) {
        return sf.getMinPercent() >= 0 || sf.getMaxPercent() >= 0;
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
        if (!hasEmptyRow()) {
            GradeDetail pd = new GradeDetail();
            GradeDetailKey key = new GradeDetailKey();
            key.setCompCode(Global.compCode);
            key.setFormulaCode(formulaCode);
            key.setCriteriaCode(criteriaCode);
            pd.setKey(key);
            listDetail.add(pd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            GradeDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getMinPercent() == 0 && get.getMaxPercent() == 0) {
                return true;
            }
        }
        return false;
    }

    public List<GradeDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<GradeDetail> listDetail) {
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
        GradeDetail sdh = listDetail.get(row);
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

    public void addObject(GradeDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public GradeDetail getObject(int row) {
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
