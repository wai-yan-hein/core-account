/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
import com.repo.AccountRepo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StandardCOATableModel extends AbstractTableModel {

    private List<ChartOfAccount> listCOA = new ArrayList();
    String[] columnNames = {"System Code", "User Code", "Name", "COA-Level", "Mark"};
    private JTable parent;
    private AccountRepo accountRepo;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
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
                    coa.getKey().getCoaCode();
                case 1 ->
                    coa.getCoaCodeUsr();
                case 2 ->
                    coa.getCoaNameEng();
                case 3 ->
                    coa.getCoaLevel();
                case 4 ->
                    coa.isMarked();
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
                case 4 -> {
                    if (value instanceof Boolean mark) {
                        coa.setMarked(mark);
                    }
                }
                default -> {
                }
            }
            save(coa);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

    }

    private ChartOfAccount save(ChartOfAccount coa) {
        return accountRepo.saveCOA(coa).block();
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
                String.class;
            case 4 ->
                Boolean.class;
            default ->
                Object.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1 || column == 4;

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

    public void clear() {
        if (listCOA != null) {
            listCOA.clear();
            fireTableDataChanged();
        }
    }
}
