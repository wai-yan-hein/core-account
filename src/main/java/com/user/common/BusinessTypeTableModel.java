/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.repo.UserRepo;
import com.repo.AccountRepo;
import com.acc.model.BusinessType;
import com.acc.model.COATemplate;
import com.acc.model.COATemplateKey;
import com.common.Util1;
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
public class BusinessTypeTableModel extends AbstractTableModel {

    private List<BusinessType> list = new ArrayList();
    private final String[] columnNames = {"Id", "Business Name"};
    private UserRepo userRepo;
    private AccountRepo accountRepo;
    private JTable table;
    private boolean edit;

    public BusinessTypeTableModel(boolean edit) {
        this.edit = edit;
    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public UserRepo getUserRepo() {
        return userRepo;
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
        if (edit) {
            return column == 1;
        }
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            BusinessType p = list.get(row);
            switch (column) {
                case 0 -> {
                    return p.getBusId().toString();
                }
                case 1 -> {
                    return p.getBusName();
                }
            }
        } catch (Exception e) {
            log.error(String.format("getValueAt : %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            BusinessType obj = list.get(row);
            if (value != null) {
                switch (column) {
                    case 1 ->
                        obj.setBusName(Util1.getString(value));

                }
                fireTableRowsUpdated(row, row);
                save(obj, row);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void save(BusinessType obj, int row) {
        userRepo.save(obj).subscribe((t) -> {
            obj.setBusId(t.getBusId());
            saveCOAHead(t.getBusId());
            addNewRow();
            table.setRowSelectionInterval(row + 1, row + 1);
            table.setColumnSelectionInterval(0, 0);
            table.requestFocus();
        });
    }

    private void saveCOAHead(Integer busId) {
        accountRepo.getCOAChild("#")
                .collectList()
                .subscribe((t) -> {
                    t.forEach((coa) -> {
                        COATemplate tmp = new COATemplate();
                        COATemplateKey key = new COATemplateKey();
                        key.setBusId(busId);
                        key.setCoaCode(coa.getKey().getCoaCode());
                        tmp.setKey(key);
                        tmp.setCoaNameEng(coa.getCoaNameEng());
                        tmp.setActive(coa.isActive());
                        tmp.setCoaCodeUsr(coa.getCoaCodeUsr());
                        tmp.setCoaLevel(coa.getCoaLevel());
                        tmp.setCoaParent(coa.getCoaParent());
                        accountRepo.save(tmp).subscribe((tt) -> {
                            log.info("saved.");
                        });
                    });
                });
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<BusinessType> getList() {
        return list;
    }

    public void setList(List<BusinessType> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (list.isEmpty() || list == null) {
            status = true;
        } else {
            BusinessType t = list.get(list.size() - 1);
            if (t.getBusId() == null) {
                status = false;
            }
        }

        return status;
    }

    public BusinessType getObject(int row) {
        return list.get(row);
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            list.add(new BusinessType());
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }

    public void delete(int row) {
        list.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
