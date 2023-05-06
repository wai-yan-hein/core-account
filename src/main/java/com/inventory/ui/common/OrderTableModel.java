/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.OrderDetailKey;
import com.inventory.model.OrderHisDetail;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.ui.entry.OrderEntry;
import com.inventory.ui.entry.Sale;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DELL
 */
public class OrderTableModel extends AbstractTableModel {
    private static final Logger log = LoggerFactory.getLogger(SaleTableModel.class);
    private String[] columnNames = {"Code", "Description", "Relation", "Location", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<OrderHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<OrderDetailKey> deleteList = new ArrayList();
    private StockBalanceTableModel sbTableModel;
    private OrderEntry orderEntry;
    private InventoryRepo inventoryRepo;
    private JLabel lblStockName;
    private JButton btnProgress;
    private JDateChooser vouDate;
    private boolean change = false;
    private JLabel lblRecord;

    public OrderEntry getOrderEntry() {
        return orderEntry;
    }

    public void setOrderEntry(OrderEntry orderEntry) {
        this.orderEntry = orderEntry;
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

    public void setSelectionObserver(SelectionObserver selectionObserver) {
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
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 6 -> {
                return ProUtil.isSalePriceChange();
            }
            case 2, 7 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OrderHisDetail sd = listDetail.get(row);
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
                    return sd.getRelName();
                }
                case 3 -> {
                    //loc
                    return sd.getLocName();
                }
                case 4 -> {
                    //qty
                    return sd.getQty();
                }
                case 5 -> {
                    return sd.getUnitCode();
                }
                case 6 -> {
                    //price
                    return sd.getPrice();
                }
                case 7 -> {
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
            OrderHisDetail sd = listDetail.get(row);
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
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setPrice(getTraderPrice(s));
                            sd.setStock(s);
                            sd.setPrice(sd.getPrice() == 0 ? s.getSalePriceN() : sd.getPrice());
                            parent.setColumnSelectionInterval(4, 4);
                            addNewRow();
                        }
                    }
                    case 3 -> {
                        //Loc
                        if (value instanceof Location l) {
                            sd.setLocCode(l.getKey().getLocCode());
                            sd.setLocName(l.getLocName());

                        }
                    }
                    case 4 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                sd.setQty(Util1.getFloat(value));
                                if (sd.getUnitCode() == null) {
                                    parent.setColumnSelectionInterval(5, 5);
                                } else {
                                    parent.setColumnSelectionInterval(6, 6);
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
                    case 5 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }

                    }

                    case 6 -> {
                        //price
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                sd.setPrice(Util1.getFloat(value));
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
                    case 7 -> {
                        //amt
                        sd.setAmount(Util1.getFloat(value));

                    }

                }
                if (column != 6) {
                    if (Util1.getFloat(sd.getPrice()) == 0) {
                        if (ProUtil.isSaleLastPrice()) {
                            if (sd.getStockCode() != null && sd.getUnitCode() != null) {
                                inventoryRepo.getSaleRecentPrice(sd.getStockCode(),
                                        Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), sd.getUnitCode()).subscribe((t) -> {
                                    sd.setPrice(t.getAmount());
                                });
                            }
                        }
                    }
                }
                change = true;
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                selectionObserver.selected("ORDER-TOTAL", "ORDER-TOTAL");
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }
    private void assignLocation(OrderHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = orderEntry.getLocationAutoCompleter();
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
                OrderHisDetail pd = new OrderHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            OrderHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<OrderHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<OrderHisDetail> listDetail) {
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

    private void calculateAmount(OrderHisDetail orderEntry) {
        if (orderEntry.getStockCode() != null) {
            float saleQty = Util1.getFloat(orderEntry.getQty());
            float stdSalePrice = Util1.getFloat(orderEntry.getPrice());
            float amount = saleQty * stdSalePrice;
            orderEntry.setAmount(Util1.getFloat(Math.round(amount)));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (OrderHisDetail sdh : listDetail) {
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

    private float getTraderPrice(Stock s) {
        float price = 0.0f;
        String priceType = getTraderType();
        switch (priceType) {
            case "N" -> {
                price = s.getSalePriceN();
            }
            case "A" -> {
                price = s.getSalePriceA();
            }
            case "B" -> {
                price = s.getSalePriceB();
            }
            case "C" -> {
                price = s.getSalePriceC();
            }
            case "D" -> {
                price = s.getSalePriceD();
            }
            case "E" -> {
                price = s.getSalePriceE();
            }
        }
        return price;
    }

    public List<OrderDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        OrderHisDetail sdh = listDetail.get(row);
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

    public void addSale(OrderHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public OrderHisDetail getOrderEntry(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
        }
    }

    private String getTraderType() {
        TraderAutoCompleter completer = orderEntry.getTraderAutoCompleter();
        if (completer != null) {
            Trader t = completer.getTrader();
            return t == null ? "N" : Util1.isNull(t.getPriceType(), "N");
        }
        return "N";
    }
    
}
