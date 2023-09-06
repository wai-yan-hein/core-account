/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.MillingOutDetail;
import com.inventory.model.MillingOutDetailKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.repo.InventoryRepo;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Athu Sint
 */
public class MilingOutTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(MilingOutTableModel.class);
    private String[] columnNames = {"Code", "Stock Name", "Location", "Weight", "Weight Unit", "Qty", "Unit", "Total Weight", "%", "Price", "Amount"};
    private JTable parent;
    private List<MillingOutDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<MillingOutDetailKey> deleteList = new ArrayList();
    private StockBalanceTableModel sbTableModel;
    private InventoryRepo inventoryRepo;
    private JLabel lblStockName;
    private JButton btnProgress;
    private JDateChooser vouDate;
    private boolean change = false;
    private JFormattedTextField totalWeight;
    private Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public JFormattedTextField getModel() {
        return totalWeight;
    }

    public void setModel(JFormattedTextField totalWeight) {
        this.totalWeight = totalWeight;
    }

    public StockBalanceTableModel getSbTableModel() {
        return sbTableModel;
    }

    public void setSbTableModel(StockBalanceTableModel sbTableModel) {
        this.sbTableModel = sbTableModel;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
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
            case 0, 1, 2 ->
                String.class;
            default ->
                Float.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 7, 8, 10 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            MillingOutDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode() == null ? sd.getStockCode() : sd.getUserCode();
                }
                case 1 -> {
                    return sd.getStockName();
                }
                case 2 -> {
                    //loc
                    return sd.getLocName();
                }
                case 3 -> {
                    return sd.getWeight();
                }
                case 4 -> {
                    return sd.getWeightUnit();
                }
                case 5 -> {
                    //qty
                    return sd.getQty();
                }
                case 6 -> {
                    return sd.getUnitCode();
                }
                case 7 -> {
                    return sd.getTotalWeight();
                }
                case 8 -> {
                    return sd.getPercent();
                }
                case 9 -> {
                    //price
                    return sd.getPrice();
                }
                case 10 -> {
                    //amount
                    return sd.getAmount();
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
            MillingOutDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            sbTableModel.calStockBalance(s.getKey().getStockCode());
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelName(s.getRelName());
                            sd.setQty(1.0f);
                            sd.setWeight(s.getWeight());
                            sd.setWeightUnit(s.getWeightUnit());
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setStock(s);
                            sd.setPrice(Util1.getFloat(sd.getPrice()) == 0 ? s.getSalePriceN() : sd.getPrice());
                            setSelection(row, 3);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        //Loc
                        if (value instanceof Location l) {
                            sd.setLocCode(l.getKey().getLocCode());
                            sd.setLocName(l.getLocName());

                        }
                    }
                    case 3 -> {
                        sd.setWeight(Util1.getFloat(value));
                    }
                    case 4 -> {
                        if (value instanceof StockUnit u) {
                            sd.setWeightUnit(u.getKey().getUnitCode());
                        }
                    }
                    case 5 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                if (ProUtil.isUseWeightPoint()) {
                                    String str = String.valueOf(value);
                                    float wt = Util1.getFloat(sd.getWeight());
                                    sd.setQty(Util1.getFloat(value));
                                    sd.setTotalWeight(Util1.getTotalWeight(wt, str));
                                } else {
                                    float wt = Util1.getFloat(sd.getWeight());
                                    sd.setQty(Util1.getFloat(value));
                                    sd.setTotalWeight(Util1.getFloat(sd.getQty()) * Util1.getFloat(sd.getWeight()));
                                }

                                if (sd.getUnitCode() == null) {
                                    setSelection(row, 6);
                                } else {
                                    setSelection(row, 9);
                                }
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                    case 6 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }
                    }
                    case 8 -> {
                        sd.setPercent(Util1.getFloat(value));
                    }

                    case 9 -> {
                        //price
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                sd.setPrice(Util1.getFloat(value));
                                setSelection(row + 1, 0);
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            setSelection(row, column);
                        }
                    }
                    case 10 -> {
                        //amt
                        sd.setAmount(Util1.getFloat(value));
                    }

                }
                change = true;
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                if (row != 0) {
                    selectionObserver.selected("SALE-TOTAL-OUT", "SALE-TOTAL-OUT");
                } else {
                    selectionObserver.selected("SALE-TOTAL-SKIP", "SALE-TOTAL-SKIP");
                }

                parent.requestFocus();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void assignLocation(MillingOutDetail sd) {
        if (sd.getLocCode() == null) {
            if (location != null) {
                sd.setLocCode(location.getKey().getLocCode());
                sd.setLocName(location.getLocName());
            }
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                MillingOutDetail pd = new MillingOutDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            MillingOutDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<MillingOutDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MillingOutDetail> listDetail) {
        this.listDetail = listDetail;
        addNewRow();
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(MillingOutDetail s) {
        float price = Util1.getFloat(s.getPrice());
        float wt = Util1.getFloat(s.getWeight());
        float ttlWt = Util1.getFloat(s.getTotalWeight());
        if (s.getStockCode() != null) {
            float amount = (ttlWt * price) / wt;
            s.setAmount(amount);
            s.setPercent((ttlWt / Util1.getFloat(totalWeight.getValue())) * 100);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (MillingOutDetail sdh : listDetail) {
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

    public List<MillingOutDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        MillingOutDetail sdh = listDetail.get(row);
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

    public void addObject(MillingOutDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void setObject(int row, MillingOutDetail mod) {
        if (!listDetail.isEmpty()) {
            listDetail.set(row, mod);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
        }
    }

}
