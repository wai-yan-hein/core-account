/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.common.Global;
import com.inventory.common.SelectionObserver;
import com.inventory.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockBalanceTmp;
import com.inventory.model.StockUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleEntryTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(SaleEntryTableModel.class);
    private String[] columnNames = {"Code", "Description", "Location",
        "Qty", "Std-Wt", "Unit", "Sale Price", "Amount"};
    private final ImageIcon progressIcon = new ImageIcon(this.getClass().getResource("/images/progress_indicator_16px.png"));
    private final ImageIcon refreshIcon = new ImageIcon(this.getClass().getResource("/images/synchronize_16px.png"));
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private JTextField txtTotalItem;
    private final List<String> deleteList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;
    private HashMap<String, List<StockBalanceTmp>> hmBalance = new HashMap<>();

    @Autowired
    private StockBalanceTableModel balanceTableModel;
    @Autowired
    private TaskExecutor taskExecutor;
    private JLabel lblStockName;
    private JButton btnProgress;

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
                    if (record.getStock() == null) {
                        return null;
                    } else {
                        if (Util1.isNull(Global.sysProperties.get("system.use.usercode"), "0").equals("1")) {
                            return record.getStock().getUserCode();
                        } else {
                            return record.getStock().getStockCode();
                        }
                    }
                }
                case 1 -> {
                    //Name
                    if (record.getStock() == null) {
                        return null;
                    } else {
                        return record.getStock().getStockName();
                    }
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
                    return new Object();
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
                            calStockBalance(stock, true);
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
                    if (record.getPrice() > 0) {
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
                            calPrice(record);
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
            if (hasEmptyRow()) {
                SaleHisDetail pd = new SaleHisDetail();
                pd.setStock(new Stock());
                pd.setLocation(locationAutoCompleter.getLocation());
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
        txtTotalItem.setText(Integer.toString(listDetail.size()));
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.size() > 1) {
            SaleHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStock().getStockCode() == null) {
                status = false;
            }
        }
        return status;
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
            sale.setAmount(amount);
        }
    }

    private void calPrice(SaleHisDetail sdh) {
        Stock stock = sdh.getStock();
        float saleAmount = 0.0f;
        float userPrice = Util1.getFloat(sdh.getPrice());
        float userWt = Util1.getFloat(sdh.getSaleWt());
        float stdWt = Util1.getFloat(stock.getSaleWeight());
        String fromUnit = stock.getSaleUnit().getItemUnitCode();
        String toUnit = sdh.getSaleUnit().getItemUnitCode();
        // String  pattern = stock.getPattern().getPatternCode();

        if (!fromUnit.equals(toUnit)) {
            /* RelationKey key = new RelationKey(fromUnit, toUnit, pattern);
            UnitRelation unitRelation = relationService.findByKey(key);
            if (unitRelation != null) {
            float factor = unitRelation.getFactor();
            float convertWt = (userWt / factor); //unit change
            saleAmount = (convertWt / stdWt) * userPrice; // cal price
            
            } else {
            key = new RelationKey(toUnit, fromUnit, pattern);
            Float factor = Global.hmRelation.get(key);
            if (factor != null) {
            float convertWt = userWt * factor; // unit change
            saleAmount = (convertWt / stdWt) * userPrice; // cal price
            } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Mapping units in Relation Setup.");
            }
            }*/
        } else {
            saleAmount = (userWt / stdWt) * userPrice;
        }
        sdh.setAmount(saleAmount);
    }

    private Float getSmallestWeight(Float weight, String unit, String purUnit, String pattern) {
        float sWt = 0.0f;
        if (!unit.equals(purUnit)) {
            /*RelationKey key = new RelationKey(unit, purUnit, pattern);
            Float factor = Global.hmRelation.get(key);
            if (factor != null) {
            sWt = factor * weight;
            } else {
            key = new RelationKey(purUnit, unit, pattern);
            factor = Global.hmRelation.get(key);
            if (factor != null) {
            sWt = weight / factor;
            } else {
            JOptionPane.showMessageDialog(Global.parentForm, String.format("Need Relation  %s with Smallest Unit", unit));
            listDetail.remove(parent.getSelectedRow());
            }
            }*/
        } else {
            sWt = weight;
        }
        return sWt;
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public List<SaleHisDetail> getListSaleDetail() {
        List<SaleHisDetail> listpurDetail = new ArrayList();
        listDetail.stream().filter(pdh2 -> (pdh2.getStock() != null)).filter(pdh2 -> (pdh2.getStock().getStockCode() != null)).forEachOrdered(pdh2 -> {
            listpurDetail.add(pdh2);
        });

        return listpurDetail;
    }

    public void setTxtTotalItem(JTextField txtTtlItem) {
        this.txtTotalItem = txtTtlItem;
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (SaleHisDetail sdh : listDetail) {
            if (sdh.getStock().getStockCode() != null) {
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

    public void calStockBalance(int row, boolean refresh) {
        if (listDetail != null) {
            SaleHisDetail sd = listDetail.get(row);
            if (sd.getStock() != null) {
                if (sd.getStock().getStockCode() != null) {
                    calStockBalance(sd.getStock(), refresh);
                }
            }
        }
    }

    private void calStockBalance(Stock stock, boolean refresh) {
        String isStock = Global.sysProperties.get("system.sale.stock.balance");
        if (Util1.isNull(isStock, "0").equals("1")) {
            taskExecutor.execute(() -> {
                String stockCode = stock.getStockCode();
                if (refresh) {
                    hmBalance.remove(stockCode);
                }
                lblStockName.setText(stock.getStockName());
                btnProgress.setIcon(progressIcon);
                if (!hmBalance.containsKey(stockCode)) {
                    log.info("Calculating Stock Balance : " + stockCode);
                    //reportService.generateStockBalance("'" + stockCode + "'", "-", Global.compCode, Global.machineId.toString());
                    List<StockBalanceTmp> listStockBalance = new ArrayList<>();
                    if (listStockBalance.isEmpty()) {
                        StockBalanceTmp tmp = new StockBalanceTmp();
                        listStockBalance.add(tmp);
                    }
                    balanceTableModel.setListStockBalance(listStockBalance);
                    hmBalance.put(stockCode, listStockBalance);
                } else {
                    balanceTableModel.setListStockBalance(hmBalance.get(stockCode));
                }
                btnProgress.setIcon(refreshIcon);
            });
        }
    }

}
