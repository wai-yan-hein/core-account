/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.inventory.entity.MessageType;
import com.repo.UserRepo;
import com.user.model.PrivilegeCompany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RoleCompanyTableModel extends AbstractTableModel {

    private List<PrivilegeCompany> listProperty = new ArrayList();
    private final String[] columnNames = {"Company Name", "Allow"};
    @Setter
    private UserRepo userRepo;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Class getColumnClass(int column) {
        return column == 1 ? Boolean.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            PrivilegeCompany p = listProperty.get(row);
            switch (column) {
                case 0 -> {
                    return p.getCompName();
                }
                case 1 -> {
                    return p.isAllow();
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
            PrivilegeCompany p = listProperty.get(row);
            if (!Objects.isNull(value)) {
                switch (column) {
                    case 1 -> {
                        if (value instanceof Boolean active) {
                            log.info(active + "");
                            p.setAllow(active);
                        }
                    }
                }
                save(p);
                fireTableRowsUpdated(row, row);
            }

        } catch (Exception e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private void save(PrivilegeCompany property) {
        userRepo.savePrivilegeCompany(property).doOnSuccess((t) -> {
            sendMessage(t.getCompName());
        }).subscribe();
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.PRIVILEGE_COMPANY, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    @Override
    public int getRowCount() {
        return listProperty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PrivilegeCompany> getListProperty() {
        return listProperty;
    }

    public PrivilegeCompany getProperty(int row) {
        return listProperty.get(row);
    }

    public void setListProperty(List<PrivilegeCompany> listProperty) {
        this.listProperty = listProperty;
        fireTableDataChanged();
    }

    public void addNewRow() {
        listProperty.add(new PrivilegeCompany());
        fireTableRowsInserted(listProperty.size() - 1, listProperty.size() - 1);
    }

    public void delete(int row) {
        listProperty.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clear() {
        listProperty.clear();
        fireTableDataChanged();
    }

}
