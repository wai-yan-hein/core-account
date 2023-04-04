/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisDetailKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class ProcessHisDetailTableModel extends AbstractTableModel {

    private List<ProcessHisDetail> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Stock Code", "Stock Name", "Location", "Qty", "Unit", "Price", "Amount"};
    private JTextField txtVouNo;
    private JTable table;
    private InventoryRepo inventoryRepo;
    private JFormattedTextField txtPrice;
    private JFormattedTextField txtQty;
    private JFormattedTextField txtAmt;
    private JDateChooser txtVouDate;
    private JRadioButton rdoAvg;
    private JRadioButton rdoRecent;
    private JRadioButton rdoProRecent;
    private JLabel lblRec;

    public JLabel getLblRec() {
        return lblRec;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public JFormattedTextField getTxtQty() {
        return txtQty;
    }

    public void setTxtQty(JFormattedTextField txtQty) {
        this.txtQty = txtQty;
    }

    public JFormattedTextField getTxtAmt() {
        return txtAmt;
    }

    public void setTxtAmt(JFormattedTextField txtAmt) {
        this.txtAmt = txtAmt;
    }

    public JRadioButton getRdoProRecent() {
        return rdoProRecent;
    }

    public void setRdoProRecent(JRadioButton rdoProRecent) {
        this.rdoProRecent = rdoProRecent;
    }

    public JRadioButton getRdoAvg() {
        return rdoAvg;
    }

    public void setRdoAvg(JRadioButton rdoAvg) {
        this.rdoAvg = rdoAvg;
    }

    public JRadioButton getRdoRecent() {
        return rdoRecent;
    }

    public void setRdoRecent(JRadioButton rdoRecent) {
        this.rdoRecent = rdoRecent;
    }

    public JDateChooser getTxtVouDate() {
        return txtVouDate;
    }

    public void setTxtVouDate(JDateChooser txtVouDate) {
        this.txtVouDate = txtVouDate;
    }

    public JFormattedTextField getTxtPrice() {
        return txtPrice;
    }

    public void setTxtPrice(JFormattedTextField txtPrice) {
        this.txtPrice = txtPrice;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public JTextField getTxtVouNo() {
        return txtVouNo;
    }

    public void setTxtVouNo(JTextField txtVouNo) {
        this.txtVouNo = txtVouNo;
    }

    public ProcessHisDetailTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 7;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 4, 6, 7 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            ProcessHisDetail med = listDetail.get(row);

            return switch (column) {
                case 0 ->
                    med.getVouDate() == null ? null : Util1.toDateStr(med.getVouDate(), "dd/MM/yyyy");
                case 1 ->
                    med.getStockUsrCode();
                case 2 ->
                    med.getStockName();
                case 3 ->
                    med.getLocName();
                case 4 ->
                    med.getQty() == 0 ? null : med.getQty();
                case 5 ->
                    med.getUnit();
                case 6 ->
                    med.getPrice() == 0 ? null : med.getPrice();
                case 7 ->
                    med.getPrice() * med.getQty();
                default ->
                    null;
            }; //Code
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (value != null) {
                ProcessHisDetail p = listDetail.get(row);
                switch (column) {
                    case 0 ->
                        p.setVouDate(Util1.formatDate(value));
                    case 1, 2 -> {
                        if (value instanceof Stock s) {
                            p.setStockUsrCode(s.getUserCode());
                            p.setStockName(s.getStockName());
                            p.setUnit(s.getPurUnitCode());
                            p.getKey().setStockCode(s.getKey().getStockCode());
                            if (p.getKey().getLocCode() != null) {
                                table.setColumnSelectionInterval(4, 4);
                            } else {
                                table.setColumnSelectionInterval(3, 3);
                            }
                        }
                    }
                    case 3 -> {
                        if (value instanceof Location l) {
                            p.getKey().setLocCode(l.getKey().getLocCode());
                            p.setLocName(l.getLocName());
                            table.setColumnSelectionInterval(4, 4);
                        }
                    }
                    case 4 -> {
                        p.setQty(Util1.getFloat(value));
                        if (p.getUnit() != null) {
                            table.setColumnSelectionInterval(6, 6);
                        }
                    }
                    case 5 -> {
                        if (value instanceof StockUnit u) {
                            p.setUnit(u.getKey().getUnitCode());
                        }
                    }
                    case 6 ->
                        p.setPrice(Util1.getFloat(value));
                }
                if (column != 6) {
                    if (Util1.getFloat(p.getPrice()) == 0) {
                        if (p.getKey().getStockCode() != null && p.getUnit() != null) {
                            if (rdoRecent.isSelected()) {
                                inventoryRepo.getPurRecentPrice(p.getKey().getStockCode(),
                                        Util1.toDateStr(p.getVouDate(), "yyyy-MM-dd"), p.getUnit()).subscribe((t) -> {
                                    p.setPrice(t.getAmount());
                                });
                            } else if (rdoAvg.isSelected()) {
                                inventoryRepo.getPurAvgPrice(p.getKey().getStockCode(),
                                        Util1.toDateStr(p.getVouDate(), "yyyy-MM-dd"), p.getUnit()).subscribe((t) -> {
                                    p.setPrice(t.getAmount());
                                });
                            } else if (rdoProRecent.isSelected()) {
                                inventoryRepo.getProductionRecentPrice(p.getKey().getStockCode(),
                                        Util1.toDateStr(p.getVouDate(), "yyyy-MM-dd"), p.getUnit()).subscribe((t) -> {
                                    p.setPrice(t.getAmount());
                                });
                            }
                        }
                    }
                }
                calPrice();
                addNewRow();
                fireTableRowsUpdated(row, row);
                table.requestFocusInWindow();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private boolean isValidEntry(ProcessHisDetail pd) {
        if (pd.getKey().getStockCode() == null) {
            JOptionPane.showMessageDialog(table, "Invalid Stock.");
            return false;
        } else if (pd.getKey().getLocCode() == null) {
            JOptionPane.showMessageDialog(table, "Invalid Location.");
            return false;
        } else if (pd.getUnit() == null) {
            JOptionPane.showMessageDialog(table, "Invalid Unit.");
            return false;
        } else if (pd.getVouDate() == null) {
            JOptionPane.showMessageDialog(table, "Invalid Date.");
            return false;
        } else if (Util1.getFloat(pd.getPrice()) == 0) {
            JOptionPane.showMessageDialog(table, "Invalid Price.");
            return false;
        } else if (Util1.getFloat(pd.getQty()) == 0) {
            JOptionPane.showMessageDialog(table, "Invalid Qty.");
            return false;
        }
        return true;
    }

    public void calPrice() {
        float ttlAmt = 0.0f;
        float qty = Util1.getFloat(txtQty.getValue());
        for (ProcessHisDetail pd : listDetail) {
            ttlAmt += Util1.getFloat(pd.getPrice()) * Util1.getFloat(pd.getQty());
        }
        lblRec.setText("Records : " + String.valueOf(listDetail.size() - 1));
        if (ttlAmt > 0) {
            txtPrice.setValue(ttlAmt / qty);
            txtAmt.setValue(ttlAmt);
            txtPrice.setEditable(false);
            txtAmt.setEditable(false);
        } else {
            txtAmt.setValue(qty * Util1.getFloat(txtPrice.getValue()));
            txtPrice.setEditable(true);
            txtAmt.setEditable(false);
        }

    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ProcessHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<ProcessHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public ProcessHisDetail getObject(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(ProcessHisDetail stock) {
        if (listDetail != null) {
            listDetail.add(stock);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);

        }
    }

    public void setObject(int row, ProcessHisDetail stock) {
        if (listDetail != null) {
            listDetail.set(row, stock);
            fireTableRowsUpdated(row, row);
        }
    }

    public void deleteObject(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                listDetail.remove(row);
                fireTableDataChanged();
            }
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    private ProcessHisDetail aboveObject() {
        if (!listDetail.isEmpty()) {
            return listDetail.get(listDetail.size() - 1);
        }
        return null;
    }

    public void addNewRow() {
        if (!hasEmptyRow()) {
            ProcessHisDetail pd = new ProcessHisDetail();
            ProcessHisDetailKey key = new ProcessHisDetailKey();
            key.setCompCode(Global.compCode);
            key.setDeptId(Global.deptId);
            key.setVouNo(txtVouNo.getText());
            pd.setKey(key);
            pd.setVouDate(txtVouDate.getDate());
            ProcessHisDetail a = aboveObject();
            if (a != null) {
                pd.setVouDate(a.getVouDate());
                pd.getKey().setLocCode(a.getKey().getLocCode());
                pd.setLocName(a.getLocName());
            }
            listDetail.add(pd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public boolean validEntry() {
        if (listDetail.size() <= 1) {
            JOptionPane.showMessageDialog(table, "Invalid Value Added Stock.");
            table.requestFocus();
            return false;
        }
        for (int i = 0; i < listDetail.size(); i++) {
            ProcessHisDetail pd = listDetail.get(i);
            if (pd.getKey().getStockCode() != null) {
                if (pd.getKey().getStockCode() == null) {
                    JOptionPane.showMessageDialog(table, "Invalid Stock.");
                    return false;
                } else if (pd.getKey().getLocCode() == null) {
                    foucsTable(i, 3);
                    JOptionPane.showMessageDialog(table, "Invalid Location.");
                    return false;
                } else if (pd.getUnit() == null) {
                    foucsTable(i, 5);
                    JOptionPane.showMessageDialog(table, "Invalid Unit.");
                    return false;
                } else if (pd.getVouDate() == null) {
                    foucsTable(i, 1);
                    JOptionPane.showMessageDialog(table, "Invalid Date.");
                    return false;
                } else if (Util1.getFloat(pd.getPrice()) == 0) {
                    foucsTable(i, 6);
                    JOptionPane.showMessageDialog(table, "Invalid Price.");
                    return false;
                } else if (Util1.getFloat(pd.getQty()) == 0) {
                    foucsTable(i, 4);
                    JOptionPane.showMessageDialog(table, "Invalid Qty.");
                    return false;
                }
            }
        }
        return true;
    }

    private void foucsTable(int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
        table.requestFocus();
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.isEmpty()) {
            return false;
        }
        if (listDetail.size() >= 1) {
            ProcessHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockName() != null) {
                status = false;
            }
        }
        return status;
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
