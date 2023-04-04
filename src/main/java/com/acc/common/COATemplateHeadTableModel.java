/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.COATemplate;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class COATemplateHeadTableModel extends AbstractTableModel {

    private List<COATemplate> list = new ArrayList();
    private final String[] columnNames = {"Code", "Name"};
    private AccountRepo accountRepo;

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
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
        return switch (column) {
            case 0 ->
                String.class;
            case 1 ->
                String.class;
            default ->
                Object.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            COATemplate coa = list.get(row);

            return switch (column) {
                case 0 ->
                    coa.getKey().getCoaCode();
                case 1 ->
                    coa.getCoaNameEng();
                default ->
                    null;
            }; //Code
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            COATemplate obj = list.get(row);
            if (value != null) {
                switch (column) {
                    case 0 ->
                        obj.getKey().setCoaCode(Util1.getString(value));
                    case 1 ->
                        obj.setCoaNameEng(Util1.getString(value));
                }
                save(obj);
                fireTableRowsUpdated(row, row);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void save(COATemplate obj) {
        accountRepo.save(obj).subscribe((t) -> {
            obj.setKey(t.getKey());
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    public COATemplate getCOATemplate(int row) {
        return list.get(row);
    }

    public List<COATemplate> getList() {
        return list;
    }

    public void setList(List<COATemplate> list) {
        this.list = list;
        fireTableDataChanged();
    }

}
