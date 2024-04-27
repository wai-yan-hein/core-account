/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.repo.AccountRepo;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.acc.model.TraderA;
import com.acc.model.VDescription;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.UndoItem;
import com.common.UndoStack;
import com.common.Util1;
import com.common.YNOptionPane;
import com.user.model.Currency;
import com.user.model.Project;
import java.awt.HeadlessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author wai yan
 */
@Slf4j
public class AllCashTableModel extends AbstractTableModel {

    private List<Gl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref :", "No :", "Batch No", "Project No", "Person", "Account", "Curr", "Cash In / Dr", "Cash Out / Cr"};
    private String sourceAccId;
    private JTable parent;
    private SelectionObserver observer;
    private String glDate;
    private String curCode;
    private AccountRepo accountRepo;
    private JProgressBar progress;
    private UndoStack undo = new UndoStack();
    private double drAmt;
    private double crAmt;
    private int size;

    public double getDrAmt() {
        return drAmt;
    }

    public double getCrAmt() {
        return crAmt;
    }

    public int getSize() {
        return size;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setGlDate(String glDate) {
        this.glDate = glDate;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        Gl gl = listVGl.get(row);
        if (gl.isTranLock()) {
            return false;
        }
        if (column == 1) {
            if (gl.getKey().getGlCode() != null) {
                return !ProUtil.isDisableDep();
            }
        }
        return Util1.isNull(gl.getTranSource(), "CB").equals("CB");
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 10 ->
                Double.class;
            case 11 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            if (!listVGl.isEmpty()) {
                Gl vgi = listVGl.get(row);
                switch (column) {
                    case 0 -> {
                        if (vgi.getGlDate() == null) {
                            return Util1.toDateStr(LocalDateTime.now(), Global.dateFormat);
                        }
                        //Id
                        return Util1.toDateStr(vgi.getGlDate(), Global.dateFormat);
                    }
                    case 1 -> {
                        //Department
                        return vgi.getDeptUsrCode();
                    }
                    case 2 -> {
                        //Desp
                        return vgi.getDescription();
                    }
                    case 3 -> {
                        //Ref
                        return vgi.getReference();
                    }
                    case 4 -> {
                        return Util1.isNull(vgi.getGlVouNo(), vgi.getRefNo());
                    }
                    case 5 -> {
                        return vgi.getBatchNo();
                    }
                    case 6 -> {
                        return vgi.getProjectNo();
                    }
                    case 7 -> {
                        //Person
                        return vgi.getTraderName();
                    }
                    case 8 -> {
                        //Account
                        return vgi.getKey().getGlCode() != null ? Util1.isNull(vgi.getAccName(), "* Journal *") : vgi.getAccName();
                    }
                    case 9 -> {
                        return vgi.getCurCode();
                    }
                    case 10 -> {
                        return Util1.toNull(vgi.getDrAmt());
                    }
                    case 11 -> {
                        return Util1.toNull(vgi.getCrAmt());
                    }
                    default -> {
                        return null;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            Gl gl = listVGl.get(row);
            undo.addUndo(new UndoItem(row, gl.clone()));
            switch (column) {
                case 0 -> {
                    if (value != null) {
                        gl.setGlDate(Util1.formatLocalDateTime(value.toString()));
                    }
                    parent.setColumnSelectionInterval(1, 1);
                }
                case 1 -> {
                    if (value instanceof DepartmentA dep) {
                        gl.setDeptUsrCode(dep.getUserCode());
                        gl.setDeptCode(dep.getKey().getDeptCode());
                    }
                    parent.setColumnSelectionInterval(2, 2);
                }
                case 2 -> {
                    if (value != null) {
                        if (value instanceof VDescription autoText) {
                            gl.setDescription(autoText.getDescription());
                        } else {
                            gl.setDescription(Util1.getString(value));
                        }

                    }
                    parent.setColumnSelectionInterval(3, 3);
                }
                case 3 -> {
                    if (value != null) {
                        if (value instanceof VDescription autoText) {
                            gl.setReference(autoText.getDescription());
                        } else {
                            gl.setReference(Util1.getString(value));
                        }
                        parent.setColumnSelectionInterval(4, 4);
                    }
                }
                case 4 -> {
                    if (value != null) {
                        gl.setRefNo(Util1.getString(value));
                        parent.setColumnSelectionInterval(5, 5);
                    }
                }
                case 5 -> {
                    if (value instanceof VDescription v) {
                        gl.setBatchNo(v.getDescription());
                        parent.setColumnSelectionInterval(6, 6);
                    }
                }
                case 6 -> {
                    if (value instanceof Project p) {
                        gl.setProjectNo(p.getKey().getProjectNo());
                        parent.setColumnSelectionInterval(7, 7);
                    }
                }
                case 7 -> {
                    if (value != null) {
                        if (value instanceof TraderA t) {
                            gl.setTraderCode(t.getKey().getCode());
                            gl.setTraderName(t.getTraderName());
                            if (t.getAccount() != null) {
                                gl.setAccCode(t.getAccount());
                                accountRepo.findCOA(t.getAccount()).doOnSuccess((c) -> {
                                    if (c != null) {
                                        gl.setAccName(c.getCoaNameEng());
                                    }
                                }).subscribe();
                                if (ProUtil.isMultiCur()) {
                                    parent.setColumnSelectionInterval(7, 7);
                                } else {
                                    parent.setColumnSelectionInterval(8, 8);
                                }
                            } else {
                                parent.setColumnSelectionInterval(5, 5);
                            }
                        } else {
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                }
                case 8 -> {
                    if (value != null) {
                        if (value instanceof ChartOfAccount coa) {
                            if (!ProUtil.isTraderCOA()) {
                                gl.setTraderCode(null);
                                gl.setTraderName(null);
                            }
                            gl.setAccCode(coa.getKey().getCoaCode());
                            gl.setAccName(coa.getCoaNameEng());
                            if (Util1.isNull(gl.getCurCode())) {
                                parent.setColumnSelectionInterval(7, 7);
                            } else {
                                parent.setColumnSelectionInterval(8, 8);
                            }
                        }

                    }
                }
                case 9 -> {
                    if (value != null) {
                        if (value instanceof Currency curr) {
                            String cuCode = curr.getCurCode();
                            gl.setCurCode(cuCode);
                        }
                    }
                    parent.setColumnSelectionInterval(7, 7);
                }
                case 10 -> {
                    gl.setDrAmt(Util1.getDouble(value));
                    gl.setCrAmt(0);
                }
                case 11 -> {
                    gl.setCrAmt(Util1.getDouble(value));
                    gl.setDrAmt(0);
                }
            }
            if (canEdit(gl, row)) {
                save(gl, row, column);
            }
            parent.requestFocus();

        } catch (HeadlessException e) {
            log.info("setValueAt : " + e.getMessage());
        }
    }

    private boolean canEdit(Gl gl, int row) {
        if (!Util1.isNull(gl.getKey().getGlCode())) {
            YNOptionPane optionPane = new YNOptionPane("Are you sure to edit?", JOptionPane.QUESTION_MESSAGE);
            JDialog dialog = optionPane.createDialog("Edit");
            dialog.setVisible(true);
            int yn = (int) optionPane.getValue();
            if (yn == JOptionPane.NO_OPTION) {
                UndoItem item = undo.undo();
                if (item != null && item.getOldValue() instanceof Gl) {
                    Gl undoGl = (Gl) item.getOldValue();
                    listVGl.set(row, undoGl);
                    fireTableRowsUpdated(row, row);
                    return false;
                }
            }
            gl.setModifyBy(Global.loginUser.getUserCode());
            return yn == JOptionPane.YES_OPTION;
        }

        // If glCode is null, return true
        return true;
    }

    private void save(Gl gl, int row, int column) {
        if (isValidEntry(gl, row, column)) {
            if (DateLockUtil.isLockDate(gl.getGlDate())) {
                DateLockUtil.showMessage(parent);
                ComponentUtil.scrollTable(parent, row, 0);
                return;
            }
            progress.setIndeterminate(true);
            accountRepo.save(gl).doOnSuccess((t) -> {
                if (t != null) {
                    listVGl.set(row, t);
                    observer.selected("CAL-TOTAL", "-");
                    addNewRow();
                    ComponentUtil.scrollTable(parent, row + 1, 0);
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(parent, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        save(gl, row, column);
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.scrollRectToVisible(parent.getCellRect(row, column, true));
        parent.requestFocus();
    }

    private boolean isValidEntry(Gl gl, int row, int column) {
        boolean status = true;
        if (Util1.isNull(gl.getAccCode(), "-").equals(sourceAccId)) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Account is the same with Source Account.");
            gl.setAccCode(null);
            gl.setAccName(null);
            parent.setColumnSelectionInterval(8, 8);
            parent.setRowSelectionInterval(row, row);
        } else if (Util1.isNull(gl.getAccCode())) {
            status = false;
            if (column > 8) {
                JOptionPane.showMessageDialog(Global.parentForm, "Account missing.");
                parent.setColumnSelectionInterval(8, 8);
                parent.setRowSelectionInterval(row, row);
            }
        } else if (Util1.isNull(gl.getCurCode())) {
            status = false;
            if (column > 9) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Currency.");
                parent.setColumnSelectionInterval(9, 9);
                parent.setRowSelectionInterval(row, row);
            }
        } else if (Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt()) <= 0) {
            status = false;
        } else if (Util1.isNull(gl.getDeptCode())) {
            status = false;
            if (column > 1) {
                JOptionPane.showMessageDialog(Global.parentForm, "Missing Department.");
                parent.setColumnSelectionInterval(1, 1);
                parent.setRowSelectionInterval(row, row);
            }
        } else if (gl.getSrcAccCode().equals(gl.getAccCode())) {
            JOptionPane.showMessageDialog(parent, "Invalid Account.");
            status = false;
        } else if (!Util1.isDateBetween(gl.getGlDate())) {
            JOptionPane.showMessageDialog(Global.parentForm, "Date must be between financial period.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            parent.setColumnSelectionInterval(0, 0);
            parent.setRowSelectionInterval(row, row);
            status = false;
        } else {
            gl.setCreatedBy(Global.loginUser.getUserCode());
        }
        return status;
    }

    @Override
    public int getRowCount() {
        return listVGl.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Gl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<Gl> listVGl) {
        this.listVGl = listVGl;
        fireTableDataChanged();
    }

    public Gl getVGl(int row) {
        return listVGl.get(row);
    }

    public void deleteVGl(int row) {
        if (!listVGl.isEmpty()) {
            listVGl.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void addVGl(Gl vgi) {
        listVGl.add(vgi);
    }

    public void setVGl(int row, Gl vgi) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, vgi);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            Gl gl = new Gl();
            GlKey key = new GlKey();
            key.setDeptId(Global.deptId);
            key.setCompCode(Global.compCode);
            gl.setKey(key);
            gl.setMacId(Global.macId);
            gl.setTranSource("CB");
            accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
                if (t != null) {
                    gl.setDeptCode(t.getKey().getDeptCode());
                    gl.setDeptUsrCode(t.getUserCode());
                }
                gl.setGlDate(glDate == null ? LocalDateTime.now() : Util1.toDate(glDate));
                gl.setSrcAccCode(sourceAccId);
                gl.setCurCode(curCode);
                listVGl.add(gl);
                fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
            }).subscribe();
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listVGl.isEmpty() || listVGl == null) {
            status = true;
        } else {
            Gl gl = listVGl.get(listVGl.size() - 1);
            if (gl.getKey().getGlCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public int getListSize() {
        return listVGl.size();
    }

    public void setColumnName(int i, String name) {
        columnNames[i] = name;
        fireTableStructureChanged();
    }

    public void addObject(Gl gl) {
        listVGl.add(gl);
        drAmt += gl.getDrAmt();
        crAmt += gl.getCrAmt();
        size += 1;
        int lastIndex = listVGl.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        if (listVGl != null) {
            drAmt = 0;
            crAmt = 0;
            size = 0;
            listVGl.clear();
            fireTableDataChanged();
        }
    }
}
