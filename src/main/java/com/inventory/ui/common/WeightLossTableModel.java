/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.common.Global;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.model.WeightLossDetail;
import com.inventory.model.WeightLossDetailKey;
import com.toedter.calendar.JDateChooser;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class WeightLossTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Stock Code", "Stock Name", "Relation", "Location", "Actual Qty", "Actual Unit", "Actual Price", "Weight Loss Qty", "Weight Loss Unit", "Weight Loss Price"};
    private List<WeightLossDetail> listDetail = new ArrayList<>();
    private JLabel lblRecord;
    private JPanel panel;
    private JTable table;
    private Location location;
    private List<WeightLossDetailKey> delKeys = new ArrayList<>();
    private JDateChooser vouDate;
    private InventoryRepo inventoryRepo;

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JDateChooser getVouDate() {
        return vouDate;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public JLabel getLblRecord() {
        return lblRecord;
    }

    public void setLblRecord(JLabel lblRecord) {
        this.lblRecord = lblRecord;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public List<WeightLossDetailKey> getDelKeys() {
        return delKeys;
    }

    public void setDelKeys(List<WeightLossDetailKey> delKeys) {
        this.delKeys = delKeys;
    }

    @Override
    public int getRowCount() {
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WeightLossDetail p = listDetail.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                p.getStockUserCode() == null ? p.getStockCode() : p.getStockUserCode();
            case 1 ->
                p.getStockName();
            case 2 ->
                p.getRelName();
            case 3 ->
                p.getLocName();
            case 4 ->
                p.getQty();
            case 5 ->
                p.getUnit();
            case 6 ->
                p.getPrice();
            case 7 ->
                p.getLossQty();
            case 8 ->
                p.getLossUnit();
            case 9 ->
                p.getLossPrice();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                WeightLossDetail p = listDetail.get(row);
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            p.setStockCode(s.getKey().getStockCode());
                            p.setStockUserCode(s.getUserCode());
                            p.setStockName(s.getStockName());
                            p.setRelName(s.getRelName());
                            p.setUnit(s.getPurUnitCode());
                            p.setLossUnit(s.getPurUnitCode());
                            if (p.getLocCode() != null) {
                                table.setColumnSelectionInterval(4, 4);
                            } else {
                                table.setColumnSelectionInterval(3, 3);
                            }
                            table.setRowSelectionInterval(row, row);
                        }
                    }
                    case 3 -> {
                        if (value instanceof Location l) {
                            p.setLocCode(l.getKey().getLocCode());
                            p.setLocName(l.getLocName());
                            table.setColumnSelectionInterval(4, 4);
                            table.setRowSelectionInterval(row, row);
                        }
                    }
                    case 4 -> {
                        if (Util1.getFloat(value) > 0) {
                            p.setQty(Util1.getFloat(value));
                            if (Util1.getFloat(p.getLossQty()) == 0) {
                                p.setLossQty(p.getQty());
                            }
                        }
                    }
                    case 5 -> {
                        if (value instanceof StockUnit unit) {
                            p.setUnit(unit.getKey().getUnitCode());
                        }
                    }
                    case 6 -> {
                        if (Util1.getFloat(value) > 0) {
                            p.setPrice(Util1.getFloat(value));
                        }
                    }
                    case 7 -> {
                        if (Util1.getFloat(value) > 0) {
                            p.setLossQty(Util1.getFloat(value));
                        }
                    }
                    case 8 -> {
                        if (value instanceof StockUnit unit) {
                            p.setLossUnit(unit.getKey().getUnitCode());
                        }
                    }
                    case 9 -> {
                        if (Util1.getFloat(value) > 0) {
                            p.setLossPrice(Util1.getFloat(value));
                        }
                    }
                }
                if (column != 6) {
                    if (Util1.getFloat(p.getPrice()) == 0) {
                        if (p.getStockCode() != null && p.getUnit() != null) {
                            p.setPrice(inventoryRepo.getPurRecentPrice(p.getStockCode(), Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), p.getUnit()).block().getAmount());
                        }
                    }
                }
                lblRecord.setText("Records : " + String.valueOf(listDetail.size() - 1));
                calPrice(p);
                addNewRow();
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private void calPrice(WeightLossDetail pd) {
        String unit = pd.getUnit();
        String lossUnit = pd.getLossUnit();
        String stockCode = pd.getStockCode();
        float lossQty = Util1.getFloat(pd.getLossQty());
        float qty = Util1.getFloat(pd.getQty());
        if (unit != null && stockCode != null && lossUnit != null && qty > 0 && lossQty > 0) {
            float tmp1 = inventoryRepo.getSmallQty(stockCode, unit).block().getSmallQty() * qty;
            float tmp2 = inventoryRepo.getSmallQty(stockCode, lossUnit).block().getSmallQty() * lossQty;
            float price = Util1.getFloat(pd.getPrice());
            float lossPrice = (tmp1 / tmp2) * price;
            pd.setLossPrice(lossPrice);
        }
    }

    public boolean isValidEntry() {
        for (WeightLossDetail d : listDetail) {
            if (d.getStockCode() != null) {
                if (d.getLocCode() == null) {
                    JOptionPane.showMessageDialog(table, "Invalid Location.");
                    return false;
                } else if (Util1.getFloat(d.getQty()) <= 0) {
                    JOptionPane.showMessageDialog(table, "Invalid Qty.");
                    return false;
                } else if (Util1.getFloat(d.getPrice()) <= 0) {
                    JOptionPane.showMessageDialog(table, "Invalid Price.");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 4, 6, 7, 9 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex
    ) {
        return true;
    }

    public List<WeightLossDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<WeightLossDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public WeightLossDetail getDetail(int row) {
        return listDetail.get(row);
    }

    public void setDetail(WeightLossDetail report, int row) {
        if (!listDetail.isEmpty()) {
            listDetail.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addDetail(WeightLossDetail item) {
        if (!listDetail.isEmpty()) {
            listDetail.add(item);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    private WeightLossDetail aboveObject() {
        if (!listDetail.isEmpty()) {
            return listDetail.get(listDetail.size() - 1);
        }
        return null;
    }

    public void addNewRow() {
        if (!hasEmptyRow()) {
            WeightLossDetail p = new WeightLossDetail();
            WeightLossDetailKey key = new WeightLossDetailKey();
            key.setCompCode(Global.compCode);
            p.setKey(key);
            p.setDeptId(Global.deptId);
            WeightLossDetail obj = aboveObject();
            if (obj != null) {
                p.setLocCode(obj.getLocCode());
                p.setLocName(obj.getLocName());
            } else {
                if (location != null) {
                    p.setLocCode(location.getKey().getLocCode());
                    p.setLocName(location.getLocName());
                }
            }
            listDetail.add(p);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.isEmpty()) {
            return false;
        }
        WeightLossDetail p = listDetail.get(listDetail.size() - 1);
        return p.getStockCode() == null;
    }

    public void addRow() {
        WeightLossDetail p = new WeightLossDetail();
        listDetail.add(p);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    public void remove(int row) {
        WeightLossDetail sdh = listDetail.get(row);
        if (sdh.getKey() != null) {
            delKeys.add(sdh.getKey());
        }
        listDetail.remove(row);
        addNewRow();
        if (row - 1 >= 0) {
            table.setRowSelectionInterval(row - 1, row - 1);
        } else {
            table.setRowSelectionInterval(0, 0);
        }
        fireTableRowsDeleted(row, row);
        table.requestFocus();
    }

    public void clear() {
        listDetail.clear();
        delKeys.clear();
        fireTableDataChanged();
    }
}
