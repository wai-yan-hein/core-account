/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.user.model.VRoleCompany;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class CompanyNameTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CompanyNameTableModel.class);
    private List<VRoleCompany> listCompany = new ArrayList<>();
    private final String[] columnNames = {"Company Name"};

    public CompanyNameTableModel(List<VRoleCompany> listCompany) {
        this.listCompany = listCompany;
        fireTableDataChanged();
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
        if (listCompany == null) {
            return null;
        }

        if (listCompany.isEmpty()) {
            return null;
        }

        try {
            VRoleCompany auto = listCompany.get(row);

            switch (column) {
                case 0: //Code
                    return auto.getCompName();
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
        if (listCompany == null) {
            return 0;
        } else {
            return listCompany.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public VRoleCompany getCompany(int row) {
        if (listCompany == null) {
            return null;
        } else if (listCompany.isEmpty()) {
            return null;
        } else {
            return listCompany.get(row);
        }
    }

    public int getSize() {
        if (listCompany == null) {
            return 0;
        } else {
            return listCompany.size();
        }
    }
}
