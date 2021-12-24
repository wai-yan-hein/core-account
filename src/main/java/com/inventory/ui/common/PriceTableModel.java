/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class PriceTableModel extends AbstractTableModel {

    private Logger log = LoggerFactory.getLogger(PriceTableModel.class.getName());
    private List<String> listPrice = new ArrayList();
    private final String[] columnNames = {"Sale Price"};

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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            String price = listPrice.get(row);

            switch (column) {
                case 0 -> {
                    //Code
                    return price;
                }
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
        return listPrice.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String getPriceType(int row) {
        return listPrice.get(row);
    }

    public List<String> getListPrice() {
        return listPrice;
    }

    public void setListPrice(List<String> listPrice) {
        this.listPrice = listPrice;
        fireTableDataChanged();

    }

}
