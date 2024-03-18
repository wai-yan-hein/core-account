/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.MillingRawDetail;
import com.inventory.entity.MillingRawDetailKey;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
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
 * @author Athu Sint
 */
@Slf4j
public class MillingRawTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Stock Name", "Weight", "Weight Unit", "Qty", "Unit", "Price", "Amount", "Total Weight"};
    private JTable parent;
    private List<MillingRawDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<MillingRawDetailKey> deleteList = new ArrayList();
    private JLabel lblStockName;
    private JButton btnProgress;
    private JDateChooser vouDate;

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
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1, 3, 5 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 7, 8 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            MillingRawDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode() == null ? sd.getStockCode() : sd.getUserCode();
                }
                case 1 -> {
                    String stockName = null;
                    if (sd.getStockCode() != null) {
                        stockName = sd.getStockName();
                        if (ProUtil.isStockNameWithCategory()) {
                            if (sd.getCatName() != null) {
                                stockName = String.format("%s (%s)", stockName, sd.getCatName());
                            }
                        }
                    }
                    return stockName;
                }
                case 2 -> {
                    return Util1.toNull(sd.getWeight());
                }
                case 3 -> {
                    return sd.getWeightUnit();
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
                case 8 -> {
                    return Util1.toNull(sd.getTotalWeight());
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
            MillingRawDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            //stockBalanceTableModel.calStockBalance(s.getKey().getStockCode());
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelName(s.getRelName());
                            sd.setQty(1.0);
                            sd.setWeight(s.getWeight());
                            sd.setWeightUnit(Util1.isNull(s.getWeightUnit(), "-"));
                            sd.setUnitCode("-");
                            sd.setStock(s);
                            sd.setPrice(Util1.getDouble(sd.getPrice()) == 0 ? s.getSalePriceN() : sd.getPrice());
                            setSelection(row, 3);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        sd.setWeight(Util1.getDouble(value));
                    }
                    case 3 -> {
                        if (value instanceof StockUnit u) {
                            sd.setWeightUnit(u.getKey().getUnitCode());
                        }
                    }
                    case 4 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setQty(Util1.getDouble(value));
                                sd.setQtyStr(value.toString());
                                if (sd.getUnitCode() == null) {
                                    setSelection(row, 5);
                                } else {
                                    setSelection(row, 6);
                                }
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            setSelection(row, column);
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
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setPrice(Util1.getDouble(value));
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
                    case 7 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));

                    }

                }
                calculateAmount(sd);
                calWeight(sd);
                fireTableRowsUpdated(row, row);
                observer.selected("SALE-TOTAL", "SALE-TOTAL");
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void calWeight(MillingRawDetail sd) {
        if (ProUtil.isUseWeightPoint()) {
            String str = sd.getQtyStr();
            double wt = Util1.getDouble(sd.getWeight());
            sd.setQty(Util1.getDouble(str));
            sd.setTotalWeight(Util1.getTotalWeight(wt, str));
        } else {
            sd.setTotalWeight(Util1.getDouble(sd.getQty()) * Util1.getDouble(sd.getWeight()));
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                MillingRawDetail pd = new MillingRawDetail();
                listDetail.add(pd);
//                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            MillingRawDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<MillingRawDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MillingRawDetail> listDetail) {
        this.listDetail = listDetail;
        addNewRow();
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(MillingRawDetail s) {
        double price = Util1.getDouble(s.getPrice());
        double wt = Util1.getDouble(s.getWeight());
        double ttlWt = Util1.getDouble(s.getTotalWeight());
        if (s.getStockCode() != null) {
            double amount = (ttlWt * price) / wt;
            s.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (MillingRawDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<MillingRawDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        MillingRawDetail sdh = listDetail.get(row);
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

    public void addData(MillingRawDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }
    public MillingRawDetail getData(int row){
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

}
