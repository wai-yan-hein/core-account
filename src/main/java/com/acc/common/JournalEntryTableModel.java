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
import com.common.Global;
import com.common.Util1;
import com.inventory.model.Trader;
import com.user.model.Currency;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author MyoGyi
 */
@Slf4j
public class JournalEntryTableModel extends AbstractTableModel {

    private List<Gl> listGV = new ArrayList();
    String[] columnNames = {"Dept:", "Descripiton", "Cus / Sup", "Account", "Currency", "Dr-Amt", "Cr-Amt"};
    private JTable parent;
    private List<GlKey> delList = new ArrayList<>();
    JFormattedTextField ttlCrdAmt;
    JFormattedTextField ttlDrAmt;
    private JTextField txtRef;
    private boolean change = false;

    public List<GlKey> getDelList() {
        return delList;
    }

    public void setDelList(List<GlKey> delList) {
        this.delList = delList;
    }


    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public JTextField getTxtRef() {
        return txtRef;
    }

    public void setTxtRef(JTextField txtRef) {
        this.txtRef = txtRef;
    }

    public JFormattedTextField getTtlCrdAmt() {
        return ttlCrdAmt;
    }

    public void setTtlCrdAmt(JFormattedTextField ttlCrdAmt) {
        this.ttlCrdAmt = ttlCrdAmt;
    }

    public JFormattedTextField getTtlDrAmt() {
        return ttlDrAmt;
    }

    public void setTtlDrAmt(JFormattedTextField ttlDrAmt) {
        this.ttlDrAmt = ttlDrAmt;
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
            Gl gv = listGV.get(row);

            return switch (column) {
                case 0 ->
                    gv.getDeptUsrCode();
                case 1 ->
                    gv.getDescription();
                case 2 ->
                    gv.getTraderName();
                case 3 ->
                    gv.getSrcAccName();
                case 4 ->
                    gv.getCurCode();
                case 5 ->
                    Util1.getDouble(gv.getDrAmt()) == 0 ? null : gv.getDrAmt();
                case 6 ->
                    Util1.getDouble(gv.getCrAmt()) == 0 ? null : gv.getCrAmt();
                default ->
                    null;
            }; //Deapart Id
            //Des
            //Cus Id
            //accc
            //dr
            //cr
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (value != null) {
                Gl gv = listGV.get(row);
                switch (column) {
                    case 0 -> {
                        if (value != null) {
                            if (value instanceof Department dep) {
                                gv.setDeptUsrCode(dep.getUserCode());
                                gv.setDeptCode(dep.getKey().getDeptCode());
                                parent.setColumnSelectionInterval(1, 1);
                            }
                        }
                    }
                    case 1 -> {
                        if (value != null) {
                            gv.setDescription(value.toString());
                        }
                        parent.setColumnSelectionInterval(2, 2);
                    }
                    case 2 -> {
                        if (value != null) {
                            if (value instanceof Trader trader) {
                                gv.setTraderCode(trader.getKey().getCode());
                                gv.setTraderName(trader.getTraderName());
                                if (trader.getAccount() != null) {
                                    gv.setSrcAccCode(trader.getAccount());
                                    //gv.setSrcAccName(trader.getAccount().getCoaNameEng());
                                    parent.setColumnSelectionInterval(3, 3);
                                } else {
                                    parent.setColumnSelectionInterval(2, 3);
                                }
                            }
                        }
                    }
                    case 3 -> {
                        if (value != null) {
                            if (value instanceof ChartOfAccount coa) {
                                gv.setSrcAccCode(coa.getKey().getCoaCode());
                                gv.setSrcAccName(coa.getCoaNameEng());
                                gv.setTraderCode(null);
                                gv.setTraderName(null);
                            }
                            parent.setColumnSelectionInterval(4, 4);
                        }
                    }
                    case 4 -> {
                        if (!Objects.isNull(value)) {
                            if (value instanceof Currency cur) {
                                gv.setCurCode(cur.getCurCode());
                                parent.setColumnSelectionInterval(5, 5);
                            }
                        }
                    }

                    case 5 -> {
                        if (value != null) {
                            gv.setDrAmt(Util1.getDouble(value));
                            gv.setCrAmt(0.0);
                            try {
                                Gl get = listGV.get(row + 1);
                                if (!Util1.isNull(get.getDeptCode())) {
                                    parent.setColumnSelectionInterval(1, 1);
                                } else {
                                    parent.setColumnSelectionInterval(0, 0);
                                }
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            } catch (Exception e) {
                                parent.setColumnSelectionInterval(0, 0);
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            }
                        }
                    }
                    case 6 -> {
                        if (value != null) {
                            gv.setCrAmt(Util1.getDouble(value));
                            gv.setDrAmt(0.0);
                            try {
                                Gl get = listGV.get(row + 1);
                                if (!Util1.isNull(get.getDeptCode())) {
                                    parent.setColumnSelectionInterval(1, 1);
                                } else {
                                    parent.setColumnSelectionInterval(0, 0);
                                }
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            } catch (Exception e) {
                                parent.setColumnSelectionInterval(0, 0);
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            }
                        }
                    }
                }
                if (isValidEntry(gv, row, column)) {
                    addEmptyRow();
                }
                if (Util1.isNullOrEmpty(gv.getDescription())) {
                    gv.setDescription(txtRef.getText());
                }
                setCurrency(gv);
                calTotalAmt();
                change();
                parent.requestFocusInWindow();
            }
        } catch (Exception e) {
            log.error(String.format("setValueAt: %s", e));
        }
    }

    public Gl getVGl(int row) {
        return listGV.get(row);
    }

    private void setCurrency(Gl vgl) {
        if (Objects.isNull(vgl.getCurCode())) {
            vgl.setCurCode(null);
        }
    }

    private void change() {
        change = true;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 5 ->
                Double.class;
            case 6 ->
                Double.class;
            default ->
                String.class;
        };
    }

    public List<Gl> getListGV() {
        return listGV;
    }

    public void setListGV(List<Gl> listGV) {
        this.listGV = listGV;
        calTotalAmt();
        fireTableDataChanged();
    }

    public void addListGV(List<Gl> listGV) {
        this.listGV.addAll(0, listGV);
        calTotalAmt();
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;

    }

    public Gl getChartOfAccount(int row) {
        return listGV.get(row);
    }

    public void addGV(Gl gv) {
        listGV.add(gv);
        fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
    }

    public void setGVGroup(int row, Gl gv) {
        if (!listGV.isEmpty()) {
            listGV.set(row, gv);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void saveGV(Gl gv, String status) {
        //if (isValidCOA(gv, Global.compCode, Global.loginUser.getAppUserCode(), status)) {
        // coaService.save(gv);
        if (status.equals("NEW")) {
            listGV.add(new Gl());
            addEmptyRow();
        }

        //}
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listGV.isEmpty() || listGV == null) {
            status = true;
        } else {
            Gl vgl = listGV.get(listGV.size() - 1);
            if (vgl.getSrcAccCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            if (listGV != null) {
                try {
                    Gl record = new Gl();
                    if (!listGV.isEmpty()) {
                        Gl get = listGV.get(listGV.size() - 1);
                        if (!Util1.isNull(get.getDeptCode())) {
                            record.setDeptCode(get.getDeptCode());
                            record.setDeptUsrCode(get.getDeptUsrCode());
                            record.setDescription(get.getDescription());
                        }
                    }
                    listGV.add(record);
                    fireTableRowsInserted(listGV.size() - 1, listGV.size() - 1);
                } catch (Exception e) {
                    log.error(String.format("addEmptyRow: %s", e));
                }
            }
        }

    }

    private boolean isValidEntry(Gl vgl, int row, int column) {
        boolean status = true;
        if (vgl.getSrcAccCode() == null) {
            status = false;
            if (column > 3) {
                JOptionPane.showMessageDialog(Global.parentForm, "Select Account.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(3, 3);
            }
        }
        if (vgl.getDeptCode() == null) {
            status = false;
            if (column > 0) {
                JOptionPane.showMessageDialog(Global.parentForm, "Select Department.");
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(3, 3);
            }
        }
        return status;
    }

    public void clear() {
        if (listGV != null) {
            listGV.clear();
            delList.clear();
            fireTableDataChanged();
        }
    }

    public int getListSize() {
        return listGV.size();
    }

    private void calTotalAmt() {
        double crdAmt = 0.0;
        double drAmt = 0.0;
        for (Gl vgl : listGV) {
            crdAmt += Util1.getDouble(vgl.getCrAmt());
            drAmt += Util1.getDouble(vgl.getDrAmt());
        }
        ttlCrdAmt.setValue(crdAmt);
        ttlDrAmt.setValue(drAmt);
    }

    public void removeJournal(int row) {
        if (!listGV.isEmpty()) {
            Gl vgl = listGV.get(row);
            if (!Util1.isNull(vgl.getKey().getGlCode())) {
                int status = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete transaction.", "Delete Transaction", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (status == JOptionPane.YES_OPTION) {
                    delList.add(vgl.getKey());
                    listGV.remove(row);
                    fireTableRowsDeleted(row, row);
                    calTotalAmt();
                    parent.setRowSelectionInterval(row, row);
                    parent.requestFocus();
                }
            }
        }
    }

}
