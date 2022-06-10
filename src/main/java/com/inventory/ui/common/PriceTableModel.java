/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.PriceOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PriceTableModel extends AbstractTableModel {

    private List<PriceOption> listPrice = new ArrayList();
    private final String[] columnNames = {"Type", "Description", "Price"};

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
        return column == 2 ? Float.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            PriceOption price = listPrice.get(row);

            switch (column) {
                case 0 -> {
                    //Code
                    return price.getPriceType();
                }
                case 1 -> {
                    return price.getDescription();
                }
                case 2 -> {
                    return price.getPrice();
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

    public List<PriceOption> getListPrice() {
        return listPrice;
    }

    public void setListPrice(List<PriceOption> listPrice) {
        this.listPrice = listPrice;
        fireTableDataChanged();
    }

    public PriceOption getPriceOption(int row) {
        return listPrice.get(row);
    }

}
