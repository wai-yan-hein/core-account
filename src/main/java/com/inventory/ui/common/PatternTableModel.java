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
import com.inventory.entity.Location;
import com.inventory.entity.MessageType;
import com.inventory.entity.Pattern;
import com.inventory.entity.PatternKey;
import com.inventory.entity.PriceOption;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PatternTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Stock Name", "Location", "Qty", "Unit", "Price", "Amount", "Price Method"};
    private List<Pattern> listPattern = new ArrayList<>();
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private String stockCode;
    @Setter
    private JPanel panel;
    @Setter
    private JTable table;
    @Setter
    private Location location;
    @Setter
    private SelectionObserver observer;

    @Override
    public int getRowCount() {
        return listPattern.size();
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
        Pattern p = listPattern.get(rowIndex);
        switch (columnIndex) {
            case 0 -> {
                return Util1.isNull(p.getUserCode(), p.getKey().getStockCode());
            }
            case 1 -> {
                String stockName = null;
                if (p.getKey() != null) {
                    stockName = p.getStockName();
                    if (ProUtil.isStockNameWithCategory()) {
                        if (p.getCatName() != null) {
                            stockName = String.format("%s (%s)", stockName, p.getCatName());
                        }
                    }
                }
                return stockName;
            }
            case 2 -> {
                return p.getLocName();
            }
            case 3 -> {
                return Util1.toNull(p.getQty());
            }
            case 4 -> {
                return p.getUnitCode();
            }
            case 5 -> {
                return Util1.toNull(p.getPrice());
            }
            case 6 -> {
                return Util1.toNull(p.getAmount());
            }
            case 7 -> {
                return p.getPriceTypeName();
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                Pattern p = listPattern.get(row);
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            p.getKey().setStockCode(s.getKey().getStockCode());
                            p.setStockName(s.getStockName());
                            p.setUserCode(s.getUserCode());
                            p.setRelation(s.getRelName());
                            p.setUnitCode(s.getPurUnitCode());
                            if (location != null) {
                                p.setLocCode(location.getKey().getLocCode());
                                p.setLocName(location.getLocName());
                                table.setColumnSelectionInterval(3, 3);
                            } else {
                                table.setColumnSelectionInterval(2, 2);
                            }
                        }
                    }
                    case 2 -> {
                        if (value instanceof Location loc) {
                            p.setLocCode(loc.getKey().getLocCode());
                            p.setLocName(loc.getLocName());
                            table.setColumnSelectionInterval(3, 3);
                        }
                    }
                    case 3 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setQty(Util1.getDouble(value));
                            if (p.getUnitCode() == null) {
                                table.setColumnSelectionInterval(4, 4);
                            }
                        } else {
                            JOptionPane.showMessageDialog(panel, String.format("Invalid %s", value));
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            p.setUnitCode(unit.getKey().getUnitCode());
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }
                    case 5 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setPrice(Util1.getDouble(value));
                        } else {
                            JOptionPane.showMessageDialog(panel, String.format("Invalid %s", value));
                        }
                    }
                    case 7 -> {
                        if (value instanceof PriceOption op) {
                            p.setPriceTypeCode(op.getKey().getPriceType());
                            p.setPriceTypeName(op.getDescription());
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }
                }
                p.getKey().setMapStockCode(stockCode);
                if (p.getKey().getUniqueId() == 0) {
                    p.getKey().setUniqueId(row + 1);
                }
                calAmt(p);
                save(p, row);
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private void calAmt(Pattern p) {
        Double qty = Util1.getDouble(p.getQty());
        Double price = Util1.getDouble(p.getPrice());
        p.setAmount(qty * price);
    }

    private boolean isValidEntry(Pattern pd) {
        boolean status = true;
        if (Util1.isNull(pd.getKey().getStockCode())) {
            status = false;
            JOptionPane.showMessageDialog(panel, "Select Pattern");
        } else if (Objects.isNull(pd.getKey().getStockCode())) {
            status = false;
        } else if (Objects.isNull(pd.getLocCode())) {
            status = false;
        } else if (Util1.getFloat(pd.getQty()) <= 0) {
            status = false;
        } else if (Objects.isNull(pd.getUnitCode())) {
            status = false;
        }
        return status;
    }

    private void save(Pattern p, int row) {
        if (isValidEntry(p)) {
            p.setDeptId(Global.deptId);
            inventoryRepo.savePattern(p).subscribe((t) -> {
                addNewRow();
                table.setRowSelectionInterval(row + 1, row + 1);
                table.setColumnSelectionInterval(0, 0);
                observer.selected("CAL_PRICE", "-");
                sendMessage(t.getStockName());
            });

        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.PATTERN, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 3, 5, 6 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 6;
    }

    public List<Pattern> getListPattern() {
        return listPattern;
    }

    public void setListPattern(List<Pattern> listPattern) {
        this.listPattern = listPattern;
        fireTableDataChanged();
    }

    public Pattern getPattern(int row) {
        return listPattern.get(row);
    }

    public void setPattern(Pattern report, int row) {
        if (!listPattern.isEmpty()) {
            listPattern.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addPattern(Pattern item) {
        if (!listPattern.isEmpty()) {
            listPattern.add(item);
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    private Pattern aboveObject() {
        if (!listPattern.isEmpty()) {
            return listPattern.get(listPattern.size() - 1);
        }
        return null;
    }

    public void addNewRow() {
        if (!hasEmptyRow()) {
            Pattern p = new Pattern();
            PatternKey key = new PatternKey();
            key.setCompCode(Global.compCode);
            p.setKey(key);
            Pattern obj = aboveObject();
            if (obj != null) {
                p.setLocCode(obj.getLocCode());
                p.setLocName(obj.getLocName());
            }
            listPattern.add(p);
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        if (listPattern.isEmpty()) {
            return false;
        }
        Pattern p = listPattern.get(listPattern.size() - 1);
        return p.getKey().getStockCode() == null;
    }

    public void addRow() {
        Pattern p = new Pattern();
        listPattern.add(p);
        fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
    }

    public void remove(int row) {
        listPattern.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
