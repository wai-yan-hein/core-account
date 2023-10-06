/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.LandingDetailCriteria;
import com.inventory.model.LandingDetailCriteriaKey;
import com.inventory.model.StockCriteria;
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
public class GradeCriteriaTableModel extends AbstractTableModel {

    private String[] columnNames = {"Description", "Percent", "Price", "Amount"};
    private JTable parent;
    private List<LandingDetailCriteria> listDetail = new ArrayList();
    private SelectionObserver observer;
    private List<LandingDetailCriteriaKey> listDel = new ArrayList();
    private JLabel lblRec;
    private boolean editable = true;

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
            case 4, 5, 6, 8 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (editable) {
            return switch (column) {
                case 2, 8 ->
                    false;
                default ->
                    true;
            };
        }
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                LandingDetailCriteria record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //Name
                        record.getCriteriaName();
                    }
                    case 1 -> {
                        //percent
                        return Util1.toNull(record.getPercent());
                    }
                    case 2 -> {
                        return Util1.toNull(record.getPrice());
                    }
                    case 3 -> {
                        return record.getAmount();
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
            LandingDetailCriteria record = listDetail.get(row);
            switch (column) {
                case 0, 1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof StockCriteria s) {
                            record.setCriteiaCode(s.getKey().getCriteriaCode());
                            record.setCriteriaName(s.getCriteriaName());
                            addNewRow();
                        }
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
                    record.setPrice(Util1.getDouble(value));
                    setSelection(row, 4);
                }
            }
            setRecord(listDetail.size() - 1);
            fireTableRowsUpdated(row, row);
            parent.requestFocusInWindow();
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
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
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                LandingDetailCriteria pd = new LandingDetailCriteria();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            LandingDetailCriteria get = listDetail.get(listDetail.size() - 1);
            if (get.getCriteiaCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<LandingDetailCriteria> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LandingDetailCriteria> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(parent, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (LandingDetailCriteria sdh : listDetail) {
            if (sdh.getCriteiaCode() != null) {
                if (sdh.getAmount() == 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.");
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return status;
    }

    public void clearDelList() {
        if (listDel != null) {
            listDel.clear();
        }
    }

    public void delete(int row) {
        LandingDetailCriteria sdh = listDetail.get(row);
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

    public void addObject(LandingDetailCriteria sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public LandingDetailCriteria getObject(int row) {
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
