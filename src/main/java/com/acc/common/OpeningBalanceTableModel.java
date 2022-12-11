/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import com.acc.model.OpeningBalance;
import com.acc.model.ChartOfAccount;
import com.acc.model.Department;
import com.acc.model.OpeningKey;
import com.acc.model.TraderA;
import com.user.model.Currency;

import com.common.SelectionObserver;
import com.common.Global;
import com.common.Util1;

import com.acc.editor.TraderAAutoCompleter;

import com.toedter.calendar.JDateChooser;

import java.awt.HeadlessException;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author htut
 */
public class OpeningBalanceTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(OpeningBalanceTableModel.class);
    private AccountRepo accountRepo;
    private JTable parent;

    private TraderAAutoCompleter tradeAutoCompleter;
    private SelectionObserver selectionObserver;

    private String[] columnsName = {"Code", "Chart Of Account", "Trader Code", "Trader Name", "Dept:", "Currency", "Dr-Amt", "Cr-Amt"};
    private List<OpeningBalance> listOpening = new ArrayList();
    private JDateChooser opDate;

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
            case 6 ->
                Double.class;
            case 7 ->
                Double.class;
            default ->
                String.class;
        };
    }

    // get data to grid view
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!listOpening.isEmpty()) {
            OpeningBalance opening = listOpening.get(rowIndex);
            switch (columnIndex) {
                case 0 -> {
                    return opening.getCoaUsrCode();
                }
                case 1 -> {
                    return opening.getSrcAccName();
                }
                case 2 -> {
                    return opening.getTradeUsrCode() == null ? opening.getTraderCode() : opening.getTradeUsrCode();
                }
                case 3 -> {
                    return opening.getTraderName();
                }
                case 4 -> {
                    return opening.getDeptUsrCode();
                }
                case 5 -> {
                    return opening.getCurCode();
                }
                case 6 -> {
                    return Util1.getDouble(opening.getDrAmt());
                }
                case 7 -> {
                    return Util1.getDouble(opening.getCrAmt());
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
                case 0, 1 -> {
                    if (value != null) {
                        if (value instanceof ChartOfAccount coa) {
                            opening.setCoaUsrCode(coa.getCoaCodeUsr());
                            opening.setSourceAccId(coa.getKey().getCoaCode());
                            opening.setSrcAccName(coa.getCoaNameEng());
                            parent.setRowSelectionInterval(rowIndex, rowIndex);
                            parent.setColumnSelectionInterval(2, 2);
                        }
                    }
                }
                case 2, 3 -> {
                    if (value != null) {
                        if (value instanceof TraderA trader) {
                            opening.setTraderCode(trader.getKey().getCode());
                            opening.setTraderName(trader.getTraderName());
                            String coaCode = trader.getAccCode();
                            if (coaCode != null) {
                                ChartOfAccount coa = accountRepo.findCOA(coaCode);
                                if (coa != null) {
                                    opening.setSourceAccId(coaCode);
                                    opening.setSrcAccName(coa.getCoaNameEng());
                                    opening.setCoaUsrCode(coa.getCoaCodeUsr());
                                }
                            }
                            parent.setRowSelectionInterval(rowIndex, rowIndex);
                            parent.setColumnSelectionInterval(4, 4);
                        }
                    }
                }
                case 4 -> {
                    if (value != null) {
                        if (value instanceof Department dep) {
                            opening.setDeptCode(dep.getKey().getDeptCode());
                            opening.setDeptUsrCode(dep.getUserCode());
                            parent.setColumnSelectionInterval(5, 5);
                        }
                    }
                }
                case 5 -> {
                    if (value != null) {
                        if (value instanceof Currency cur) {
                            opening.setCurCode(cur.getCurCode());
                            parent.setRowSelectionInterval(rowIndex, rowIndex);
                            parent.setColumnSelectionInterval(6, 6);
                        }
                    }
                }
                case 6 -> {
                    opening.setDrAmt(Util1.getDouble(value));
                    opening.setCrAmt(null);
                }
                case 7 -> {
                    opening.setCrAmt(Util1.getDouble(value));
                    opening.setDrAmt(null);
                }
            }
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
            if (col > 1) {
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
        if (opening.getKey().getOpId() == null) {
            opening.setCreatedDate(Util1.getTodayDate());
        }
        OpeningBalance op = accountRepo.saveCOAOpening(opening);
        if (op != null) {
            opening.setKey(op.getKey());
            listOpening.set(row, opening);
            addNewRow();
            double amt = Util1.getDouble(opening.getDrAmt()) + Util1.getDouble(opening.getCrAmt());
            if (amt == 0) {
                parent.setColumnSelectionInterval(6, 6);
            } else {
                parent.setRowSelectionInterval(row + 1, row + 1);
                parent.setColumnSelectionInterval(0, 0);
            }
            selectionObserver.selected("CAL-TOTAL", "-");
        }
    }

    public void addNewRow() {
        if (!isEmptyRow()) {
            OpeningBalance opening = new OpeningBalance();
            OpeningKey key = new OpeningKey();
            key.setCompCode(Global.compCode);
            opening.setKey(key);
            opening.setOpDate(opDate.getDate());
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
        return p.getKey().getOpId() == null;
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

    public void deleteOpening(int rowIndex) {
        if (!listOpening.isEmpty()) {
            listOpening.remove(rowIndex);
            fireTableRowsDeleted(0, listOpening.size());
        }
    }

    public void addOpening(OpeningBalance opening) {
        listOpening.add(opening);
        fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
    }

    public void setOpening(int rowIndex, OpeningBalance opening) {
        if (!listOpening.isEmpty()) {
            listOpening.set(rowIndex, opening);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    public void clear() {
        if (listOpening != null) {
            listOpening.clear();
            fireTableDataChanged();
        }
    }

}