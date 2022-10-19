/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.Department;
import com.acc.model.Gl;
import com.acc.model.TraderA;
import com.acc.model.VCOALv3;
import com.acc.model.VDescription;
import com.acc.model.VGl;
import com.acc.model.VRef;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.user.model.Currency;
import java.awt.HeadlessException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author winswe
 */
public class AllCashTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(AllCashTableModel.class);
    private List<VGl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dept:", "Description", "Ref :", "No :", "Person", "Account", "Curr", "Cash In / Dr", "Cash Out / Cr"};
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private String sourceAccId;
    private JTable parent;
    private SelectionObserver selectionObserver;
    private TraderA trader;
    private DateAutoCompleter dateAutoCompleter;
    private String glDate;
    private Currency currency;
    private Department department;
    private AccountRepo accountRepo;

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
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

    public String getGlDate() {
        return glDate;
    }

    public void setGlDate(String glDate) {
        this.glDate = glDate;
    }

    public DateAutoCompleter getDateAutoCompleter() {
        return dateAutoCompleter;
    }

    public void setDateAutoCompleter(DateAutoCompleter dateAutoCompleter) {
        this.dateAutoCompleter = dateAutoCompleter;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
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
        VGl vgl = listVGl.get(row);
        return Util1.isNull(vgl.getTranSource(), "CB").equals("CB");
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 8 ->
                Double.class;
            case 9 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            if (!listVGl.isEmpty()) {
                VGl vgi = listVGl.get(row);
                switch (column) {
                    case 0 -> {
                        //Id
                        return Util1.toDateStr(vgi.getGlDate(), "dd/MM/yyyy");
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
                        //Person
                        return vgi.getTraderName();
                    }
                    case 6 -> {
                        //Account
                        return vgi.getGlCode() != null ? Util1.isNull(vgi.getAccName(), "* Journal *") : vgi.getAccName();
                    }
                    case 7 -> {
                        return vgi.getCurCode();
                    }
                    case 8 -> {
                        if (vgi.getDrAmt() != null) {
                            if (vgi.getDrAmt() == 0) {
                                return null;
                            } else {
                                return vgi.getDrAmt();
                            }
                        } else {
                            return vgi.getDrAmt();
                        }
                    }
                    case 9 -> {
                        if (vgi.getCrAmt() != null) {
                            if (vgi.getCrAmt() == 0) {
                                return null;
                            } else {
                                return vgi.getCrAmt();
                            }
                        } else {
                            return vgi.getCrAmt();
                        }
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
            VGl vgl = listVGl.get(row);
            switch (column) {
                case 0 -> {
                    if (value != null) {
                        if (Util1.isValidDateFormat(value.toString(), "dd/MM/yyyy")) {
                            vgl.setGlDate(Util1.toDate(value, "dd/MM/yyyy"));
                        } else {
                            if (value.toString().length() == 8) {
                                String toFormatDate = Util1.toFormatDate(value.toString());
                                vgl.setGlDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                            } else {
                                vgl.setGlDate(Util1.getTodayDate());
                                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date");
                            }
                        }
                    }
                    parent.setColumnSelectionInterval(1, 1);
                }
                case 1 -> {
                    if (value instanceof Department dep) {
                        vgl.setDeptUsrCode(dep.getUserCode());
                        vgl.setDeptCode(dep.getKey().getDeptCode());
                    }
                    parent.setColumnSelectionInterval(2, 2);
                }
                case 2 -> {
                    if (value != null) {
                        if (value instanceof VDescription autoText) {
                            vgl.setDescription(autoText.getDescription());
                        } else {
                            vgl.setDescription(value.toString());
                        }

                    }
                    parent.setColumnSelectionInterval(3, 3);
                }
                case 3 -> {
                    if (value != null) {
                        if (value instanceof VRef autoText) {
                            vgl.setReference(autoText.getReference());
                        } else {
                            vgl.setReference(value.toString());
                        }

                    }
                    parent.setColumnSelectionInterval(4, 4);
                }
                case 4 -> {
                    if (value != null) {
                        vgl.setRefNo(value.toString());
                    }
                }
                case 5 -> {
                    if (value != null) {
                        if (value instanceof TraderA trader1) {
                            trader = trader1;
                        }
                        if (trader != null) {
                            vgl.setTraderCode(trader.getCode());
                            vgl.setTraderName(trader.getTraderName());
                            if (trader.getAccCode() != null) {
                                vgl.setAccCode(trader.getAccCode());
                                vgl.setAccName(trader.getAccCode());
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
                case 6 -> {
                    if (value != null) {
                        if (value instanceof VCOALv3 coa) {
                            if (!coa.getCoaCode().equals(sourceAccId)) {
                                vgl.setAccCode(coa.getCoaCode());
                                vgl.setAccName(coa.getCoaNameEng());
                                vgl.setTraderCode(null);
                                vgl.setTraderName(null);
                                if (Util1.isNull(vgl.getCurCode())) {
                                    parent.setColumnSelectionInterval(7, 7);
                                } else {
                                    parent.setColumnSelectionInterval(8, 8);
                                }
                            }
                        }

                    }
                }
                case 7 -> {
                    if (value != null) {
                        if (value instanceof Currency curr) {
                            String cuCode = curr.getCurCode();
                            vgl.setCurCode(cuCode);
                        }
                    }
                    parent.setColumnSelectionInterval(7, 7);
                }
                case 8 -> {
                    vgl.setDrAmt(Util1.getDouble(value));
                    vgl.setCrAmt(null);
                }
                case 9 -> {
                    vgl.setCrAmt(Util1.getDouble(value));
                    vgl.setDrAmt(null);
                }
            }
            save(vgl, row, column);
            parent.requestFocus();

        } catch (HeadlessException e) {
            log.info("setValueAt : " + e.getMessage());
        }
    }

    private void save(VGl vgl, int row, int column) {
        if (isValidEntry(vgl, row, column)) {
            try {
                String strVGL = gson.toJson(vgl);
                Gl gl = gson.fromJson(strVGL, Gl.class);
                gl.setSourceAcId(sourceAccId);
                if (Util1.isNull(gl.getGlCode())) {
                    gl.setCompCode(Global.compCode);
                    gl.setCreatedBy(Global.loginUser.getUserCode());
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setTranSource("CB");
                    gl.setMacId(Global.macId);
                } else {
                    gl.setModifyBy(Global.loginUser.getUserCode());
                }
                Gl glSave = accountRepo.saveGl(gl);
                if (glSave != null) {
                    VGl saveVGl = listVGl.get(row);
                    saveVGl.setGlCode(glSave.getGlCode());
                    saveVGl.setCreatedBy(glSave.getCreatedBy());
                    saveVGl.setModifyBy(glSave.getModifyBy());
                    saveVGl.setCompCode(glSave.getCompCode());
                    saveVGl.setCreatedDate(glSave.getCreatedDate());
                    saveVGl.setTranSource(gl.getTranSource());
                    addNewRow();
                    row = parent.getSelectedRow();
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(0, 0);
                    selectionObserver.selected("CAL-TOTAL", "-");
                    //send to inventory
                    //sendPaymentToInv(glSave);
                }
            } catch (JsonSyntaxException ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                log.error("Save Gl :" + ex.getMessage());
            }
        }
    }

    private boolean isValidEntry(VGl vgl, int row, int column) {
        boolean status = true;
        if (Util1.isNull(vgl.getAccCode())) {
            status = false;
            if (column > 6) {
                JOptionPane.showMessageDialog(Global.parentForm, "Account missing.");
                parent.setColumnSelectionInterval(6, 6);
                parent.setRowSelectionInterval(row, row);
            }
        } else if (Util1.getDouble(vgl.getDrAmt()) + Util1.getDouble(vgl.getCrAmt()) <= 0) {
            status = false;
        } else if (Util1.isNull(vgl.getDeptCode())) {
            status = false;
            if (column > 1) {
                JOptionPane.showMessageDialog(Global.parentForm, "Missing Department.");
                parent.setColumnSelectionInterval(1, 1);
                parent.setRowSelectionInterval(row, row);
            }
        } else if (!Util1.isNull(vgl.getGlCode())) {
            int yn = JOptionPane.showConfirmDialog(Global.parentForm,
                    "Are you sure to edit?", "Edit",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            status = yn == JOptionPane.YES_OPTION;
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

    public List<VGl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<VGl> listVGl) {
        this.listVGl = listVGl;
        fireTableDataChanged();
    }

    public VGl getVGl(int row) {
        return listVGl.get(row);
    }

    public void deleteVGl(int row) {
        if (!listVGl.isEmpty()) {
            listVGl.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void addVGl(VGl vgi) {
        listVGl.add(vgi);
        fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setVGl(int row, VGl vgi) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, vgi);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            VGl vGl = new VGl();
            if (ProUtil.getProperty(sourceAccId) != null) {
                vGl.setCurCode(ProUtil.getProperty(sourceAccId));
            } else {
                if (currency != null) {
                    vGl.setCurCode(currency.getCurCode());
                }
            }
            if (department != null) {
                vGl.setDeptCode(department.getKey().getDeptCode());
                vGl.setDeptUsrCode(department.getUserCode());
            }
            vGl.setGlDate(glDate == null ? Util1.getTodayDate() : Util1.toDate(glDate, "dd/MM/yyyy"));
            listVGl.add(vGl);
            fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listVGl.isEmpty() || listVGl == null) {
            status = true;
        } else {
            VGl vgl = listVGl.get(listVGl.size() - 1);
            if (vgl.getGlCode() == null) {
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

    public void copyRow() {
        try {
            int selectRow = parent.convertRowIndexToModel(parent.getSelectedRow());
            int column = parent.getSelectedColumn();
            if (listVGl != null) {
                VGl vgl = listVGl.get(selectRow - 1);
                if (vgl.getGlCode() != null) {
                    Date glDate = vgl.getGlDate();
                    VGl selGL = listVGl.get(selectRow);
                    switch (column) {
                        case 0 ->
                            selGL.setGlDate(glDate);
                        case 1 -> {
                            selGL.setDeptCode(vgl.getDeptCode());
                            selGL.setDeptName(vgl.getDeptName());
                            selGL.setDeptUsrCode(vgl.getDeptUsrCode());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 2);
                        }
                        case 2 -> {
                            selGL.setDescription(vgl.getDescription());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 3);
                        }
                        case 3 -> {
                            selGL.setReference(vgl.getReference());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 4);
                        }
                        case 4 -> {
                            selGL.setRefNo(vgl.getRefNo());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 5);
                        }
                        case 5 -> {
                            selGL.setTraderCode(vgl.getTraderCode());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 7);
                        }
                        case 6 -> {
                            selGL.setAccCode(vgl.getAccCode());
                            selGL.setAccName(vgl.getAccName());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 7);
                        }
                        case 7 -> {
                            selGL.setCurCode(vgl.getCurCode());
                            fireTableCellUpdated(selectRow, column);
                            selectTable(selectRow, 8);
                        }
                        case 8 -> {
                            selGL.setDrAmt(vgl.getDrAmt());
                            fireTableCellUpdated(selectRow, column);
                        }
                        case 9 -> {
                            selGL.setDrAmt(vgl.getDrAmt());
                            fireTableCellUpdated(selectRow, column);
                        }
                    }
                    if (parent.getCellEditor() != null) {
                        parent.getCellEditor().stopCellEditing();
                    }
                }
            }
        } catch (Exception e) {
            log.error("copyRow " + e.getMessage());
        }
    }

    private void selectTable(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
        }
    }
}
