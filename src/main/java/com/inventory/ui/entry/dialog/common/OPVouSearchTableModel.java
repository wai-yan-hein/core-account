/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.model.OPHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OPVouSearchTableModel extends AbstractTableModel {

    private List<OPHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Opening Date", "Vou No", "Location", "Remark", "Created By"};
    private JTable parent;

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return column == 4 ? Float.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OPHis his = listDetail.get(row);

            switch (column) {
                case 0 -> {
                    //date
                    return Util1.toDateStr(his.getVouDate(), "dd/MM/yyyy");
                }
                case 1 -> {
                    //vou-no
                    return his.getVouNo();
                }
                case 2 -> {
                    //location
                    return his.getLocation().getLocationName();
                }
                case 3 -> {
                    //remark
                    return his.getRemark();
                }
                case 4 -> {
                    //user
                    return null;
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<OPHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<OPHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public OPHis getSelectVou(int row) {
        return listDetail.get(row);
    }
}
