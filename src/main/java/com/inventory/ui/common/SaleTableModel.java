/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.setup.dialog.PriceOptionDialog;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author wai yan
 */
@Component
public class SaleTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(SaleTableModel.class);
    private String[] columnNames = {"Code", "Description", "Location",
        "Qty", "Std-Wt", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<String> deleteList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;
    @Autowired
    private StockBalanceTableModel sbTableModel;
    @Autowired
    private InventoryRepo inventoryRepo;
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

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3,4,6,7 ->
                Float.class;
            default ->
                String.class;
        }; //Qty
        //Std-Wt
        /*case 6: //Unit
        return Object.class;*/
        //Sale Price
        //Amount
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 7;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return record.getStock() == null ? null : record.getStock().getUserCode();
                }
                case 1 -> {
                    //Name
                    return record.getStock() == null ? null : record.getStock().getStockName();
                }
                case 2 -> {
                    //loc
                    return record.getLocation();
                }
                case 3 -> {
                    //qty
                    return record.getQty();
                }
                case 4 -> {
                    //Std-Wt
                    return record.getSaleWt();
                }
                case 5 -> {
                    //unit
                    return record.getSaleUnit();
                }
                case 6 -> {
                    //price
                    return record.getPrice();
                }
                case 7 -> {
                    //amount
                    return record.getAmount();
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
            SaleHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock stock) {
                            sbTableModel.calStockBalance(stock.getStockCode());
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setSaleWt(Util1.gerFloatOne(stock.getSaleWeight()));
                            record.setSaleUnit(stock.getSaleUnit());
                            record.setPrice(stock.getSalePriceN());
                            record.setLocation(locationAutoCompleter.getLocation());
                            addNewRow();
                        }
                    }
                    parent.setColumnSelectionInterval(3, 3);
                }
                case 2 -> {
                    //Loc
                    if (value instanceof Location location) {
                        record.setLocation(location);
                    } else {
                        record.setLocation(null);
                    }
                }
                case 3 -> {
                    //Qty
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setQty(Util1.getFloat(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    if (ProUtil.isPriceOption()) {
                        PriceOptionDialog dialog = new PriceOptionDialog();
                        dialog.setInventoryRepo(inventoryRepo);
                        dialog.initData();
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                        String priceType = dialog.getOption().getPriceType();
                        Stock s = record.getStock();
                        switch (priceType) {
                            case "N" -> {
                                record.setPrice(s.getSalePriceN());
                            }
                            case "A" -> {
                                record.setPrice(s.getSalePriceA());
                            }
                            case "B" -> {
                                record.setPrice(s.getSalePriceB());
                            }
                            case "C" -> {
                                record.setPrice(s.getSalePriceC());
                            }
                            case "D" -> {
                                record.setPrice(s.getSalePriceD());
                            }
                            case "E" -> {
                                record.setPrice(s.getSalePriceE());
                            }
                        }
                    }
                    if (Util1.getFloat(record.getPrice()) > 0) {
                        parent.setRowSelectionInterval(row + 1, row + 1);
                        parent.setColumnSelectionInterval(0, 0);
                    } else {
                        parent.setRowSelectionInterval(row, row);
                        parent.setColumnSelectionInterval(6, 6);
                    }

                }
                case 4 -> {
                    //Std-Wt
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setSaleWt(Util1.getFloat(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be positive");
                        parent.setColumnSelectionInterval(column, column);
                    }
                }

                case 5 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit stockUnit) {
                            record.setSaleUnit(stockUnit);
                        }
                    }
                    parent.setColumnSelectionInterval(6, 6);
                }
                case 6 -> {
                    //Sale Price
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setPrice(Util1.getFloat(value));
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
                    //Amount
                    if (value != null) {
                        record.setAmount(Util1.getFloat(value));
                    }
                }
            }
            if (column != 6) {
                if (record.getStock() != null && record.getSaleUnit() != null) {
                    record.setPrice(inventoryRepo.getSaleRecentPrice(record.getStock().getStockCode(),
                            Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), record.getSaleUnit().getUnitCode()));
                }
            }
            calculateAmount(record);
            fireTableRowsUpdated(row, row);
            selectionObserver.selected("SALE-TOTAL", "SALE-TOTAL");
            parent.requestFocusInWindow();
            //   fireTableCellUpdated(row, 8);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleHisDetail pd = new SaleHisDetail();
                pd.setLocation(locationAutoCompleter.getLocation());
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            SaleHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStock() == null) {
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
        addNewRow();
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(SaleHisDetail sale) {
        if (sale.getStock() != null) {
            float saleQty = Util1.getFloat(sale.getQty());
            float stdSalePrice = Util1.getFloat(sale.getPrice());
            float amount = saleQty * stdSalePrice;
            sale.setAmount(Util1.getFloat(Math.round(amount)));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (SaleHisDetail sdh : listDetail) {
            if (sdh.getStock() != null) {
                if (Util1.getFloat(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getLocation() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                } else if (sdh.getSaleUnit() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Sale Unit.");
                    status = false;
                    parent.requestFocus();
                }
            }
        }
        return status;
    }

    public List<String> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        SaleHisDetail sdh = listDetail.get(row);
        if (sdh.getSdKey() != null) {
            deleteList.add(sdh.getSdKey().getSdCode());
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

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
        }
    }
}
