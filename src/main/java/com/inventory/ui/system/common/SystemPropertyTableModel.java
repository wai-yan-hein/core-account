/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.system.common;

import com.inventory.common.Global;
import com.inventory.common.ReturnObject;
import com.inventory.model.SysProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SystemPropertyTableModel extends AbstractTableModel {

    private List<SysProperty> listProperty = new ArrayList();
    private final String[] columnNames = {"Propery Key", "Property Value", "Remark"};
    private WebClient webClient;

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
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
            SysProperty p = listProperty.get(row);
            switch (column) {
                case 0 -> {
                    return p.getPropKey();
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
            SysProperty p = listProperty.get(row);
            if (!Objects.isNull(value)) {
                switch (column) {
                    case 0 ->
                        p.setPropKey(String.valueOf(value));
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

    private boolean isValidEntry(SysProperty property) {
        boolean status = true;
        if (Objects.isNull(property.getPropKey())) {
            status = false;
        } else if (Objects.isNull(property.getPropValue())) {
            status = false;
        }
        return status;
    }

    private void save(SysProperty property) {
        if (isValidEntry(property)) {
            property.setCompCode(Global.compCode);
            addNewRow();
            Mono<ReturnObject> result = webClient.post()
                    .uri("/setup/save-system-property")
                    .body(Mono.just(property), SysProperty.class)
                    .retrieve()
                    .bodyToMono(ReturnObject.class);
            result.subscribe((t) -> {
            }, (e) -> {
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            });
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

    public List<SysProperty> getListProperty() {
        return listProperty;
    }

    public void setListProperty(List<SysProperty> listProperty) {
        this.listProperty = listProperty;
        fireTableDataChanged();
    }

    public void addNewRow() {
        listProperty.add(new SysProperty());
        fireTableRowsInserted(listProperty.size() - 1, listProperty.size() - 1);
    }

}
