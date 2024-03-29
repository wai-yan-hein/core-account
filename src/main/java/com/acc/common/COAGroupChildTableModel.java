/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.repo.AccountRepo;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.Util1;
import java.awt.HeadlessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class COAGroupChildTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COAGroupChildTableModel.class);
    private List<ChartOfAccount> listCOA = new ArrayList();
    String[] columnNames = {"System Code", "User Code", "Name", "Active", "Group"};
    private JTable parent;
    private String coaGroupCode;
    private AccountRepo accountRepo;
    private HashMap<String, String> hmCOA = new HashMap<>();
    private boolean edit;
    private JProgressBar progress;

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
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
            ChartOfAccount coa = listCOA.get(row);
            switch (column) {
                case 0 -> {
                    return coa.getKey() == null ? null : coa.getKey().getCoaCode();
                }
                case 1 -> {
                    return coa.getCoaCodeUsr();
                }
                case 2 -> {
                    return coa.getCoaNameEng();
                }
                case 3 -> {
                    return coa.isActive();
                }
                case 4 -> {
                    return getParentName(coa.getCoaParent());
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

    private String getParentName(String parentCode) {
        if (hmCOA.get(parentCode) == null) {
            accountRepo.findCOA(parentCode).subscribe((obj) -> {
                if (obj != null) {
                    hmCOA.put(parentCode, obj.getCoaNameEng());
                }
            });

        }
        return hmCOA.get(parentCode);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

        try {
            ChartOfAccount coa = listCOA.get(row);
            switch (column) {
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
                case 4 -> {
                    if (value instanceof ChartOfAccount c) {
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

    private void save(ChartOfAccount coa, int row) {
        if (isValidCOA(coa)) {
            coa.setCoaNameEng(Util1.convertToUniCode(coa.getCoaNameEng()));
            progress.setIndeterminate(true);
            accountRepo.saveCOA(coa).doOnSuccess((t) -> {
                if (t != null) {
                    listCOA.set(row, t);
                    addEmptyRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(1, 1);
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Error", JOptionPane.ERROR);
            }).subscribe();

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
        if (edit) {
            ChartOfAccount c = listCOA.get(row);
            if (c.isMarked()) {
                return false;
            }
            return switch (column) {
                case 0 ->
                    false;
                default ->
                    true;
            };
        }
        return false;
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
            coa.setCoaOption("USR");
            if (Util1.isNullOrEmpty(coa.getKey().getCoaCode())) {
                coa.setActive(true);
                coa.setCreatedBy(Global.loginUser.getUserCode());
                coa.setCreatedDate(LocalDateTime.now());
                coa.setMacId(Global.macId);
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
            if (coa.getKey().getCoaCode() == null) {
                valid = false;
            }
        }

        return valid;
    }

    public void addEmptyRow() {
        if (listCOA != null) {
            if (hasEmptyRow()) {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCompCode(Global.compCode);
                coa.setKey(key);
                listCOA.add(coa);
                fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
            }
            progress.setIndeterminate(false);
        }

    }

    public void clear() {
        hmCOA.clear();
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
