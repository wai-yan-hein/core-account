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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Athu Sint
 */
@Slf4j
public class MillingRawTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Stock Name", "Weight", "Qty", "Price", "Amount", "Total Weight"};
    @Setter
    private JTable parent;
    private List<MillingRawDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<MillingRawDetailKey> deleteList = new ArrayList();

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
        //"Code", "Stock Name", "Weight", "Qty", "Price", "Amount", "Total Weight"
        return switch (column) {
            case 0, 1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            MillingRawDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode();
                }
                case 1 -> {
                    return sd.getStockName();
                }
                case 2 -> {
                    return Util1.toNull(sd.getWeight());
                }
                case 3 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 4-> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 5 -> {
                    //amount
                    return Util1.toNull(sd.getAmount());
                }
                case 6 -> {
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
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setQty(1.0);
                            sd.setWeight(s.getWeight());
                            sd.setWeightUnit(Util1.isNull(s.getWeightUnit(), "-"));
                            sd.setUnitCode("-");
                            sd.setStock(s);
                            setSelection(row, 2);
                            calWeight(sd);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        double weight = Util1.getDouble(value);
                        if (weight > 0) {
                            sd.setWeight(weight);
                            calWeight(sd);
                            calculateAmount(sd);
                        }
                    }
                    case 3 -> {
                        //Qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(qty);
                            calWeight(sd);
                            calculateAmount(sd);
                            setSelection(row, column + 1);
                        }
                    }
                    case 4 -> {
                        //price
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(price);
                            calculateAmount(sd);
                            setSelection(row + 1, 0);
                        }
                    }
                    case 5 -> {
                        //amt
                        double amt = Util1.getDouble(value);
                        if (amt > 0) {
                            sd.setAmount(amt);
                            calPrice(sd);
                        }
                    }

                }
                fireTableRowsUpdated(row, row);
                observer.selected("SALE-TOTAL", "SALE-TOTAL");
                parent.requestFocus();
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
            sd.setTotalWeight(sd.getQty() * sd.getWeight());
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
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
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
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
    }

    private void calculateAmount(MillingRawDetail s) {
        double price = s.getPrice();
        double wt = s.getWeight();
        double ttlWt = s.getTotalWeight();
        if (s.getStockCode() != null) {
            double amount = (ttlWt * price) / wt;
            s.setAmount(amount);
        }
    }

    private void calPrice(MillingRawDetail s) {
        double wt = s.getWeight();
        double ttlWt = s.getTotalWeight();
        double amount = s.getAmount();
        if (s.getStockCode() != null) {
            double price = amount * wt / ttlWt;
            s.setPrice(price);
        }
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

    public MillingRawDetail getData(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

}
