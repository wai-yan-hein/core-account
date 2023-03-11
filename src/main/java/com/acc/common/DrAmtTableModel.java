/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.Gl;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author winswe
 */
@Slf4j
public class DrAmtTableModel extends AbstractTableModel {

    private List<Gl> listVGl = new ArrayList();
    private String[] columnNames = {"Date", "Dep :", "Description", "Ref", "No :", "Dr-Amt"};
    private double drAmt;

    public double getDrAmt() {
        return drAmt;
    }

    public void setDrAmt(double drAmt) {
        this.drAmt = drAmt;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
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

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Gl apar = listVGl.get(row);

            return switch (column) {
                case 0 ->
                    Util1.toDateStr(apar.getGlDate(), "dd/MM/yyyy");
                case 1 ->
                    apar.getDeptUsrCode();
                case 2 ->
                    apar.getDescription();
                case 3 ->
                    apar.getReference();
                case 4 ->
                    apar.getRefNo();
                case 5 ->
                    apar.getDrAmt();
                default ->
                    null;
            }; //date
            //dep
            //des
            //ref
            //dr-amt
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listVGl == null) {
            return 0;
        }
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
            fireTableRowsDeleted(0, listVGl.size());
        }

    }

    public void addVGl(Gl apar) {
        listVGl.add(apar);
        drAmt += Util1.getDouble(apar.getDrAmt());
        //fireTableRowsInserted(listVGl.size() - 1, listVGl.size() - 1);
    }

    public void setVGl(int row, Gl apar) {
        if (!listVGl.isEmpty()) {
            listVGl.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        if (listVGl != null) {
            listVGl.clear();
            drAmt = 0;
            fireTableDataChanged();
        }
    }

}
