/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.LandingHisPrice;
import com.inventory.model.LandingHisPriceKey;
import com.inventory.model.LandingHisDetail;
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
public class LandingPriceTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Percent", "Percent Allowed", "Price", "Amount"};
    private JTable parent;
    private List<LandingHisPrice> listDetail = new ArrayList();
    private SelectionObserver observer;
    private List<LandingHisPriceKey> listDel = new ArrayList();
    private JLabel lblRec;
    private boolean editable = true;
    private LandingHisDetail landingHisDetail;

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public List<LandingHisPriceKey> getListDel() {
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
            case 2, 3, 4, 5 ->
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
                LandingHisPrice record = listDetail.get(row);
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
                        return Util1.toNull(record.getPrice());
                    }
                    case 5 -> {
                        return Util1.toNull(record.getAmount());
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
                LandingHisPrice record = listDetail.get(row);
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
                    //price
                    case 3 -> {
                        record.setPrice(Util1.getDouble(value));
                        setSelection(row, 4);
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

    private void calAmount(LandingHisPrice l) {
        double percentAllow = l.getPercentAllow();
        double percent = l.getPercent();
        double price = l.getPrice();
        if (percentAllow > 0) {
            if (percent <= percentAllow) {
                double diff = percentAllow - percent;
                l.setAmount(diff * price);
            }
        } else {
            l.setAmount(percent * price);
        }
        observer.selected("CAL_CRITERIA", "CAL_CRITERIA");
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
                LandingHisPrice pd = new LandingHisPrice();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            LandingHisPrice get = listDetail.get(listDetail.size() - 1);
            if (get.getCriteriaCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<LandingHisPrice> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LandingHisPrice> listDetail) {
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
        LandingHisPrice sdh = listDetail.get(row);
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

    public void addObject(LandingHisPrice sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public LandingHisPrice getObject(int row) {
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
