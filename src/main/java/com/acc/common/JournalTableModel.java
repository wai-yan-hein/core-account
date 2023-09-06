/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.Gl;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class JournalTableModel extends AbstractTableModel {

    private List<Gl> listGV = new ArrayList();
    private final String[] columnNames = {"Date", "Voucher No", "Description", "Refrence", "Project No", "Amount", "Type"};
    private JTable parent;

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
                    Util1.toDateStr(gv.getGlDate(), "dd/MM/yyyy");
                case 1 ->
                    gv.getGlVouNo();
                case 2 ->
                    gv.getDescription();
                case 3 ->
                    gv.getReference();
                case 4 ->
                    gv.getProjectNo();
                case 5 ->
                    gv.getDrAmt() == 0 ? null : gv.getDrAmt();
                case 6 ->
                    getType(gv.getTranSource());
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

    private String getType(String tranSource) {
        return tranSource.equals("GV") ? "Journal" : "Conversion";
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public Gl getGl(int row) {
        return listGV.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 5) {
            return Double.class;
        }
        return String.class;
    }

    public List<Gl> getListGV() {
        return listGV;
    }

    public void setListGV(List<Gl> listGV) {
        this.listGV = listGV;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;

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

    public int getListSize() {
        return listGV.size();
    }

    public void delete(int row) {
        if (!listGV.isEmpty()) {
            listGV.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
}
