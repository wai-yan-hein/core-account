/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author winswe
 */
public class ChartOfAccountImportTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(ChartOfAccountImportTableModel.class);
    private List<ChartOfAccount> listCOA = new ArrayList();
    private String[] columnNames = {"Code", "User Code", "Name", "Parent Code", "Option", "Company Id", "Active"};

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
            case 6:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            ChartOfAccount coa = listCOA.get(row);

            switch (column) {
                case 0: //Id
                    return coa.getMigCode();
                case 1: //Name
                    return coa.getCoaCodeUsr();
                case 2: //Name
                    return coa.getCoaNameEng();
                case 3:
                    return coa.getCoaParent();
                case 4:
                    return coa.getOption();
                case 5:
                    return coa.getCompCode();
                case 6:
                    return coa.isActive();
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
        if (listCOA == null) {
            return 0;
        }
        return listCOA.size();
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

    public List<ChartOfAccount> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<ChartOfAccount> listCOA) {
        this.listCOA = listCOA;
        fireTableDataChanged();
    }

    public void clear() {
        if (listCOA != null) {
            listCOA.clear();
            fireTableDataChanged();
        }
    }

}
