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

/**
 *
 * @author MyoGyi
 */
public class VoucherTableModel extends AbstractTableModel {

    private List<Gl> listGV = new ArrayList();
    String[] columnNames = {"Date", "Voucher", "Description", "Refrence", "Ref No", "Vou Type", "Amount"};
    private JTable parent;

    @Override
    public int getRowCount() {
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
                gv.getRefNo();
            case 5 ->
                gv.getTranSource().equals("DR") ? "Payment / Debit" : "Receipt / Credit";
            case 6 ->
                gv.getTranSource().equals("DR") ? gv.getCrAmt() : gv.getDrAmt();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public Gl getVGl(int row) {
        return listGV.get(row);
    }

    @Override
    public Class getColumnClass(int column) {
        return column == 6 ? Double.class : String.class;
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

    public Gl getVoucher(int row) {
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

    public void remove(int row) {
        if (!listGV.isEmpty()) {
            listGV.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public int getListSize() {
        return listGV.size();
    }

}
