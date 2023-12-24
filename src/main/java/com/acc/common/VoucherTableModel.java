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
    String[] columnNames = {"Date", "Cash / Bank", "Voucher", "Description", "Refrence", "Narration", "From / To", "For", "Vou Type", "Debit", "Credit"};
    private JTable parent;
    private int size;
    private double drAmt;
    private double crAmt;

    public int getSize() {
        return size;
    }

    public double getCrAmt() {
        return crAmt;
    }

    public double getDrAmt() {
        return drAmt;
    }

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
            case 1->
                gv.getSrcAccName();
            case 2 ->
                gv.getGlVouNo();
            case 3 ->
                gv.getDescription();
            case 4 ->
                gv.getReference();
            case 5 ->
                gv.getNarration();
            case 6 ->
                gv.getFromDes();
            case 7 ->
                gv.getForDes();
            case 8 ->
                gv.getTranSource().equals("DR") ? "Payment / Debit" : "Receipt / Credit";
            case 9 ->
                Util1.getDouble(gv.getDrAmt()) == 0 ? null : gv.getDrAmt();
            case 10 ->
                Util1.getDouble(gv.getCrAmt()) == 0 ? null : gv.getCrAmt();
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
        switch (column) {
            case 9, 10 -> {
                return Double.class;
            }
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

    public void addObject(Gl t) {
        listGV.add(t);
        size += 1;
        drAmt += t.getDrAmt();
        crAmt += t.getCrAmt();
        int lastIndex = listGV.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        listGV.clear();
        size = 0;
        drAmt = 0;
        crAmt = 0;
        fireTableDataChanged();
    }

}
