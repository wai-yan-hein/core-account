/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.acc.model.ChartOfAccount;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.LabourPaymentDetail;
import com.repo.AccountRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class LabourPaymentTableModel extends AbstractTableModel {

    private List<LabourPaymentDetail> listDetail = new ArrayList<>();
    private final String[] columnNames = {"Description", "Qty", "Price", "Amount", "Expens A/C"};
    private JTable table;
    private SelectionObserver observer;
    private HashMap<String, String> hm = new HashMap<>();
    private AccountRepo accountRepo;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public LabourPaymentTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 3;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 1, 2, 3 -> {
                return Double.class;
            }
        }
        return String.class;
    }

    public List<LabourPaymentDetail> getPaymentList() {
        listDetail.removeIf(p -> Util1.getDouble(p.getAmount()) == 0.0);
        return listDetail;

    }

    public List<LabourPaymentDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LabourPaymentDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            LabourPaymentDetail b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getDescription();
                case 1 ->
                    b.getQty();
                case 2 ->
                    b.getPrice();
                case 3 ->
                    b.getAmount();
                case 4 ->
                    getCoaName(b.getAccount());
                default ->
                    null;
            }; //Code
            //Description
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (value != null) {
                LabourPaymentDetail obj = listDetail.get(row);
                switch (column) {
                    case 0 ->
                        obj.setDescription(value.toString());
                    case 1 ->
                        obj.setQty(Util1.getDouble(value));
                    case 2 ->
                        obj.setPrice(Util1.getDouble(value));
                    case 4 -> {
                        if (value instanceof ChartOfAccount coa) {
                            obj.setAccount(coa.getKey().getCoaCode());
                            obj.setAccountName(coa.getCoaNameEng());
                        }
                    }
                }
                calAmount(obj);
                addNewRow();
                fireTableRowsUpdated(row, row);
                observer.selected("CAL_PAYMENT", "CAL_PAYMENT");
                table.requestFocus();
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }

    }

    private void calAmount(LabourPaymentDetail obj) {
        obj.setAmount(obj.getQty() * obj.getPrice());
    }

    private String getCoaName(String coaCode) {
        if (hm.containsKey(coaCode)) {
            return hm.get(coaCode);
        }
        ChartOfAccount coa = accountRepo.findCOA(coaCode).block();
        if (coa != null) {
            hm.put(coaCode, coa.getCoaNameEng());
            return coa.getCoaNameEng();
        }
        return "-";

    }

    public void delete(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void setPayment(LabourPaymentDetail t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addPayment(LabourPaymentDetail t) {
        listDetail.add(t);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public LabourPaymentDetail getPayment(int row) {
        if (listDetail == null) {
            return null;
        } else if (listDetail.isEmpty()) {
            return null;
        } else {
            return listDetail.get(row);
        }
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }

    public boolean isValidEntry() {
        return listDetail.stream()
                .filter(pd -> Util1.getDouble(pd.getAmount()) < 0)
                .peek(pd -> {
                    JOptionPane.showMessageDialog(table, "Invalid Pay Amount.");
                    int index = listDetail.indexOf(pd);
                    table.setRowSelectionInterval(index, index);
                    table.setColumnSelectionInterval(7, 7);
                    table.requestFocus();
                })
                .findFirst()
                .isEmpty();
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                LabourPaymentDetail pd = new LabourPaymentDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            LabourPaymentDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getAmount() == 0) {
                return true;
            }
        }
        return false;
    }

}
