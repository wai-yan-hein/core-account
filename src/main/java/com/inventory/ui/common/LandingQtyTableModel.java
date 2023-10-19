/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.LandingHisQty;
import com.inventory.model.LandingHisQtyKey;
import com.inventory.model.StockCriteria;
import com.inventory.model.StockUnit;
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
public class LandingQtyTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Percent", "Percent Allowed", "Qty", "Unit", "Total"};
    private JTable parent;
    private List<LandingHisQty> listDetail = new ArrayList();
    private SelectionObserver observer;
    private List<LandingHisQtyKey> listDel = new ArrayList();
    private JLabel lblRec;

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public List<LandingHisQtyKey> getListDel() {
        return listDel;
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
            case 2, 3, 4, 6 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0, 1 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                LandingHisQty record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //Name
                        return record.getCriteriaUserCode();
                    }
                    case 1 -> {
                        //Name
                        return record.getCriteriaName();
                    }
                    case 2 -> {
                        //percent
                        return record.getPercent();
                    }
                    case 3 -> {
                        //percent
                        return Util1.toNull(record.getPercentAllow());
                    }
                    case 4 -> {
                        return Util1.toNull(record.getQty());
                    }
                    case 5 -> {
                        return record.getUnit();
                    }
                    case 6 -> {
                        return Util1.toNull(record.getTotalQty());
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
                LandingHisQty record = listDetail.get(row);
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof StockCriteria s) {
                            record.setCriteriaCode(s.getKey().getCriteriaCode());
                            record.setCriteriaName(s.getCriteriaName());
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        //percent
                        if (Util1.getDouble(value) >= 0) {
                            record.setPercent(Util1.getDouble(value));
                            if (row == listDetail.size() - 1) {
                                setSelection(row, 3);
                            } else {
                                setSelection(row + 1, 2);
                            }
                        }
                    }
                    //qty
                    case 3 -> {
                        record.setPercentAllow(Util1.getDouble(value));
                        setSelection(row, 4);
                    }
                    //qty
                    case 4 -> {
                        record.setQty(Util1.getDouble(value));
                        setSelection(row, 4);
                    }
                    case 5 -> {
                        if (value instanceof StockUnit u) {
                            record.setUnit(u.getKey().getUnitCode());
                        }
                    }
                }
                setRecord(listDetail.size() - 1);
                calAmount(record);
                fireTableRowsUpdated(row, row);
                parent.requestFocusInWindow();
            }
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void calAmount(LandingHisQty l) {
        double percentAllow = l.getPercentAllow();
        double percent = l.getPercent();
        double qty = l.getQty();
        if (percentAllow > 0 && percent > 0) {
            if (percent >= percentAllow) {
                double diff = percentAllow - percent;
                l.setTotalQty(diff * qty);
            } else {
                l.setTotalQty(0);
            }
        } else {
            l.setTotalQty(percent * qty);
        }
        observer.selected("CAL_QTY", "CAL_QTY");
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void setRecord(int size) {
        if (lblRec != null) {
            lblRec.setText(String.valueOf(size));
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                LandingHisQty pd = new LandingHisQty();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            LandingHisQty get = listDetail.get(listDetail.size() - 1);
            if (get.getCriteriaCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<LandingHisQty> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LandingHisQty> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(parent, text);
    }

    public boolean isValidEntry() {
        return true;
    }

    public void clearDelList() {
        if (listDel != null) {
            listDel.clear();
        }
    }

    public void delete(int row) {
        LandingHisQty sdh = listDetail.get(row);
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

    public void addObject(LandingHisQty sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public LandingHisQty getObject(int row) {
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
