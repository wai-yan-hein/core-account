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
import com.inventory.entity.LandingHisQty;
import com.inventory.entity.Location;
import com.inventory.entity.PurDetailKey;
import com.inventory.entity.PurHisDetail;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.PurchaseDynamic;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class PurchaseRiceTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Location", "Weight", "Weight Unit", "Qty", "Unit", "Total Weight", "Price", "Amount"};
    private JTable parent;
    private List<PurHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<PurDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;
    private PurchaseDynamic purchase;
    private JProgressBar progress;

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public PurchaseDynamic getPurchase() {
        return purchase;
    }

    public void setPurchase(PurchaseDynamic purchase) {
        this.purchase = purchase;
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
            case 0, 1, 2, 4, 6 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 11 ->
                false;
            default ->
                true;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                PurHisDetail record = listDetail.get(row);
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
                            if (ProUtil.isStockNameWithCategory()) {
                                if (record.getCatName() != null) {
                                    stockName = String.format("%s (%s)", stockName, record.getCatName());
                                }
                            }
                        }
                        return stockName;
                    }
                    case 2 -> {
                        //loc
                        return record.getLocName();
                    }
                    case 3 -> {
                        //weight
                        return Util1.toNull(record.getWeight());
                    }
                    case 4 -> {
                        //unit
                        return record.getWeightUnit();
                    }
                    case 5 -> {
                        return Util1.toNull(record.getQty());
                    }
                    case 6 -> {
                        return record.getUnitCode();
                    }
                    //total
                    case 7 -> {
                        return Util1.toNull(record.getTotalWeight());
                    }
                    case 8 -> {
                        return Util1.toNull(record.getPrice());
                    }
                    case 9 -> {
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
            PurHisDetail record = listDetail.get(row);
            switch (column) {
                case 0, 1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock s) {
                            record.setStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                            record.setUserCode(s.getUserCode());
                            record.setRelName(s.getRelName());
                            record.setQty(0);
                            record.setUnitCode(s.getPurUnitCode());
                            record.setWeightUnit(s.getWeightUnit());
                            record.setWeight(Util1.getDouble(s.getWeight()));
                            record.setStdWeight(Util1.getDouble(s.getWeight()));
                            record.setPurQty(s.getPurQty());
                            double price = record.getPrice();
                            if (price == 0) {
                                record.setPrice(s.getPurPrice());
                            }
                            addNewRow();
                            setSelection(row, 5);
                        }
                    }
                }
                case 2 -> {
                    //Loc
                    if (value instanceof Location l) {
                        record.setLocCode(l.getKey().getLocCode());
                        record.setLocName(l.getLocName());
                    }
                }
                case 3 -> {
                    //weight
                    double wt = Util1.getDouble(value);
                    if (wt > 0) {
                        record.setWeight(Util1.getDouble(value));
                    }
                }
                case 4 -> {
                    //weight unit
                    if (value instanceof StockUnit u) {
                        record.setWeightUnit(u.getKey().getUnitCode());
                    }
                }
                case 5 -> {
                    //Qty
                    double qty = Util1.getDouble(value);
                    if (qty > 0) {
                        record.setQty(Util1.getDouble(value));
                        if (record.getWeight() > 0) {
                            record.setTotalWeight(Util1.getDouble(record.getQty()) * Util1.getDouble(record.getWeight()));
                        }
                    }
                }
                case 6 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit u) {
                            record.setUnitCode(u.getKey().getUnitCode());
                        }
                    }
                    parent.setColumnSelectionInterval(8, 8);
                }
                case 7 -> {
                    double ttlWt = Util1.getDouble(value);
                    if (ttlWt > 0) {
                        double weight = Util1.getDouble(record.getWeight());
                        double qty = ttlWt / weight;
                        record.setQty(Util1.roundUp(qty));
                        record.setTotalWeight(ttlWt);
                        calWeightLoss(record);
                    }
                }
                case 8 -> {
                    //Pur Price
                    double price = Util1.getDouble(value);
                    if (price > 0) {
                        record.setPrice(Util1.getDouble(value));
                        record.setOrgPrice(record.getPrice());
                        parent.setColumnSelectionInterval(0, 0);
                        parent.setRowSelectionInterval(row + 1, row + 1);
                    }
                }
                case 9 -> {
                    //Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                    }
                }
            }
            assignLocation(record);
            calculateAmount(record);
            setRecord(listDetail.size() - 1);
            fireTableRowsUpdated(row, row);
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            parent.requestFocusInWindow();
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void calWeightLoss(PurHisDetail pd) {
        String vouNo = pd.getLandVouNo();
        if (!Util1.isNullOrEmpty(vouNo)) {
            progress.setIndeterminate(true);
            double purQty = pd.getQty();
            double stockQty = pd.getPurQty();
            if (stockQty > 0) {
                inventoryRepo.getLandingHisQty(vouNo).doOnSuccess((t) -> {
                    if (t != null) {
                        if (!t.isEmpty()) {
                            LandingHisQty obj = t.get(0);
                            double percent = obj.getPercent();
                            double percentAllow = obj.getPercentAllow();
                            if (percent > percentAllow) {
                                double qty = obj.getQty();
                                double diff = percent - percentAllow;
                                double lossQty = diff * qty * purQty / stockQty;
                                PurHisDetail loss = new PurHisDetail();
                                loss.setStockCode(pd.getStockCode());
                                loss.setUserCode(pd.getUserCode());
                                loss.setStockName(pd.getStockName());
                                loss.setLocCode(pd.getLocCode());
                                loss.setLocName(pd.getLocName());
                                loss.setWeight(pd.getWeight());
                                loss.setWeightUnit(pd.getWeightUnit());
                                double roundQty = Util1.roundUp(lossQty);
                                loss.setQty(roundQty);
                                loss.setUnitCode(pd.getUnitCode());
                                loss.setPrice(pd.getPrice());
                                loss.setTotalWeight(roundQty * loss.getWeight());
                                double amt = pd.getPrice() * roundQty;
                                loss.setAmount(Util1.round(amt));
                                listDetail.set(1, loss);
                                fireTableRowsUpdated(1, 1);
                                addNewRow();
                                observer.selected("CAL-TOTAL", "CAL-TOTAL");
                            }
                        }
                        progress.setIndeterminate(false);
                    }
                }).doOnError((e) -> {
                    progress.setIndeterminate(false);
                }).subscribe();
            } else {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(parent, pd.getStockName() + "need to setup purchase qty in Stock Setup.");
            }
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void assignLocation(PurHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = purchase.getLocationAutoCompleter();
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
        lblRec.setText("Records : " + size);
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                PurHisDetail pd = new PurHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            PurHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                return true;
            }
        }
        return false;
    }

    public List<PurHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PurHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void calculateAmount(PurHisDetail pur) {
        double price = Util1.getDouble(pur.getPrice());
        double qty = Util1.getDouble(pur.getQty());
        if (pur.getStockCode() != null) {
            double amount = qty * price;
            pur.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (int i = 0; i < listDetail.size(); i++) {
            PurHisDetail sdh = listDetail.get(i);
            if (sdh.getStockCode() != null) {
                if (Util1.getDouble(sdh.getAmount()) == 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    focusTable(i);
                    return false;
                } else if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    focusTable(i);
                    return false;
                } else if (sdh.getWeight() == 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Weight");
                    focusTable(i);
                    return false;
                } else if (sdh.getWeightUnit() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Weight Unit");
                    focusTable(i);
                    return false;
                }
            }
        }
        return status;
    }

    private void focusTable(int row) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(0, 0);
        parent.requestFocus();
    }

    public List<PurDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        PurHisDetail sdh = listDetail.get(row);
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

    public void addPurchase(PurHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public PurHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }
}
