/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.inventory.model.OutputCost;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wai yan
 */
public class OutputCostTabelModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(OutputCostTabelModel.class);
    private List<OutputCost> listOutputCost = new ArrayList();
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
            OutputCost outputCost = listOutputCost.get(row);

            return switch (column) {
                case 0 ->
                    String.valueOf(row + 1 + ". ");
                case 1 ->
                    outputCost.getUserCode();
                case 2 ->
                    outputCost.getName();
                case 3 ->
                    outputCost.isActive();
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
        if (listOutputCost == null) {
            return 0;
        }
        return listOutputCost.size();
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

    public List<OutputCost> getListOutputCost() {
        return listOutputCost;
    }

    public void setListOutputCost(List<OutputCost> listOutputCost) {
        this.listOutputCost = listOutputCost;
        fireTableDataChanged();
    }

    public OutputCost getCutputCost(int row) {
        return listOutputCost.get(row);
    }

    public void deleteOutputCost(int row) {
        if (!listOutputCost.isEmpty()) {
            listOutputCost.remove(row);
            fireTableRowsDeleted(0, listOutputCost.size());
        }

    }

    public void addOutputCost(OutputCost outputCost) {
        listOutputCost.add(outputCost);
        fireTableRowsInserted(listOutputCost.size() - 1, listOutputCost.size() - 1);
    }

    public void setOutputCost(int row, OutputCost outputCost) {
        if (!listOutputCost.isEmpty()) {
            listOutputCost.set(row, outputCost);
            fireTableRowsUpdated(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
