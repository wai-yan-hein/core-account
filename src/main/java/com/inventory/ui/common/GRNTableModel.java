/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.GRNDetail;
import com.inventory.entity.GRNDetailKey;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.GRNEntry;
import com.toedter.calendar.JDateChooser;
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
public class GRNTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Relation", "Location", "Weight", "Weight Unit", "Qty", "Unit", "Total"};
    private JTable parent;
    private List<GRNDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private List<GRNDetailKey> listDel = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;
    private boolean editable = true;
    private GRNEntry grn;

    public void setGrn(GRNEntry grn) {
        this.grn = grn;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<GRNDetailKey> getListDel() {
        return listDel;
    }

    public void setListDel(List<GRNDetailKey> listDel) {
        this.listDel = listDel;
    }

    public JLabel getLblRec() {
        return lblRec;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

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
                GRNDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode() == null ? record.getStockCode() : record.getUserCode();
                    }
                    case 1 -> {
                        //Name
                        String stockName = null;
                        if (record.getStockCode() != null) {
                            stockName = record.getStockName();
                        }
                        return stockName;
                    }
                    case 2 -> {
                        return record.getRelName();
                    }
                    case 3 -> {
                        //loc
                        return record.getLocName();
                    }
                    case 4 -> {
                        if (Util1.getDouble(record.getWeight()) == 0) {
                            return null;
                        }
                        return record.getWeight();
                    }
                    case 5 -> {
                        return record.getWeightUnit();
                    }
                    case 6 -> {
                        //qty
                        return record.getQty();
                    }
                    case 7 -> {
                        //unit
                        return record.getUnit();
                    }

                    case 8 -> {
                        double ttl = Util1.getDouble(record.getTotalWeight());
                        return ttl == 0 ? null : ttl;
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
            GRNDetail record = listDetail.get(row);

            String key = "stock.use.weight";
            switch (column) {
                case 0, 1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock s) {
                            record.setStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                            record.setUserCode(s.getUserCode());
                            record.setRelName(s.getRelName());
                            record.setQty(1.0);
                            record.setUnit(s.getPurUnitCode());
                            record.setWeight(s.getWeight());
                            record.setWeightUnit(s.getWeightUnit());
                            record.setStock(s);
                            addNewRow();
                            if (ProUtil.isUseWeight()) {
                                setSelection(row, 4);
                            } else {
                                setSelection(row, 6);
                            }
                        }
                    }
                }
                case 3 -> {
                    //Loc
                    if (value instanceof Location l) {
                        record.setLocCode(l.getKey().getLocCode());
                        record.setLocName(l.getLocName());
                    }
                }
                case 4 -> {
                    record.setWeight(Util1.getDouble(value));
                    setSelection(row, 6);
                }
                case 5 -> {
                    if (value instanceof StockUnit u) {
                        record.setWeightUnit(u.getKey().getUnitCode());
                        setSelection(row + 1, 0);
                    }
                }
                case 6 -> {
                    //Qty
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            if (ProUtil.isUseWeightPoint()) {
                                String str = String.valueOf(value);
                                double wt = Util1.getDouble(record.getWeight());
                                record.setQty(Util1.getDouble(value));
                                record.setTotalWeight(Util1.getTotalWeight(wt, str));
                            } else {
                                record.setQty(Util1.getDouble(value));
                                if (record.getQty() != null && record.getWeight() != null) {
                                    record.setTotalWeight(Util1.getDouble(record.getQty()) * Util1.getDouble(record.getWeight()));
                                }
                            }

                            setSelection(row, column);
                        } else {
                            showMessageBox("Input value must be positive");
                            setSelection(row, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        setSelection(row, column);
                    }
                }
                case 7 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit u) {
                            record.setUnit(u.getKey().getUnitCode());
                            setSelection(row, 6);
                        }
                    }
                }

            }
            assignLocation(record);
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

    private void assignLocation(GRNDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = grn.getLocationAutoCompleter();
            if (completer != null) {
                Location l = completer.getLocation();
                if (l != null) {
                    sd.setLocCode(l.getKey().getLocCode());
                    sd.setLocName(l.getLocName());
                }
            }
        }
    }

    private void setRecord(int size) {
        if (lblRec != null) {
            lblRec.setText("Records : " + size);
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                GRNDetail pd = new GRNDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            GRNDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<GRNDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<GRNDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (GRNDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                } else if (sdh.getUnit() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    status = false;
                    parent.requestFocus();
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
        GRNDetail sdh = listDetail.get(row);
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

    public void addObject(GRNDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public GRNDetail getObject(int row) {
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
