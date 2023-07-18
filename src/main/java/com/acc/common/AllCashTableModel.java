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
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wai yan
 */
public class AllCashTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(AllCashTableModel.class);
    private List<Gl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref :", "No :", "Batch No", "Project No", "Person", "Account", "Curr", "Cash In / Dr", "Cash Out / Cr"};
    private String sourceAccId;
    private JTable parent;
    private SelectionObserver observer;
    private String glDate;
    private String curCode;
    private DepartmentA department;
    private AccountRepo accountRepo;
    private JProgressBar progress;

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setDepartment(DepartmentA department) {
        this.department = department;
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
                        //Ref no
                        return vgi.getRefNo();
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
                        return Util1.getDouble(vgi.getDrAmt()) == 0 ? null : vgi.getDrAmt();
                    }
                    case 11 -> {
                        return Util1.getDouble(vgi.getCrAmt()) == 0 ? null : vgi.getCrAmt();
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
                                accountRepo.findCOA(t.getAccount()).subscribe((c) -> {
                                    if (c != null) {
                                        gl.setAccName(c.getCoaNameEng());
                                    }
                                });
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
                            if (!coa.getKey().getCoaCode().equals(sourceAccId)) {
                                gl.setAccCode(coa.getKey().getCoaCode());
                                gl.setAccName(coa.getCoaNameEng());
                                gl.setTraderCode(null);
                                gl.setTraderName(null);
                                if (Util1.isNull(gl.getCurCode())) {
                                    parent.setColumnSelectionInterval(7, 7);
                                } else {
                                    parent.setColumnSelectionInterval(8, 8);
                                }
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
                    gl.setCrAmt(null);
                }
                case 11 -> {
                    gl.setCrAmt(Util1.getDouble(value));
                    gl.setDrAmt(null);
                }
            }
            save(gl, row, column);
            parent.requestFocus();

        } catch (HeadlessException e) {
            log.info("setValueAt : " + e.getMessage());
        }
    }

    private void save(Gl gl, int row, int column) {
        if (isValidEntry(gl, row, column)) {
            progress.setIndeterminate(true);
            accountRepo.save(gl).subscribe((t) -> {
                if (t != null) {
                    listVGl.set(row, t);
                    addNewRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(0, 0);
                    observer.selected("CAL-TOTAL", "-");
                }
            }, (err) -> {
                JOptionPane.showMessageDialog(parent, err.getMessage());
                progress.setIndeterminate(false);
            });
        }
    }

    private boolean isValidEntry(Gl gl, int row, int column) {
        boolean status = true;
        if (Util1.isNull(gl.getAccCode())) {
            status = false;
            if (column > 6) {
                JOptionPane.showMessageDialog(Global.parentForm, "Account missing.");
                parent.setColumnSelectionInterval(6, 6);
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
        } else if (!Util1.isNull(gl.getKey().getGlCode())) {
            int yn = JOptionPane.showConfirmDialog(Global.parentForm,
                    "Are you sure to edit?", "Edit",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            gl.setModifyBy(Global.loginUser.getUserCode());
            status = yn == JOptionPane.YES_OPTION;
        } else if (gl.getSrcAccCode().equals(gl.getAccCode())) {
            JOptionPane.showMessageDialog(parent, "Invalid Account.");
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
            if (department != null) {
                gl.setDeptCode(department.getKey().getDeptCode());
                gl.setDeptUsrCode(department.getUserCode());
            }
            gl.setGlDate(glDate == null ? LocalDateTime.now() : Util1.toDate(glDate));
            gl.setSrcAccCode(sourceAccId);
            gl.setCurCode(curCode);
            listVGl.add(gl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
        }
        progress.setIndeterminate(false);
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

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
            fireTableDataChanged();
        }
    }
}
