/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.acc.model.VDescription;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.SaleDetailKey;
import com.inventory.entity.SaleHisDetail;
import com.inventory.ui.entry.SaleDynamic;
import com.repo.InventoryRepo;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author DELL
 */
@Slf4j
public class SaleDesginTableModel extends AbstractTableModel {

    private String[] columnNames = {"Design", "Size", "Length", "Height", "Divider", "Total Sqft", "Order Qty", "Price", "Amount"};
    @Setter
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<SaleDetailKey> deleteList = new ArrayList();
    @Setter
    private SaleDynamic sale;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private JDateChooser vouDate;
    @Setter
    private JLabel lblRecord;

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
        switch (column) {
            case 6, 8 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleHisDetail sd = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    sd.getDesign();
                case 1 ->
                    sd.getSize();
                case 2 ->
                    Util1.toNull(Util1.getDouble(sd.getLength()));
                case 3 ->
                    Util1.toNull(Util1.getDouble(sd.getHeight()));
                case 4 ->
                    Util1.toNull(Util1.getDouble(sd.getDivider()));
                case 5 ->
                    Util1.toNull(Util1.getDouble(sd.getQty()));
                case 6 ->
                    Util1.toNull(Util1.getDouble(sd.getTotalSqft()));
                case 7 ->
                    Util1.toNull(Util1.getDouble(sd.getPrice()));
                case 8 ->
                    Util1.toNull(Util1.getDouble(sd.getAmount()));
                default ->
                    null;
            };
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            SaleHisDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0 -> {
                        //Code
                        if (value instanceof VDescription d) {
                            sd.setDesign(d.getDescription());
                        } else {
                            sd.setDesign(value.toString());
                        }
                        sd.setDivider(144);
                        addNewRow();
                        setSelection(row, column + 1);
                    }
                    case 1 -> {
                        //Code
                        if (value instanceof VDescription d) {
                            sd.setSize(d.getDescription());
                        } else {
                            sd.setSize(value.toString());
                        }
                        setSelection(row, column + 1);
                    }
                    case 2 -> {
                        double length = Util1.getDouble(value);
                        if (length > 0) {
                            sd.setLength(length);
                            setSelection(row, column + 1);
                        }
                    }
                    case 3 -> {
                        double height = Util1.getDouble(value);
                        if (height > 0) {
                            sd.setHeight(height);
                            setSelection(row, column + 1);
                        }
                    }
                    case 4 -> {
                        double divider = Util1.getDouble(value);
                        if (divider > 0) {
                            sd.setQty(divider);
                            setSelection(row, column + 1);
                        }
                    }
                    case 5 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(qty);
                            setSelection(row, column + 1);
                        }
                    }
                    case 7 -> {
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setPrice(Util1.getDouble(value));
                                sd.setOrgPrice(sd.getPrice());
                                parent.setColumnSelectionInterval(0, 0);
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                }
                if (column != 8) {
                    if (sd.getPrice() == 0) {
                        if (ProUtil.isSaleLastPrice()) {
                            String stockCode = sd.getStockCode();
                            if (stockCode != null && sd.getUnitCode() != null) {
                                inventoryRepo.getSaleRecentPrice(stockCode,
                                        Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), sd.getUnitCode()).doOnSuccess((t) -> {
                                    sd.setPrice(t == null ? 0 : Util1.getDouble(t.getAmount()));
                                    sd.setOrgPrice(sd.getPrice());
                                    calculateAmount(sd);
                                    fireTableRowsUpdated(row, row);
                                }).subscribe();
                            }
                        }
                    }
                }
                sd.setStockCode("-");
                sd.setUnitCode("-");
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    private void assignLocation(SaleHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = sale.getLocationAutoCompleter();
            if (completer != null) {
                Location l = completer.getLocation();
                if (l != null) {
                    sd.setLocCode(l.getKey().getLocCode());
                    sd.setLocName(l.getLocName());
                }
            }
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleHisDetail pd = new SaleHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            SaleHisDetail get = listDetail.get(listDetail.size() - 1);
            if (Util1.isNullOrEmpty(get.getDesign())) {
                return true;
            }
        }
        return false;
    }

    public List<SaleHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<SaleHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    private void calculateAmount(SaleHisDetail oh) {
        if (oh.getStockCode() != null) {
            double amount = Util1.getDouble(oh.getQty()) * Util1.getDouble(oh.getPrice());
            oh.setAmount(Util1.getDouble(Math.round(amount)));

            double totalSqft = Util1.getDouble(oh.getQty()) * Util1.getDouble((oh.getLength() * oh.getHeight()) / oh.getDivider());
            oh.setTotalSqft(totalSqft);
            observer.selected("SALE-TOTAL", "SALE-TOTAL");
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (SaleHisDetail sdh : listDetail) {
            if (!Util1.isNullOrEmpty(sdh.getDesign())) {
                if (sdh.getQty() <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Qty.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<SaleDetailKey> getDelList() {
        return deleteList;
    }

    public void delete(int row) {
        SaleHisDetail sdh = listDetail.get(row);
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

    public void addSale(SaleHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public SaleHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }
}
