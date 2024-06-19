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
import com.inventory.entity.GRN;
import com.inventory.entity.Location;
import com.inventory.entity.SaleDetailKey;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.SaleDynamic;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
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
public class SaleByBatchTableModel extends AbstractTableModel {

    private String[] columnNames = {"Batch No", "Sup :", "Code", "Description", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private InventoryRepo inventoryRepo;
    private JLabel lblStockName;
    private JButton btnProgress;
    private JDateChooser vouDate;
    private boolean change = false;
    private JLabel lblRecord;
    private SaleDynamic sale;
    private StockBalanceFrame dialog;

    public void setDialog(StockBalanceFrame dialog) {
        this.dialog = dialog;
    }

    public void setSale(SaleDynamic sale) {
        this.sale = sale;
    }

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

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
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
            case 4, 6, 7 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 1, 7 -> {
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
                    return sd.getBatchNo();
                }
                case 1 -> {
                    return sd.getTraderName();
                }
                case 2 -> {
                    //code
                    return sd.getUserCode() == null ? sd.getStockCode() : sd.getUserCode();
                }
                case 3 -> {
                    return sd.getStockName();
                }
                case 4 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 5 -> {
                    return sd.getUnitCode();
                }
                case 6 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 7 -> {
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
                    case 0 -> {
                        if (value instanceof GRN g) {
                            log.info(g.getBatchNo());
                            sd.setBatchNo(g.getBatchNo());
                            sd.setTraderName(g.getTraderName());
                            parent.setColumnSelectionInterval(1, 1);
                        }
                        //batch
                    }
                    case 2, 3 -> {
                        //Code
                        if (value instanceof Stock s) {
                            dialog.calStock(s, Global.parentForm);
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelName(s.getRelName());
                            sd.setQty(1.0);
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setStock(s);
                            sd.setPrice(Util1.getFloat(sd.getPrice()) == 0 ? s.getSalePriceN() : sd.getPrice());
                            parent.setColumnSelectionInterval(4, 4);
                            addNewRow();
                        }
                    }
                    case 4 -> {
                        //Qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(Util1.getDouble(value));
                            if (sd.getUnitCode() == null) {
                                parent.setColumnSelectionInterval(5, 5);
                            } else {
                                parent.setColumnSelectionInterval(6, 6);
                            }
                        }
                    }
                    case 5 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }

                    }

                    case 6 -> {
                        //price
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(Util1.getDouble(value));
                            parent.setColumnSelectionInterval(0, 0);
                            parent.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }

                }
                change = true;
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

    public void setListDetail(List<SaleHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    private void calculateAmount(SaleHisDetail sale) {
        if (sale.getStockCode() != null) {
            float saleQty = Util1.getFloat(sale.getQty());
            float stdSalePrice = Util1.getFloat(sale.getPrice());
            float amount = saleQty * stdSalePrice;
            sale.setAmount(Util1.getDouble(Math.round(amount)));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
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

    public void delete(int row) {
        listDetail.remove(row);
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

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

    public SaleHisDetail getObject(int row) {
        return listDetail.get(row);
    }
}
