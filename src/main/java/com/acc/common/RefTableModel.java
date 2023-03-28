/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.VDescription;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class RefTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(RefTableModel.class);
    private List<VDescription> listAutoText = new ArrayList<>();
    private final String[] columnNames = {"Reference"};

    public RefTableModel() {
    }

    public List<VDescription> getListAutoText() {
        return listAutoText;
    }

    public void setListAutoText(List<VDescription> listAutoText) {
        this.listAutoText = listAutoText;
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

        try {
            if (!listAutoText.isEmpty()) {
                VDescription auto = listAutoText.get(row);

                switch (column) {
                    case 0: //Code
                        return auto == null ? null : auto.getDescription();
                    default:
                        return null;
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
        if (listAutoText == null) {
            return 0;
        } else {
            return listAutoText.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public VDescription getRemark(int row) {
        if (listAutoText == null) {
            return null;
        } else if (listAutoText.isEmpty()) {
            return null;
        } else {
            return listAutoText.get(row);
        }
    }

    public int getSize() {
        if (listAutoText == null) {
            return 0;
        } else {
            return listAutoText.size();
        }
    }

    public void addObject(VDescription t) {
        listAutoText.add(t);
    }

    public void clear() {
        listAutoText.clear();
        fireTableDataChanged();
    }
}
