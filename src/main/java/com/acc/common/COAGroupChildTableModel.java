/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author MyoGyi
 */
public class COAGroupChildTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COAGroupChildTableModel.class);
    private List<ChartOfAccount> listCOA = new ArrayList();
    String[] columnNames = {"System Code", "User Code", "Name", "Active"};
    private JTable parent;
    private String coaGroupCode;
    private AccountRepo accountRepo;

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setCoaGroupCode(String coaGroupCode) {
        this.coaGroupCode = coaGroupCode;
    }

    @Override
    public int getRowCount() {
        if (listCOA == null) {
            return 0;
        }
        return listCOA.size();
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
    public Object getValueAt(int row, int column) {
        try {
            ChartOfAccount coa = listCOA.get(row);

            return switch (column) {
                case 0 ->
                    coa.getCoaCode();
                case 1 ->
                    coa.getCoaCodeUsr();
                case 2 ->
                    coa.getCoaNameEng();
                case 3 ->
                    coa.isActive();
                default ->
                    null;
            }; //Code
            //User Code
            //Name
            //Active
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

        try {

            ChartOfAccount coa = listCOA.get(row);
            switch (column) {
                case 0 -> {
                }
                case 1 -> {
                    //user code
                    if (value != null) {
                        coa.setCoaCodeUsr(value.toString());
                        parent.setColumnSelectionInterval(2, 2);
                    }
                }
                case 2 -> {
                    if (value != null) {
                        coa.setCoaNameEng(value.toString());
                    }
                }
                case 3 -> {
                    if (value != null) {
                        Boolean active = (Boolean) value;
                        coa.setActive(active);
                    } else {
                        coa.setActive(Boolean.TRUE);
                    }
                }
                default -> {
                }
            }
            coa.setCoaLevel(3);
            coa.setCoaParent(coaGroupCode);
            save(coa, row);
            parent.requestFocus();
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

    }

    private void save(ChartOfAccount coa, int row) {
        if (isValidCOA(coa)) {
            try {
                ChartOfAccount save = accountRepo.saveCOA(coa);
                if (save.getCoaCode() != null) {
                    listCOA.set(row, save);
                    addEmptyRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(1, 1);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
            }

        }
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0 ->
                String.class;
            case 1 ->
                String.class;
            case 2 ->
                String.class;
            case 3 ->
                Boolean.class;
            default ->
                Object.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column > 0;

    }

    public ChartOfAccount getChartOfAccount(int row) {
        return listCOA.get(row);
    }

    public void addCoa(ChartOfAccount coa) {
        listCOA.add(coa);
        fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
    }

    public void setCoaGroup(int row, ChartOfAccount coa) {
        if (!listCOA.isEmpty()) {
            listCOA.set(row, coa);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public List<ChartOfAccount> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<ChartOfAccount> listCOA) {
        this.listCOA = listCOA;
        fireTableDataChanged();
    }

    public boolean isValidCOA(ChartOfAccount coa) {
        boolean status = true;
        if (Util1.isNull(coa.getCoaNameEng())) {
            status = false;
        } else if (Util1.isNull(coa.getCoaParent())) {
            status = false;
            JOptionPane.showMessageDialog(parent, "Select Group.");
        } else if (Util1.isNull(coa.getCoaLevel())) {
            status = false;
        } else {
            if (Objects.isNull(coa.getCoaCode())) {
                coa.setCompCode(Global.compCode);
                coa.setCreatedBy(Global.loginUser.getUserCode());
                coa.setCreatedDate(Util1.getTodayDate());
                coa.setMacId(Global.macId);
                coa.setOption("USR");
            } else {
                coa.setModifiedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private boolean hasEmptyRow() {
        boolean valid = true;
        if (listCOA.isEmpty() || listCOA == null) {
            valid = true;
        } else {
            ChartOfAccount coa = listCOA.get(listCOA.size() - 1);
            if (coa.getCoaCode() == null) {
                valid = false;
            }
        }

        return valid;
    }

    public void addEmptyRow() {
        if (listCOA != null) {
            if (hasEmptyRow()) {
                ChartOfAccount coa = new ChartOfAccount();
                listCOA.add(coa);
                fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
            }
            //parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));

        }

    }

    public void clear() {
        if (listCOA != null) {
            listCOA.clear();
            fireTableDataChanged();
        }
    }
}
