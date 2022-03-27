/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.VTranSource;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class TranSourceTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(TranSourceTableModel.class);
    private List<VTranSource> listAutoText = new ArrayList<>();
    private final String[] columnNames = {"Transource"};

    public TranSourceTableModel(List<VTranSource> listAutoText) {
        this.listAutoText = listAutoText;
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
                VTranSource auto = listAutoText.get(row);

                return switch (column) {
                    case 0 ->
                        auto == null ? null : auto.getTranSource();
                    default ->
                        null;
                }; //Code
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

    public VTranSource getRemark(int row) {
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
}
