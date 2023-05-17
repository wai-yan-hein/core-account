/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.COATemplate;
import com.acc.model.COATemplateKey;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
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
    private Integer busId;

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
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
        return switch (column) {
            case 0 ->
                true;
            case 1 ->
                true;
            default ->
                true;
        };
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
                obj.setCoaLevel(1);
                obj.getKey().setBusId(busId);
                obj.setCoaParent("#");
                save(obj, row);
                fireTableRowsUpdated(row, row);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public boolean isValidCOA(COATemplate coa) {
        boolean status = true;
        if (Util1.isNull(coa.getCoaNameEng())) {
            status = false;
        } else if (Util1.isNull(coa.getKey().getCoaCode())) {
            status = false;
        } else if (Util1.isNull(coa.getCoaLevel())) {
            status = false;
        } else {
            coa.setActive(true);
        }
        return status;
    }

    private void save(COATemplate obj, int row) {
        if (isValidCOA(obj)) {
            accountRepo.save(obj).subscribe((t) -> {
                obj.setKey(t.getKey());
                list.set(row, t);
                addEmptyRow();
            }, (e) -> {
                log.error(e.getMessage());
            });
        }

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

    private boolean hasEmptyRow() {
        boolean valid = true;
        if (list.isEmpty() || list == null) {
            valid = true;
        } else {
            COATemplate coa = list.get(list.size() - 1);
            if (coa.getKey().getCoaCode() == null) {
                valid = false;
            }
        }

        return valid;
    }

    public void addEmptyRow() {
        if (list != null) {
            if (hasEmptyRow()) {
                COATemplate coa = new COATemplate();
                COATemplateKey key = new COATemplateKey();
                coa.setKey(key);
                list.add(coa);
                fireTableRowsInserted(list.size() - 1, list.size() - 1);
            }
        }

    }
}
