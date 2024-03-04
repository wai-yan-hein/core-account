/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.inventory.entity.TraderGroup;
import com.repo.InventoryRepo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class TraderGroupTableModel extends AbstractTableModel {

    static Logger log = LoggerFactory.getLogger(TraderGroupTableModel.class.getName());
    private List<TraderGroup> listGroup = new ArrayList();
    private final String[] columnNames = {"Code", "Group Name"};
    private InventoryRepo inventoryRepo;

    public TraderGroupTableModel() {
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public TraderGroupTableModel(List<TraderGroup> listGroup) {
        this.listGroup = listGroup;
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listGroup == null) {
            return null;
        }

        if (listGroup.isEmpty()) {
            return null;
        }

        try {
            TraderGroup med = listGroup.get(row);

            return switch (column) {
                case 0 ->
                    med.getUserCode();
                case 1 ->
                    med.getGroupName();
                default ->
                    null;
            }; //Code
            //Name
            //Active
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
        if (listGroup == null) {
            return 0;
        }
        return listGroup.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TraderGroup> getListGroup() {
        return listGroup;
    }

    public void setListGroup(List<TraderGroup> listGroup) {
        this.listGroup = listGroup;
        fireTableDataChanged();
    }

    public TraderGroup getGroup(int row) {
        if (listGroup != null) {
            if (!listGroup.isEmpty()) {
                return listGroup.get(row);
            }
        }
        return null;
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
