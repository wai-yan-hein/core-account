/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import com.repo.AccountRepo;
import com.acc.model.OpeningBalance;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.acc.model.OpeningKey;
import com.acc.model.TraderA;
import com.user.model.Currency;
import com.common.SelectionObserver;
import com.common.Global;
import com.common.Util1;

import com.acc.editor.TraderAAutoCompleter;

import com.toedter.calendar.JDateChooser;
import com.user.model.Project;

import java.awt.HeadlessException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author htut
 */
@Slf4j
public class OpeningBalanceTableModel extends AbstractTableModel {

    private AccountRepo accountRepo;
    private JTable parent;
    private TraderAAutoCompleter tradeAutoCompleter;
    private SelectionObserver selectionObserver;
    private String[] columnsName = {"Dept:", "Code", "Chart Of Account", "Trader Code", "Trader Name", "Project No ", "Currency", "Dr-Amt", "Cr-Amt"};
    private List<OpeningBalance> listOpening = new ArrayList();
    private JDateChooser opDate;
    private JProgressBar progress;
    @Getter
    private double drAmt;
    @Getter
    private double crAmt;
    @Getter
    private int size;

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public JDateChooser getOpDate() {
        return opDate;
    }

    public void setOpdate(JDateChooser opDate) {
        this.opDate = opDate;
    }

    public TraderAAutoCompleter getTradeAutoCompleter() {
        return tradeAutoCompleter;
    }

    public void setTradeAutoCompleter(TraderAAutoCompleter tradeAutoCompleter) {
        this.tradeAutoCompleter = tradeAutoCompleter;
    }

    public SelectionObserver getObserver() {
        return selectionObserver;
    }

    public void setObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    @Override
    public int getRowCount() {
        return listOpening == null ? 0 : listOpening.size();
    }

    @Override
    public int getColumnCount() {
        return columnsName.length;
    }

    public String[] getColumnNames() {
        return columnsName;
    }

    public void setColumnNames(String[] columnsName) {
        this.columnsName = columnsName;
    }

    @Override
    public String getColumnName(int col) {
        return columnsName[col];
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 7 ->
                Double.class;
            case 8 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!listOpening.isEmpty()) {
            OpeningBalance opening = listOpening.get(rowIndex);
            switch (columnIndex) {
                case 0 -> {
                    return opening.getDeptUsrCode();
                }
                case 1 -> {
                    return opening.getCoaUsrCode();
                }
                case 2 -> {
                    return opening.getSrcAccName();
                }
                case 3 -> {
                    return opening.getTraderUsrCode() == null ? opening.getTraderCode() : opening.getTraderUsrCode();
                }
                case 4 -> {
                    return opening.getTraderName();
                }
                case 5 -> {
                    return opening.getProjectNo();
                }
                case 6 -> {
                    return opening.getCurCode();
                }
                case 7 -> {
                    return Util1.toNull(opening.getDrAmt());
                }
                case 8 -> {
                    return Util1.toNull(opening.getCrAmt());
                }
                default -> {
                    return null;
                }
            }
        }
        return null;
    }

    // set value to grid view
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        try {
            OpeningBalance opening = listOpening.get(rowIndex);
            switch (columnIndex) {
                case 0 -> {
                    if (value != null) {
                        if (value instanceof DepartmentA dep) {
                            opening.setDeptCode(dep.getKey().getDeptCode());
                            opening.setDeptUsrCode(dep.getUserCode());
                            parent.setColumnSelectionInterval(1, 1);
                        }
                    }
                }
                case 1, 2 -> {
                    if (value != null) {
                        if (value instanceof ChartOfAccount coa) {
                            opening.setCoaUsrCode(coa.getCoaCodeUsr());
                            opening.setSourceAccId(coa.getKey().getCoaCode());
                            opening.setSrcAccName(coa.getCoaNameEng());
                            parent.setRowSelectionInterval(rowIndex, rowIndex);
                            parent.setColumnSelectionInterval(3, 3);
                        }
                    }
                }
                case 3, 4 -> {
                    if (value != null) {
                        if (value instanceof TraderA trader) {
                            opening.setTraderUsrCode(trader.getUserCode());
                            opening.setTraderCode(trader.getKey().getCode());
                            opening.setTraderName(trader.getTraderName());
                            String coaCode = trader.getAccount();
                            if (coaCode != null) {
                                accountRepo.findCOA(coaCode).doOnSuccess((coa) -> {
                                    if (coa != null) {
                                        opening.setSourceAccId(coaCode);
                                        opening.setSrcAccName(coa.getCoaNameEng());
                                        opening.setCoaUsrCode(coa.getCoaCodeUsr());
                                    }
                                }).block();
                            }
                            parent.setRowSelectionInterval(rowIndex, rowIndex);
                            parent.setColumnSelectionInterval(5, 6);
                        }
                    }
                }

                case 5 -> {
                    if (value != null) {
                        if (value instanceof Project p) {
                            opening.setProjectNo(p.getKey().getProjectNo());
                            parent.setColumnSelectionInterval(6, 6);
                        }
                    }
                }
                case 6 -> {
                    if (value != null) {
                        if (value instanceof Currency cur) {
                            opening.setCurCode(cur.getCurCode());
                            parent.setRowSelectionInterval(rowIndex, rowIndex);
                            parent.setColumnSelectionInterval(7, 7);
                        }
                    }
                }
                case 7 -> {
                    double amt = Util1.getDouble(value);
                    if (amt > 0) {
                        opening.setDrAmt(amt);
                        opening.setCrAmt(0);
                    } else {
                        opening.setCrAmt(amt);
                        opening.setDrAmt(0);
                    }

                }
                case 8 -> {
                    double amt = Util1.getDouble(value);
                    if (amt > 0) {
                        opening.setCrAmt(amt);
                        opening.setDrAmt(0);
                    } else {
                        opening.setDrAmt(amt);
                        opening.setCrAmt(0);
                    }

                }
            }
            opening.setOpDate(Util1.toLocalDate(opDate.getDate()));
            if (isValidEntry(opening, columnIndex, rowIndex)) {
                save(opening, rowIndex);
                parent.requestFocus();
            }
        } catch (HeadlessException e) {
            log.info("setValueAt : " + e.getMessage());
        }
    }

    // check if the grid volumn data ais valid
    private boolean isValidEntry(OpeningBalance v, int col, int row) {
        boolean valid = true;
        if (Util1.isNull(v.getSourceAccId())) {
            if (col > 6) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Account.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(0, 0);
            }
            valid = false;
        } else if (Util1.isNull(v.getDeptCode())) {
            if (col > 5) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Department.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(4, 4);
            }
            valid = false;
        } else if (Util1.isNull(v.getCurCode())) {
            if (col > 6) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Currency.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(5, 5);
            }
            valid = false;
        } else if (Util1.isNull(v.getCurCode())) {
            valid = false;
        }
        return valid;
    }

    // save opening balance data from the grid view
    private void save(OpeningBalance opening, int row) {
        progress.setIndeterminate(true);
        accountRepo.saveCOAOpening(opening).doOnSuccess((t) -> {
            if (t != null) {
                opening.setKey(t.getKey());
                listOpening.set(row, opening);
                double amt = Util1.getDouble(opening.getDrAmt()) + Util1.getDouble(opening.getCrAmt());
                if (amt == 0) {
                    setSelection(row, 6);
                } else {
                    setSelection(row + 1, 0);
                }
            }
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
            selectionObserver.selected("CAL-TOTAL", "-");
            addNewRow();
        }).subscribe();
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public void addNewRow() {
        if (!isEmptyRow()) {
            OpeningBalance opening = new OpeningBalance();
            OpeningKey key = new OpeningKey();
            key.setCompCode(Global.compCode);
            opening.setKey(key);
            opening.setOpDate(Util1.toLocalDate(opDate.getDate()));
            OpeningBalance b = aboveObject();
            if (b != null) {
                opening.setDeptUsrCode(b.getDeptUsrCode());
                opening.setDeptCode(b.getDeptCode());
                opening.setCurCode(b.getCurCode());
            }
            listOpening.add(opening);
            fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
        }
    }

    private OpeningBalance aboveObject() {
        if (!listOpening.isEmpty()) {
            return listOpening.get(listOpening.size() - 1);
        }
        return null;
    }

    // check if the list is empty
    private boolean isEmptyRow() {
        if (listOpening.isEmpty()) {
            return false;
        }
        OpeningBalance p = listOpening.get(listOpening.size() - 1);
        return p.getKey().getCoaOpId() == null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<OpeningBalance> getListOpening() {
        return listOpening;
    }

    public void setListOpening(List<OpeningBalance> listOpening) {
        this.listOpening = listOpening;
        fireTableDataChanged();
    }

    public OpeningBalance getOpening(int rowIndex) {
        return listOpening.get(rowIndex);
    }

    public void deleteOpening(int row) {
        if (!listOpening.isEmpty()) {
            listOpening.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void addObject(OpeningBalance t) {
        listOpening.add(t);
        drAmt += t.getDrAmt();
        crAmt += t.getCrAmt();
        size += 1;
        int lastIndex = listOpening.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void setOpening(int rowIndex, OpeningBalance opening) {
        if (!listOpening.isEmpty()) {
            listOpening.set(rowIndex, opening);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    public void clear() {
        if (listOpening != null) {
            drAmt = 0;
            crAmt = 0;
            size = 0;
            listOpening.clear();
            fireTableDataChanged();
        }
    }

}
