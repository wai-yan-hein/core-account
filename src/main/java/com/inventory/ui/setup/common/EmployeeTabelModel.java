/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.inventory.model.Trader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wai yan
 */
public class EmployeeTabelModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(EmployeeTabelModel.class);
    private List<Trader> listEmployee = new ArrayList();
    private String[] columnNames = {"No.", "Code", "Name", "Active"};

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
            case 3 ->
                Boolean.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Trader customer = listEmployee.get(row);

            return switch (column) {
                case 0 ->
                    String.valueOf(row + 1 + ". ");
                case 1 ->
                    customer.getUserCode();
                case 2 ->
                    customer.getTraderName();
                case 3 ->
                    customer.isActive();
                default ->
                    null;
            }; //Id
            //Name
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
        if (listEmployee== null) {
            return 0;
        }
        return listEmployee.size();
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

    public List<Trader> getListCustomer() {
        return listEmployee;
    }

    public void setListEmployee(List<Trader> listEmployee) {
        this.listEmployee = listEmployee;
        fireTableDataChanged();
    }

    public Trader getEmployee(int row) {
        return listEmployee.get(row);
    }

    public void deleteEmployee(int row) {
        if (!listEmployee.isEmpty()) {
            listEmployee.remove(row);
            fireTableRowsDeleted(0, listEmployee.size());
        }

    }

    public void addEmployee(Trader customer) {
        listEmployee.add(customer);
        fireTableRowsInserted(listEmployee.size() - 1, listEmployee.size() - 1);
    }

    public void setEmployee(int row, Trader customer) {
        if (!listEmployee.isEmpty()) {
            listEmployee.set(row, customer);
            fireTableRowsUpdated(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
