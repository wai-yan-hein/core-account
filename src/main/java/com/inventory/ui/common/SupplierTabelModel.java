/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.Trader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author winswe
 */
public class SupplierTabelModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(SupplierTabelModel.class);
    private List<Trader> listCustomer = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Active"};

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
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Trader customer = listCustomer.get(row);

            switch (column) {
                case 0: //Id
                    return customer.getUserCode();
                case 1: //Name
                    return customer.getTraderName();
                case 2:
                    return customer.isActive();
                default:
                    return null;
            }
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
        if (listCustomer == null) {
            return 0;
        }
        return listCustomer.size();
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
        return listCustomer;
    }

    public void setListCustomer(List<Trader> listCustomer) {
        this.listCustomer = listCustomer;
        fireTableDataChanged();
    }

    public Trader getCustomer(int row) {
        return listCustomer.get(row);
    }

    public void deleteCustomer(int row) {
        if (!listCustomer.isEmpty()) {
            listCustomer.remove(row);
            fireTableRowsDeleted(0, listCustomer.size());
        }

    }

    public void addCustomer(Trader customer) {
        listCustomer.add(customer);
        fireTableRowsInserted(listCustomer.size() - 1, listCustomer.size() - 1);
    }

    public void setCustomer(int row, Trader customer) {
        if (!listCustomer.isEmpty()) {
            listCustomer.set(row, customer);
            fireTableRowsUpdated(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
