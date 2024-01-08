/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.repo.UserRepo;
import com.common.Global;
import com.common.Util1;
import com.user.model.DateLock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class DateLockTableModel extends AbstractTableModel {

    private List<DateLock> list = new ArrayList();
    private final String[] columnNames = {"Remark", "Start Date", "End Date", "Lock"};
    private UserRepo userRepo;
    private JTable table;

    public void setTable(JTable table) {
        this.table = table;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return column == 3 ? Boolean.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            DateLock p = list.get(row);
            switch (column) {
                case 0 -> {
                    return p.getRemark();
                }
                case 1 -> {
                    LocalDate d = p.getStartDate();
                    return d == null ? null : Util1.toDateStr(d, Global.dateFormat);
                }
                case 2 -> {
                    LocalDate d = p.getEndDate();
                    return d == null ? null : Util1.toDateStr(d, Global.dateFormat);
                }
                case 3 -> {
                    return p.isDateLock();
                }
            }
        } catch (Exception e) {
            log.error(String.format("getValueAt : %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<DateLock> getList() {
        return list;
    }

    public void setList(List<DateLock> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (list.isEmpty() || list == null) {
            status = true;
        } else {
            DateLock t = list.get(list.size() - 1);
            if (t.getKey().getLockCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public DateLock getObject(int row) {
        return list.get(row);
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            list.add(new DateLock());
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }

    public void delete(int row) {
        list.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void addObject(DateLock info) {
        list.add(info);
        fireTableRowsInserted(list.size() - 1, list.size() - 1);
    }

    public void setObject(int row, DateLock info) {
        if (!list.isEmpty()) {
            list.set(row, info);
            fireTableRowsUpdated(row, row);
        }
    }

}
