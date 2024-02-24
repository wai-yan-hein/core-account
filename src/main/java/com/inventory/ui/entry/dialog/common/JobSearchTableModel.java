/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.model.Job;
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
public class JobSearchTableModel extends AbstractTableModel {

    private List<Job> listDetail = new ArrayList();
    private final String[] columnNames = {"Job No", "Job Name", "Start Date", "End Date"};

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
        if (listDetail == null) {
            return 0;
        }
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                Job his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //batch
                        return his.getKey().getJobNo();
                    }
                    case 1 -> {
                        //sup
                        return his.getJobName();
                    }
                    case 2 -> {
                        return Util1.toDateStr(his.getStartDate(), "dd/MM/yyyy");
                    }
                    case 3 -> {
                        return Util1.toDateStr(his.getEndDate(), "dd/MM/yyyy");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<Job> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<Job> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public Job getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(Job g) {
        listDetail.add(g);
    }

    public int getSize() {
        return listDetail.size();
    }
}
