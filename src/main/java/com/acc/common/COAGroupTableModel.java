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
import com.inventory.entity.MessageType;
import java.awt.HeadlessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class COAGroupTableModel extends AbstractTableModel {

    private List<ChartOfAccount> listCOA = new ArrayList();
    private final String[] columnNames = {"System Code", "User Code", "Name", "Active", "Head"};
    private JTable parent;
    private String coaHeadCode;
    private JLabel paretnDesp;
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

    public JLabel getParetnDesp() {
        return paretnDesp;
    }

    public void setParetnDesp(JLabel paretnDesp) {
        this.paretnDesp = paretnDesp;
    }

    public void setCoaHeadCode(String coaHeadCode) {
        this.coaHeadCode = coaHeadCode;
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
            coa.setCoaLevel(2);
            coa.setCoaParent(Util1.isNull(coa.getCoaParent(), coaHeadCode));
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
                if (t.getKey().getCoaCode() != null) {
                    listCOA.set(row, t);
                    addEmptyRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(1, 1);
                    sendMessage(t.getCoaNameEng());
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(parent, e.getMessage());
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        accountRepo.sendDownloadMessage(MessageType.COA, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
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
        return edit;
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
            if (Objects.isNull(coa.getKey().getCoaCode())) {
                coa.setActive(true);
                coa.setCreatedBy(Global.loginUser.getUserCode());
                coa.setCreatedDate(LocalDateTime.now());
                coa.setMacId(Global.macId);
                coa.setCoaOption("USR");
            } else {
                coa.setModifiedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listCOA.isEmpty() || listCOA == null) {
            status = true;
        } else {
            ChartOfAccount coa = listCOA.get(listCOA.size() - 1);
            if (coa.getKey().getCoaCode() == null) {
                status = false;
            }
        }

        return status;
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
}
