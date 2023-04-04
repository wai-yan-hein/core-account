/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.COATemplate;
import com.acc.model.COATemplateKey;
import com.common.Util1;
import java.awt.HeadlessException;
import java.util.ArrayList;
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
public class COATemplateTableModel extends AbstractTableModel {

    private List<COATemplate> listCOA = new ArrayList();
    String[] columnNames = {"No", " Code", "Name", "Active"};
    private JTable parent;
    private String coaGroupCode;
    private AccountRepo accountRepo;
    private Integer busId;

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

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
            COATemplate coa = listCOA.get(row);
            switch (column) {
                case 0 -> {
                    return String.valueOf(row + 1 + ". ");
                }
                case 1 -> {
                    return coa.getKey() == null ? null : coa.getKey().getCoaCode();
                }
                case 2 -> {
                    return coa.getCoaNameEng();
                }
                case 3 -> {
                    return coa.isActive();
                }
            } //Code
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
            COATemplate coa = listCOA.get(row);
            switch (column) {
                case 2 -> {
                    //user code
                    if (value != null) {
                        coa.setCoaCodeUsr(value.toString());
                        parent.setColumnSelectionInterval(2, 2);
                    }
                }
                case 3 -> {
                    if (value != null) {
                        coa.setCoaNameEng(value.toString());
                    }
                }
                case 4 -> {
                    if (value != null) {
                        Boolean active = (Boolean) value;
                        coa.setActive(active);
                    } else {
                        coa.setActive(Boolean.TRUE);
                    }
                }
                case 5 -> {
                    if (value instanceof COATemplate c) {
                        int yn = JOptionPane.showConfirmDialog(parent, "Do you want to change group?", "Confirm Dialog", JOptionPane.WARNING_MESSAGE);
                        if (yn == JOptionPane.YES_OPTION) {
                            coa.setCoaParent(c.getKey().getCoaCode());
                        }
                    }
                }
            }
            coa.setCoaLevel(3);
            coa.setCoaParent(Util1.isNull(coa.getCoaParent(), coaGroupCode));
            save(coa, row);
            parent.requestFocus();
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

    }

    private void save(COATemplate coa, int row) {
        if (isValidCOA(coa)) {
            accountRepo.save(coa).subscribe((t) -> {
                if (t.getKey().getCoaCode() != null) {
                    listCOA.set(row, t);
                    addEmptyRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(1, 1);
                }
            });

        }
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3 ->
                Boolean.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;

    }

    public COATemplate getChartOfAccount(int row) {
        return listCOA.get(row);
    }

    public void addCoa(COATemplate coa) {
        listCOA.add(coa);
        fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
    }

    public void setCoaGroup(int row, COATemplate coa) {
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

    public List<COATemplate> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<COATemplate> listCOA) {
        this.listCOA = listCOA;
        fireTableDataChanged();
    }

    public boolean isValidCOA(COATemplate coa) {
        boolean status = true;
        if (Util1.isNull(coa.getCoaNameEng())) {
            status = false;
        } else if (Util1.isNull(coa.getCoaParent())) {
            status = false;
            JOptionPane.showMessageDialog(parent, "Select Group.");
        } else if (Util1.isNull(coa.getCoaLevel())) {
            status = false;
        } else {
            coa.setActive(true);
        }
        return status;
    }

    private boolean hasEmptyRow() {
        boolean valid = true;
        if (listCOA.isEmpty() || listCOA == null) {
            valid = true;
        } else {
            COATemplate coa = listCOA.get(listCOA.size() - 1);
            if (coa.getKey().getCoaCode() == null) {
                valid = false;
            }
        }

        return valid;
    }

    public void addEmptyRow() {
        if (listCOA != null) {
            if (hasEmptyRow()) {
                COATemplate coa = new COATemplate();
                COATemplateKey key = new COATemplateKey();
                coa.setKey(key);
                listCOA.add(coa);
                fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
            }
        }

    }

    public void clear() {
        if (listCOA != null) {
            listCOA.clear();
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
        listCOA.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
