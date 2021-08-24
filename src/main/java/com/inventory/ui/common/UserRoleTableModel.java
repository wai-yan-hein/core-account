/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.common.Global;
import com.inventory.common.Util1;
import com.inventory.model.SaleHis;
import com.inventory.model.UserRole;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author winswe
 */
@Component
@Slf4j
public class UserRoleTableModel extends AbstractTableModel {

    private List<UserRole> listRole = new ArrayList();
    private String[] columnNames = {"Role Name"};
    private JTable table;
    @Autowired
    private WebClient webClient;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
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
        return String.class;

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            UserRole user = listRole.get(row);

            return switch (column) {
                case 0 ->
                    user.getRoleName();
                default ->
                    null;
            }; //Id
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            UserRole user = listRole.get(row);
            if (value != null) {
                switch (column) {
                    case 0 ->
                        user.setRoleName(value.toString());
                }
            }
            save(user);
        } catch (Exception e) {
            log.error("Set Value At :" + e.getMessage());
        }

    }

    private void save(UserRole user) {
        boolean hasMenu = false;
        try {
            if (user.getRoleCode() != null) {
                user.setUpdatedBy(Global.loginUser);
                hasMenu = true;
            } else {
                user.setCompCode(Global.compCode);
                user.setMacId(Global.machineId);
                user.setCreatedBy(Global.loginUser);
                user.setCreatedDate(Util1.getTodayDate());
            }
            Mono<UserRole> result = webClient.post()
                    .uri("/user/save-role")
                    .body(Mono.just(user), SaleHis.class)
                    .retrieve()
                    .bodyToMono(UserRole.class);
            result.subscribe((t) -> {
                if (t != null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                    addEmptyRow();
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            });
        } catch (Exception e) {
            log.error("Save User :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Can't save user role.");
        }
    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            UserRole user = new UserRole();
            addRole(user);
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listRole.isEmpty() || listRole == null) {
            status = true;
        } else {
            UserRole user = listRole.get(listRole.size() - 1);
            if (user.getRoleCode() == null) {
                status = false;
            }
        }

        return status;

    }

    @Override
    public int getRowCount() {
        if (listRole == null) {
            return 0;
        }
        return listRole.size();
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

    public List<UserRole> getListRole() {
        return listRole;
    }

    public void setListRole(List<UserRole> listRole) {
        this.listRole = listRole;
        fireTableDataChanged();
    }

    public UserRole getRole(int row) {
        return listRole.get(row);
    }

    public void deleteRole(int row) {
        if (!listRole.isEmpty()) {
            String roleCode = getRole(row).getRoleCode();
            if (roleCode != null) {
                listRole.remove(row);
                fireTableRowsDeleted(row, row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
            }
        }
    }

    public void addRole(UserRole user) {
        listRole.add(user);
        fireTableRowsInserted(listRole.size() - 1, listRole.size() - 1);
    }

    public void setRole(int row, UserRole user) {
        if (!listRole.isEmpty()) {
            listRole.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

}
