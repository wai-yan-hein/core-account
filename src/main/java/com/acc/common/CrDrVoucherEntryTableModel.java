/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
import com.acc.model.Department;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.acc.model.TraderA;
import com.common.Global;
import com.common.ProUtil;
import com.common.Util1;
import com.user.model.Currency;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author MyoGyi
 */
public class CrDrVoucherEntryTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CrDrVoucherEntryTableModel.class);
    private List<Gl> listVGl = new ArrayList();
    String[] columnNames = {"Dep:", "Description", "Person", "Account", "Currency", "Amount"};
    private JTable parent;
    private JFormattedTextField ttlAmt;
    private String vouType;
    private List<GlKey> delList = new ArrayList<>();
    private boolean change = false;
    private AccountRepo accountRepo;
    private Department department;
    private boolean edit = false;

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public List<GlKey> getDelList() {
        return delList;
    }

    public void setDelList(List<GlKey> delList) {
        this.delList = delList;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    public JFormattedTextField getTtlAmt() {
        return ttlAmt;
    }

    public void setTtlAmt(JFormattedTextField ttlAmt) {
        this.ttlAmt = ttlAmt;
    }

    @Override
    public int getRowCount() {
        return listVGl.size();
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
            Gl gl = listVGl.get(row);
            return switch (column) {
                case 0 ->
                    gl.getDeptUsrCode();
                case 1 ->
                    gl.getDescription();
                case 2 ->
                    gl.getTraderName();
                case 3 ->
                    gl.getAccName();
                case 4 ->
                    gl.getCurCode();
                case 5 ->
                    vouType.equals("CR") ? gl.getDrAmt() : gl.getCrAmt();
                default ->
                    null;
            }; //dep
            //des
            //Ref
            //Desp
            //Dr
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (value != null) {
                Gl gl = listVGl.get(row);
                switch (column) {
                    case 0 -> {
                        if (value instanceof Department dep) {
                            gl.setDeptCode(dep.getKey().getDeptCode());
                            gl.setDeptUsrCode(dep.getUserCode());
                        }
                        parent.setColumnSelectionInterval(1, 1);
                    }
                    case 1 -> {
                        if (value != null) {
                            gl.setDescription(String.valueOf(value));
                            parent.setColumnSelectionInterval(1, 2);
                        }
                    }
                    case 2 -> {
                        if (value != null) {
                            if (value instanceof TraderA t) {
                                gl.setTraderCode(t.getKey().getCode());
                                gl.setTraderName(t.getTraderName());
                                if (t.getAccount() != null) {
                                    gl.setAccCode(t.getAccount());
                                    accountRepo.findCOA(t.getAccount()).subscribe((coa) -> {
                                        if (coa != null) {
                                            gl.setAccName(coa.getCoaNameEng());
                                        }
                                    });
                                    if (ProUtil.isMultiCur()) {
                                        parent.setColumnSelectionInterval(4, 4);
                                    } else {
                                        parent.setColumnSelectionInterval(5, 5);
                                    }
                                } else {
                                    parent.setColumnSelectionInterval(5, 5);
                                }
                            }
                        }
                    }
                    case 3 -> {
                        if (value != null) {
                            if (value instanceof ChartOfAccount coa) {
                                gl.setAccCode(coa.getKey().getCoaCode());
                                gl.setAccName(coa.getCoaNameEng());
                                parent.setColumnSelectionInterval(5, 5);
                            }
                        }
                    }
                    case 4 -> {
                        if (value instanceof Currency cur) {
                            gl.setCurCode(cur.getCurCode());
                        }
                        parent.setColumnSelectionInterval(5, 5);
                    }
                    case 5 -> {
                        if (vouType.equals("CR")) {
                            gl.setDrAmt(Util1.getDouble(value));
                        } else {
                            gl.setCrAmt(Util1.getDouble(value));
                        }
                        calAmount();
                    }

                }
                addNewRow(gl, row, column);
                change = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public Gl getVGl(int row) {
        return listVGl.get(row);
    }

    private void addNewRow(Gl gl, int row, int column) {
        if (isValidEntry(gl, column)) {
            if (hasEmptyRow()) {
                Gl up = listVGl.get(row);
                Gl vGl = new Gl();
                GlKey key = new GlKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                vGl.setKey(key);
                vGl.setDeptCode(up.getDeptCode());
                vGl.setDeptUsrCode(up.getDeptUsrCode());
                vGl.setCurCode(up.getCurCode());
                vGl.setDescription(up.getDescription());
                listVGl.add(vGl);
                fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
                parent.setRowSelectionInterval(row + 1, row + 1);
                parent.setColumnSelectionInterval(0, 0);
            }
        }
    }

    private boolean isValidEntry(Gl gl, int column) {
        boolean status = true;
        if (column == 5) {
            if (Util1.isNull(gl.getAccCode())) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Account.");
                status = false;
                parent.setColumnSelectionInterval(3, 3);
                parent.requestFocus();
            }
        } else if (Util1.getFloat(gl.getDrAmt()) + Util1.getFloat(gl.getCrAmt()) == 0) {
            status = false;
        }
        return status;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 5 ->
                Double.class;
            default ->
                String.class;
        };
    }

    public List<Gl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<Gl> listVGl) {
        this.listVGl = listVGl;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 4 -> {
                return ProUtil.isMultiCur();
            }
        }
        return true;

    }

    public Gl getChartOfAccount(int row) {
        return listVGl.get(row);
    }

    public void addGV(Gl cd) {
        listVGl.add(cd);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void remove(int row) {
        listVGl.remove(row);
        fireTableRowsDeleted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setGVGroup(int row, Gl cd) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, cd);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            Gl gl = new Gl();
            GlKey key = new GlKey();
            key.setCompCode(Global.compCode);
            key.setDeptId(Global.deptId);
            gl.setKey(key);
            if (department != null) {
                gl.setDeptCode(department.getKey().getDeptCode());
                gl.setDeptUsrCode(department.getUserCode());
            }
            gl.setCurCode(Global.currency);
            listVGl.add(gl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
        }

    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listVGl.isEmpty() || listVGl == null) {
            status = true;
        } else {
            Gl gl = listVGl.get(listVGl.size() - 1);
            if (gl.getAccCode() == null) {
                status = false;
            }
        }
        return status;
    }

    public int getListSize() {
        return listVGl.size();
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
            addEmptyRow();
            fireTableDataChanged();
        }
    }

    public void calAmount() {
        if (!listVGl.isEmpty()) {
            double amt = 0.0;
            if (vouType.equals("CR")) {
                for (Gl gl : listVGl) {
                    amt += Util1.getDouble(gl.getDrAmt());
                }
            } else {
                for (Gl gl : listVGl) {
                    amt += Util1.getDouble(gl.getCrAmt());
                }
            }
            ttlAmt.setValue(amt);
        }
    }

    public void removeJournal(int row) {
        if (!listVGl.isEmpty()) {
            Gl gl = listVGl.get(row);
            if (!Util1.isNull(gl.getKey().getGlCode())) {
                int status = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete transaction.", "Delete Transaction", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (status == JOptionPane.YES_OPTION) {
                    delList.add(gl.getKey());
                    listVGl.remove(row);
                    fireTableRowsDeleted(row, row);
                    calAmount();
                    parent.setRowSelectionInterval(row, row);
                    parent.requestFocus();
                }
            }
        }
    }
}
