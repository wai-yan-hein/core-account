/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.repo.AccountRepo;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.acc.model.StockOP;
import com.acc.model.StockOPKey;
import com.common.Global;
import com.common.Util1;
import com.user.model.Currency;
import com.user.model.Project;
import java.awt.HeadlessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class JournalClosingStockTableModel extends AbstractTableModel {

    private List<StockOP> listGV = new ArrayList();
    private final String[] columnNames = {"Stock Closing Date", "Dep :", "Code", "COA Name", "Project No", "Currency", "Closing Amount"};
    private JTable parent;
    private AccountRepo accountRepo;
    private DepartmentA department;
    private JProgressBar progress;

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setDepartment(DepartmentA department) {
        this.department = department;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public int getRowCount() {
        if (listGV == null) {
            return 0;
        }
        return listGV.size();
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
            StockOP op = listGV.get(row);
            return switch (column) {
                case 0 ->
                    Util1.toDateStr(op.getTranDate(), "dd/MM/yyyy");
                case 1 ->
                    op.getDeptUsrCode();
                case 2 ->
                    op.getCoaCodeUser();
                case 3 ->
                    op.getCoaNameEng();
                case 4 ->
                    op.getProjectNo();
                case 5 ->
                    op.getCurCode();
                case 6 ->
                    op.getClAmt();
                default ->
                    null;
            }; //Date
            //Vou
            //Refrence
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            StockOP op = listGV.get(row);
            if (op != null) {
                switch (column) {
                    case 0 -> {
                        if (Util1.isValidDateFormat(value.toString(), "dd/MM/yyyy")) {
                            op.setTranDate(Util1.parseDate(value.toString(), "dd/MM/yyyy"));
                        } else {
                            int length = value.toString().length();
                            if (length == 8 || length == 6) {
                                String toFormatDate = Util1.toFormatDate(value.toString(), length);
                                op.setTranDate(Util1.parseDate(toFormatDate, "dd/MM/yyyy"));
                            } else {
                                op.setTranDate(Util1.getTodayDate());
                                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date");
                            }
                        }
                    }
                    case 1 -> {
                        if (value instanceof DepartmentA dep) {
                            op.setDeptUsrCode(dep.getUserCode());
                            op.setDeptCode(dep.getKey().getDeptCode());
                            foucsTable(row, 2);
                        }
                    }
                    case 2, 3 -> {
                        if (value instanceof ChartOfAccount coa) {
                            op.setCoaCodeUser(coa.getCoaCodeUsr());
                            op.setCoaCode(coa.getKey().getCoaCode());
                            op.setCoaNameEng(coa.getCoaNameEng());
                            if (op.getCurCode() == null) {
                                foucsTable(row, 4);
                            } else {
                                foucsTable(row, 5);
                            }
                        }
                    }
                    case 4 -> {
                        if (value instanceof Project c) {
                            op.setProjectNo(c.getKey().getProjectNo());
                            foucsTable(row, 5);
                        }
                    }
                    case 5 -> {
                        if (value instanceof Currency c) {
                            op.setCurCode(c.getCurCode());
                            foucsTable(row, 6);
                        }
                    }
                    case 6 ->
                        op.setClAmt(Util1.getDouble(value));

                }
                save(op, row, column);
                fireTableRowsUpdated(row, row);
                parent.requestFocus();
            }
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void foucsTable(int row, int colum) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(colum, colum);
    }

    public StockOP getGl(int row) {
        return listGV.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 6) {
            return Double.class;
        }
        return String.class;
    }

    public List<StockOP> getListGV() {
        return listGV;
    }

    public void setListGV(List<StockOP> listGV) {
        this.listGV = listGV;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;

    }

    public void addGV(StockOP op) {
        listGV.add(op);
        fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
    }

    public void setGVGroup(int row, StockOP op) {
        if (!listGV.isEmpty()) {
            listGV.set(row, op);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public int getListSize() {
        return listGV.size();
    }

    public void delete(int row) {
        if (!listGV.isEmpty()) {
            listGV.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    private void save(StockOP op, int row, int column) {
        op.setUpdatedDate(LocalDateTime.now());
        if (isValidEntry(op, column)) {
            progress.setIndeterminate(true);
            if (op.getKey().getTranCode() == null) {
                op.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                op.setUpdatedBy(Global.loginUser.getUserCode());
            }
            accountRepo.save(op).subscribe((t) -> {
                if (t != null) {
                    listGV.set(row, t);
                    addNewRow();
                }
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(parent, e.getMessage());
            });

        }
    }

    private boolean isValidEntry(StockOP op, int column) {
        if (op.getTranDate() == null && column > 0) {
            JOptionPane.showMessageDialog(parent, "Invalid Closing Date.");
            return false;
        } else if (op.getDeptCode() == null && column > 1) {
            JOptionPane.showMessageDialog(parent, "Invalid Department.");
            return false;
        } else if (op.getCoaCode() == null && column > 3) {
            JOptionPane.showMessageDialog(parent, "Invalid COA.");
            return false;
        } else if (op.getCurCode() == null && column > 4) {
            JOptionPane.showMessageDialog(parent, "Invalid Currency.");
            return false;
        } else if (Util1.getDouble(op.getClAmt()) <= 0) {
            return false;
        }
        return true;
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listGV.isEmpty() || listGV == null) {
            status = true;
        } else {
            StockOP op = listGV.get(listGV.size() - 1);
            if (op.getKey().getTranCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            StockOP op = new StockOP();
            StockOPKey key = new StockOPKey();
            key.setCompCode(Global.compCode);
            key.setDeptId(Global.deptId);
            op.setKey(key);
            op.setTranDate(Util1.getTodayDate());
            op.setCurCode(Global.currency);
            if (department != null) {
                op.setDeptUsrCode(department.getUserCode());
                op.setDeptCode(department.getKey().getDeptCode());
            }
            listGV.add(op);
            fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);

        }
        progress.setIndeterminate(false);
    }

}
