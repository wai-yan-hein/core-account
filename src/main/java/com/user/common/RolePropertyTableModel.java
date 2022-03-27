/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.common.Global;
import com.common.ReturnObject;
import com.common.RoleProperty;
import com.common.RolePropertyKey;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RolePropertyTableModel extends AbstractTableModel {

    private List<RoleProperty> listProperty = new ArrayList();
    private final String[] columnNames = {"Propery Key", "Property Value", "Remark"};
    private WebClient userApi;
    private String roleCode;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public WebClient getWebClient() {
        return userApi;
    }

    public void setWebClient(WebClient userApi) {
        this.userApi = userApi;
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
            RoleProperty p = listProperty.get(row);
            switch (column) {
                case 0 -> {
                    return p.getKey() == null ? null : p.getKey().getPropKey();
                }
                case 1 -> {
                    return p.getPropValue();
                }
                case 2 -> {
                    return p.getRemark();
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
            RoleProperty p = listProperty.get(row);
            if (!Objects.isNull(value)) {
                switch (column) {
                    case 0 -> {
                        if (p.getKey() == null) {
                            RolePropertyKey key = new RolePropertyKey();
                            key.setPropKey(String.valueOf(value));
                            key.setRoleCode(roleCode);
                            p.setKey(key);
                        } else {
                            p.getKey().setPropKey(String.valueOf(value));
                        }
                    }
                    case 1 ->
                        p.setPropValue(String.valueOf(value));
                    case 2 ->
                        p.setRemark(String.valueOf(value));
                }
                save(p);
                fireTableRowsUpdated(row, row);
            }

        } catch (Exception e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private boolean isValidEntry(RoleProperty property) {
        boolean status = true;
        if (Objects.isNull(property.getKey())) {
            status = false;
        } else if (Objects.isNull(property.getPropValue())) {
            status = false;
        }
        return status;
    }

    private void save(RoleProperty property) {
        if (isValidEntry(property)) {
            Global.hmRoleProperty.put(property.getKey().getPropKey(), property.getPropValue());
            property.setCompCode(Global.compCode);
            addNewRow();
            Mono<RoleProperty> result = userApi.post()
                    .uri("/user/save-role-property")
                    .body(Mono.just(property), RoleProperty.class)
                    .retrieve()
                    .bodyToMono(RoleProperty.class);
            result.block(Duration.ofMinutes(1));
        }
    }

    @Override
    public int getRowCount() {
        return listProperty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<RoleProperty> getListProperty() {
        return listProperty;
    }

    public RoleProperty getProperty(int row) {
        return listProperty.get(row);
    }

    public void setListProperty(List<RoleProperty> listProperty) {
        this.listProperty = listProperty;
        fireTableDataChanged();
    }

    public void addNewRow() {
        listProperty.add(new RoleProperty());
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
