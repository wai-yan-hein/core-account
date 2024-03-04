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
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.SaleDetailKey;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.Sale;
import com.inventory.ui.entry.SaleDynamic;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SaleQtyTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<SaleDetailKey> deleteList = new ArrayList();
    @Setter
    private SaleDynamic sale;
    private InventoryRepo inventoryRepo;
    private JLabel lblStockName;
    private JButton btnProgress;
    private JDateChooser vouDate;
    private JLabel lblRecord;

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JLabel getLblRecord() {
        return lblRecord;
    }

    public void setLblRecord(JLabel lblRecord) {
        this.lblRecord = lblRecord;
    }

    public JDateChooser getVouDate() {
        return vouDate;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public JLabel getLblStockName() {
        return lblStockName;
    }

    public void setLblStockName(JLabel lblStockName) {
        this.lblStockName = lblStockName;
    }

    public JButton getBtnProgress() {
        return btnProgress;
    }

    public void setBtnProgress(JButton btnProgress) {
        this.btnProgress = btnProgress;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public SelectionObserver getSelectionObserver() {
        return selectionObserver;
    }

    public void setObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
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
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 2, 4, 5 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 5 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleHisDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode() == null ? sd.getStockCode() : sd.getUserCode();
                }
                case 1 -> {
                    return sd.getStockName();
                }
                case 2 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 3 -> {
                    return sd.getUnitCode();
                }
                case 4 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 5 -> {
                    //amount
                    return Util1.toNull(sd.getAmount());
                }
                default -> {
                    return null;
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
            SaleHisDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setQty(1.0);
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setStock(s);
                            sd.setCalculate(s.isCalculate());
                            sd.setPrice(s.getSalePriceN());
                            parent.setColumnSelectionInterval(2, 2);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(qty);
                            if (sd.getUnitCode() == null) {
                                parent.setColumnSelectionInterval(3, 4);
                            } else {
                                parent.setColumnSelectionInterval(4, 4);
                            }
                        }
                    }
                    case 3 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }

                    }

                    case 4 -> {
                        //price
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(price);
                        }
                    }
                    case 5 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));

                    }

                }
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                selectionObserver.selected("SALE-TOTAL", "SALE-TOTAL");
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
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
        boolean status = false;
        if (listDetail.size() >= 1) {
            SaleHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<SaleHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListOrderDetail(List<OrderHisDetail> listDetail) {
        for (OrderHisDetail ld : listDetail) {
            SaleHisDetail sdl = new SaleHisDetail();
            sdl.setAmount(Util1.getDouble(ld.getAmount()));
            sdl.setBrandName(ld.getBrandName());
            sdl.setCatName(ld.getCatName());
            sdl.setGroupName(ld.getGroupName());
            sdl.setLocCode(ld.getLocCode());
            sdl.setLocName(ld.getLocName());
            sdl.setPrice(Util1.getDouble(ld.getPrice()));
            sdl.setQty(Util1.getDouble(ld.getQty()));
            sdl.setRelName(ld.getRelName());
            sdl.setStockCode(ld.getStockCode());
            sdl.setStockName(ld.getStockName());
            sdl.setTraderName(ld.getTraderName());
            sdl.setUnitCode(ld.getUnitCode());
            this.listDetail.add(sdl);
        }
        setRecord(listDetail.size());
        addNewRow();
    }

    public void setListDetail(List<SaleHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        addNewRow();
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(SaleHisDetail sale) {
        if (sale.getStockCode() != null) {
            double qty = Util1.getDouble(sale.getQty());
            double price = Util1.getDouble(sale.getPrice());
            double amount = qty * price;
            sale.setAmount(Util1.getDouble(Math.round(amount)));
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (SaleHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (Util1.getFloat(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Sale Unit.");
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

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
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
        }
    }
}
