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

import ch.qos.logback.classic.pattern.Util;

import com.common.SelectionObserver;
import com.common.Global;
import com.common.Util1;

import com.acc.editor.DepartmentAutoCompleter;
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

    private Currency currency;
    private Department department;

    private DepartmentAutoCompleter deptAutoCompleter;
    private TraderAAutoCompleter tradeAutoCompleter;
    private SelectionObserver selectionObserver;
    private DateAutoCompleter dateAutoCompleter;

    private String[] columnsName = { "Code", "Chart Of Account", "Trader Code", "Trader Name", "Dept:", "Currency","Dr-Amt", "Cr-Amt" };
    private List<OpeningBalance> listOpening = new ArrayList();

    private JDateChooser opDate;
    private boolean valid;
    private String sourceAccId;

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

    public boolean isValid(){
        return valid;
    }

    public void setValid(boolean valid){
        this.valid=valid;
    }

    public JDateChooser getOpDate() {
        return opDate;
    }

    public void setOpdate(JDateChooser opDate) {
        this.opDate = opDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public DepartmentAutoCompleter getDeptAutoCompleter() {
        return deptAutoCompleter;
    }

    public void setDeptAutoCompleter(DepartmentAutoCompleter deptAutoCompleter) {
        this.deptAutoCompleter = deptAutoCompleter;
    }

    public TraderAAutoCompleter getTradeAutoCompleter() {
        return tradeAutoCompleter;
    }

    public void setTradeAutoCompleter(TraderAAutoCompleter tradeAutoCompleter) {
        this.tradeAutoCompleter = tradeAutoCompleter;
    }

    public DateAutoCompleter getDateAutoCompleter() {
        return dateAutoCompleter;
    }

    public void setDateAutoCompleter(DateAutoCompleter dateAutoCompleter) {
        this.dateAutoCompleter = dateAutoCompleter;
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
                    if (opening.getDrAmt() != null) {
                        if (opening.getDrAmt() == 0) {
                            return null;
                        } else {
                            return opening.getDrAmt();
                        }
                    }
                }
                case 7 -> {
                    if (opening.getCrAmt() != null) {
                        if (opening.getCrAmt() == 0) {
                            return null;
                        } else {
                            return opening.getCrAmt();
                        }
                    }
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
                            parent.setRowSelectionInterval(rowIndex,rowIndex);
                            parent.setColumnSelectionInterval(2, 2);
                        }
                    }
                }
                case 2, 3 -> {
                    if (value != null) {
                        if (value instanceof TraderA trader) {
                            opening.setTraderCode(trader.getKey().getCode());
                            opening.setTraderName(trader.getTraderName());
                            if (trader.getAccCode() != null) {
                                opening.setSourceAccId(trader.getAccCode());
                                opening.setSrcAccName(trader.getAccCode());
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
        valid = true;
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
        opening = accountRepo.saveCOAOpening(opening);
        if (opening != null) {
            listOpening.set(row, opening);
            addNewRow();
            parent.setRowSelectionInterval(row + 1, row + 1);
            parent.setColumnSelectionInterval(0, 0);
            selectionObserver.selected("CAL-TOTAL", "-");
        }
    }

    public void addNewRow() {
        if (isEmptyRow()) {
            OpeningBalance opening = new OpeningBalance();
            OpeningKey key = new OpeningKey();
            key.setCompCode(Global.compCode);
            opening.setKey(key);
            String deptCode = deptAutoCompleter.getDepartment().getDeptName();
            String depUserCode = deptAutoCompleter.getDepartment().getUserCode();
            opening.setOpDate(Util1.getTodayDate());
            opening.setUserCode(Global.loginUser.getUserCode());
            opening.setOpDate(opDate.getDate());
            opening.setDeptCode(deptCode.equals("-") ? getDepCode() : deptCode);
            opening.setDeptUsrCode(Util1.isNull(depUserCode, getDepUserCode()));
            listOpening.add(opening);
            fireTableRowsInserted(listOpening.size() - 1, listOpening.size() - 1);
        }
    }

    // check if the list is empty
    private boolean isEmptyRow() {
        boolean status = true;
        if (listOpening.isEmpty() || listOpening == null) {
            status = true;
        } else {
            OpeningBalance opening = listOpening.get(listOpening.size() - 1);
            if (Util1.isNull(opening.getDeptCode()) ||Util1.isNull(opening.getSourceAccId())) {
                status = false;
            }
        }
        return status;
    }

    private String getDepCode() {
        String depCode;
        try {
            depCode = listOpening.get(0).getDeptCode();
        } catch (Exception e) {
            depCode = null;
        }
        return depCode;
    }

    private String getDepUserCode() {
        String depCode;
        try {
            depCode = listOpening.get(0).getDeptUsrCode();
        } catch (Exception e) {
            depCode = null;
        }
        return depCode;
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
