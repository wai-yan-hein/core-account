/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.MachineInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class MachineInfoCompleterTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationCompleterTableModel.class);
    private List<MachineInfo> listMachine = new ArrayList();
    private final String[] columnNames = {"Name"};

    public MachineInfoCompleterTableModel(List<MachineInfo> listMach) {
        this.listMachine = listMach;
    }

    @Override
    public int getRowCount() {
        if (listMachine == null) {
            return 0;
        }
        return listMachine.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            MachineInfo mach = listMachine.get(row);

            switch (column) {
                case 0: //Name
                    return mach.getMachineName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
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
        switch (column) {
            case 0:
                return String.class;
            default:
                return Object.class;
        }

    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public MachineInfo getMachineInfo(int row) {
        return listMachine.get(row);
    }

}
